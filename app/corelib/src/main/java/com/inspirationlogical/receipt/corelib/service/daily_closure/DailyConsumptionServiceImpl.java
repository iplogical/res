package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosureNew;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.model.view.DailyConsumptionModel;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRowModel;
import com.inspirationlogical.receipt.corelib.params.CloseDayParams;
import com.inspirationlogical.receipt.corelib.repository.DailyClosureNewRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.VATSerieRepository;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class DailyConsumptionServiceImpl implements DailyConsumptionService {

    final private static Logger logger = LoggerFactory.getLogger(DailyConsumptionServiceImpl.class);

    @Autowired
    private DailyClosureService dailyClosureService;

    @Autowired
    private DailyClosureNewRepository dailyClosureNewRepository;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private VATSerieRepository vatSerieRepository;

    private int getTotalSalePrice(ReceiptRecord receiptRecord) {
        return (int) (receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity());
    }

    @Override
    public void printAggregatedConsumption(DailyConsumptionModel dailyConsumptionModel) {
        logger.info("The aggregated consumption was printed between: " + dailyConsumptionModel.getStartTime() +
                " - " + dailyConsumptionModel.getEndTime());
        receiptService.printAggregatedReceipt(dailyConsumptionModel);
    }


    @Override
    @Transactional(propagation = Propagation.NEVER)
    public DailyConsumptionModel getDailyConsumptionModel(LocalDate startDate, LocalDate endDate) {
        DailyConsumptionModel dailyConsumptionModel = new DailyConsumptionModel();
        List<LocalDateTime> closureTimes = dailyClosureService.getClosureTimes(startDate, endDate);
        dailyConsumptionModel.setStartTime(closureTimes.get(0));
        dailyConsumptionModel.setEndTime(closureTimes.get(1));

        List<Receipt> receipts = receiptRepository.getReceiptsByClosureTime(closureTimes.get(0), closureTimes.get(1));
        setConsumptionFields(dailyConsumptionModel, receipts);
        setDiscountFields(dailyConsumptionModel, receipts);
        List<ReceiptRecordView> reducedSoldProducts = getReducedSoldProducts(receipts);
        dailyConsumptionModel.setSoldProducts(reducedSoldProducts);
        dailyConsumptionModel.getSoldProducts().sort(Comparator.comparing(ReceiptRecordView::getSoldQuantity).reversed());
        return dailyConsumptionModel;
    }

    private ReceiptRecordView mergeEquivalentRecords(ReceiptRecordView recordA, ReceiptRecordView recordB) {
        double sumSoldQuantity = recordA.getSoldQuantity() + recordB.getSoldQuantity();
        List<LocalDateTime> created = new ArrayList<>();
        created.addAll(recordA.getCreated());
        created.addAll(recordB.getCreated());
        return ReceiptRecordView.builder()
                .id(recordA.getId())
                .name(recordA.getName())
                .type(recordA.getType())
                .soldQuantity(sumSoldQuantity)
                .purchasePrice(recordA.getPurchasePrice())
                .salePrice(recordA.getSalePrice())
                .totalPrice((int) Math.round(recordA.getSalePrice() * sumSoldQuantity))
                .discountPercent(recordA.getDiscountPercent())
                .vat(recordA.getVat())
                .created(created)
                .isPartiallyPayable(recordA.isPartiallyPayable())
                .ownerStatus(recordA.getOwnerStatus())
                .family(recordA.getFamily())
                .build();
    }

    private void setConsumptionFields(DailyConsumptionModel dailyConsumptionModel, List<Receipt> receipts) {
        dailyConsumptionModel.setOpenConsumption(getOpenConsumption());
        dailyConsumptionModel.setConsumptionCash(getConsumptionOfTheDay(receipts, PaymentMethod.CASH));
        dailyConsumptionModel.setConsumptionCreditCard(getConsumptionOfTheDay(receipts, PaymentMethod.CREDIT_CARD));
        dailyConsumptionModel.setConsumptionCoupon(getConsumptionOfTheDay(receipts, PaymentMethod.COUPON));
        dailyConsumptionModel.setServiceFeeCash(getServiceFeeOfTheDay(receipts, PaymentMethod.CASH));
        int vatPercent = getVatPercent();
        dailyConsumptionModel.setServiceFeeNetCash(calculateNetServiceFee(dailyConsumptionModel.getServiceFeeCash(), vatPercent));
        dailyConsumptionModel.setServiceFeeCreditCard(getServiceFeeOfTheDay(receipts, PaymentMethod.CREDIT_CARD));
        dailyConsumptionModel.setServiceFeeNetCreditCard(calculateNetServiceFee(dailyConsumptionModel.getServiceFeeCreditCard(), vatPercent));
        dailyConsumptionModel.setServiceFeeCoupon(getServiceFeeOfTheDay(receipts, PaymentMethod.COUPON));
        dailyConsumptionModel.setServiceFeeTotal(getServiceFeeOfTheDay(receipts));
        dailyConsumptionModel.setServiceFeeNetTotal(getNetServiceFee(receipts));
        dailyConsumptionModel.setTotalConsumption(dailyConsumptionModel.getOpenConsumption() +
                dailyConsumptionModel.getConsumptionCash() +
                dailyConsumptionModel.getConsumptionCreditCard() +
                dailyConsumptionModel.getConsumptionCoupon() +
                dailyConsumptionModel.getServiceFeeTotal());
    }

    public int getOpenConsumption() {
        List<Receipt> openReceipts = receiptRepository.getAllOpenReceipts();
        return openReceipts.stream().map(Receipt::getRecords).flatMap(Collection::stream).mapToInt(this::getTotalSalePrice).sum();
    }

    private int getConsumptionOfTheDay(List<Receipt> receipts, PaymentMethod paymentMethod) {
        int totalConsumption = (int) receipts.stream()
                .filter(receipt -> receipt.getPaymentMethod().equals(paymentMethod))
                .map(Receipt::getRecords).flatMap(Collection::stream)
                .filter(receiptRecord -> !receiptRecord.getProduct().getType().equals(ProductType.SERVICE_FEE_PRODUCT))
                .mapToDouble(receiptRecord -> receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity())
                .sum();
        int tableDiscount = getTableDiscount(receipts, paymentMethod);
        return totalConsumption - tableDiscount;
    }

    private int getServiceFeeOfTheDay(List<Receipt> receipts) {
        return getServiceFeeOfTheDay(receipts, PaymentMethod.CASH) +
                getServiceFeeOfTheDay(receipts, PaymentMethod.CREDIT_CARD) +
                getServiceFeeOfTheDay(receipts, PaymentMethod.COUPON);
    }

    private int getServiceFeeOfTheDay(List<Receipt> receipts, PaymentMethod paymentMethod) {
        return (int) receipts.stream()
                .filter(receipt -> receipt.getPaymentMethod().equals(paymentMethod))
                .map(Receipt::getRecords).flatMap(Collection::stream)
                .filter(receiptRecord -> receiptRecord.getProduct().getType().equals(ProductType.SERVICE_FEE_PRODUCT))
                .mapToDouble(receiptRecord -> receiptRecord.getSalePrice() * receiptRecord.getSoldQuantity())
                .sum();
    }

    private int getNetServiceFee(List<Receipt> receipts) {
        int vatPercent = getVatPercent();
        int grossCashServiceFee = getServiceFeeOfTheDay(receipts, PaymentMethod.CASH);
        int netCashServiceFee = calculateNetServiceFee(grossCashServiceFee, vatPercent);

        int grossCreditCardServiceFee = getServiceFeeOfTheDay(receipts, PaymentMethod.CREDIT_CARD);
        int netCreditCardServiceFee = calculateNetServiceFee(grossCreditCardServiceFee, vatPercent);

        int netCouponServiceFee = getServiceFeeOfTheDay(receipts, PaymentMethod.COUPON);
        return netCashServiceFee + netCreditCardServiceFee + netCouponServiceFee;
    }

    private int getVatPercent() {
        return (int) vatSerieRepository.findFirstByStatus(VATStatus.VALID).getVat().stream()
                .filter(vat -> vat.getName().equals(VATName.NORMAL)).collect(toList()).get(0).getVAT();
    }

    private int calculateNetServiceFee(double grossServiceFee, double vatPercent) {
        return (int) Math.round((grossServiceFee / (100D + vatPercent)) * 100D);
    }

    private int getTableDiscount(List<Receipt> receipts, PaymentMethod paymentMethod) {
        return receipts.stream()
                .filter(receipt -> receipt.getPaymentMethod().equals(paymentMethod))
                .mapToInt(receipt -> receipt.getSumSaleGrossOriginalPrice() - receipt.getSumSaleGrossPrice()).sum();
    }

    private void setDiscountFields(DailyConsumptionModel dailyConsumptionModel, List<Receipt> receipts) {
        dailyConsumptionModel.setProductDiscount(getProductDiscount(receipts));
        dailyConsumptionModel.setTableDiscount(getTableDiscount(receipts));
        dailyConsumptionModel.setTotalDiscount(dailyConsumptionModel.getProductDiscount() + dailyConsumptionModel.getTableDiscount());
    }

    private int getProductDiscount(List<Receipt> receipts) {
        return (int) Math.round(receipts.stream().map(Receipt::getRecords).flatMap(Collection::stream)
                .mapToDouble(receiptRecord ->
                        (receiptRecord.getOriginalSalePrice() - receiptRecord.getSalePrice()) * receiptRecord.getSoldQuantity())
                .sum());
    }

    private int getTableDiscount(List<Receipt> receipts) {
        return receipts.stream()
                .mapToInt(receipt -> receipt.getSumSaleGrossOriginalPrice() - receipt.getSumSaleGrossPrice()).sum();
    }

    private List<ReceiptRecordView> getReducedSoldProducts(List<Receipt> receipts) {
        List<ReceiptRecordView> soldProducts = getSoldProducts(receipts);

        Map<String, Map<Double, Optional<ReceiptRecordView>>> reducedSoldProductsMap =
                soldProducts.stream().collect(Collectors.groupingBy(ReceiptRecordView::getName,
                        Collectors.groupingBy(ReceiptRecordView::getDiscountPercent, Collectors.reducing(this::mergeEquivalentRecords))));

        List<Optional<ReceiptRecordView>> reducedOptionalSoldPrducts =
                reducedSoldProductsMap.values().stream().map(Map::entrySet).flatMap(Collection::stream).map(Map.Entry::getValue).collect(Collectors.toList());

        return reducedOptionalSoldPrducts.stream().map(Optional::get).collect(Collectors.toList());
    }

    private List<ReceiptRecordView> getSoldProducts(List<Receipt> receipts) {
        return receipts.stream()
                .map(Receipt::getRecords)
                .flatMap(Collection::stream)
                .filter(receiptRecord -> !receiptRecord.getProduct().getType().equals(ProductType.SERVICE_FEE_PRODUCT))
                .map(ReceiptRecordView::new)
                .collect(toList());
    }

    @Override
    public List<ReceiptRowModel> getReceipts(LocalDate startDate, LocalDate endDate) {
        List<LocalDateTime> closureTimes = dailyClosureService.getClosureTimes(startDate, endDate);
        List<Receipt> receipts = receiptRepository.getReceiptsByClosureTime(closureTimes.get(0), closureTimes.get(1));
        return receipts.stream()
                .map(this::buildReceiptRowModel)
                .sorted(Comparator.comparing(ReceiptRowModel::getReceiptClosureTime))
                .collect(toList());
    }

    private ReceiptRowModel buildReceiptRowModel(Receipt receipt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM.dd HH:mm:ss");
        return ReceiptRowModel.builder()
                .receiptId(String.valueOf(receipt.getId()))
                .receiptTotalPrice(String.valueOf(receipt.getSumSaleGrossPrice()))
                .receiptPaymentMethod(receipt.getPaymentMethod().toI18nString())
                .receiptOpenTime(receipt.getOpenTime().format(dtf))
                .receiptClosureTime(receipt.getClosureTime().format(dtf))
                .build();
    }

    @Override
    public void updatePaymentMethod(int receiptId, PaymentMethod paymentMethod) {
        Receipt receipt = receiptRepository.findById(receiptId);
        receipt.setPaymentMethod(paymentMethod);
        receiptRepository.save(receipt);
    }

    @Override
    public void closeDay(CloseDayParams closeDayParams) {
        DailyConsumptionModel dailyConsumptionModel = getDailyConsumptionModel(closeDayParams.getStartDate(), closeDayParams.getEndDate());
        DailyClosureNew dailyClosureNew = new DailyClosureNew();
        dailyClosureNew.setClosureTime(now());
        dailyClosureNew.setTotalCash(dailyConsumptionModel.getConsumptionCash());
        dailyClosureNew.setTotalCreditCard(dailyConsumptionModel.getConsumptionCreditCard());
        dailyClosureNew.setTotalCoupon(dailyConsumptionModel.getConsumptionCoupon());
        dailyClosureNew.setServiceFeeCash(dailyConsumptionModel.getServiceFeeCash());
        dailyClosureNew.setServiceFeeCreditCard(dailyConsumptionModel.getServiceFeeCreditCard());
        dailyClosureNew.setServiceFeeCoupon(dailyConsumptionModel.getServiceFeeCoupon());
        dailyClosureNew.setServiceFeeNet(dailyConsumptionModel.getServiceFeeNetTotal());
        dailyClosureNew.setServiceFeeTotal(dailyConsumptionModel.getServiceFeeTotal());
        dailyClosureNew.setTotalCommerce(closeDayParams.getTotalCommerce());
        dailyClosureNew.setOtherIncome(closeDayParams.getOtherIncome());
        dailyClosureNew.setCreditCardTerminal(closeDayParams.getCreditCardTerminal());
        dailyClosureNew.setServiceFeeOver(closeDayParams.getServiceFeeOver());
        dailyClosureNewRepository.save(dailyClosureNew);
    }

    public enum DiscountType {
        PRODUCT,
        TABLE,
        TOTAL;

        public String toI18nString() {
            if (this.equals(PRODUCT))
                return Resources.CONFIG.getString("DiscountType.Product");
            if (this.equals(TABLE))
                return Resources.CONFIG.getString("DiscountType.Table");
            if (this.equals(TOTAL))
                return Resources.CONFIG.getString("DiscountType.Total");
            return "";
        }
    }
}
