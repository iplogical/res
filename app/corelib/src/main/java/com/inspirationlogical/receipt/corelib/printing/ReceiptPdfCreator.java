package com.inspirationlogical.receipt.corelib.printing;

import com.inspirationlogical.receipt.corelib.params.ReceiptPrintModel;
import com.inspirationlogical.receipt.corelib.params.ReceiptRecordPrintModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Component
public class ReceiptPdfCreator {

    private final static Logger logger = LoggerFactory.getLogger(ReceiptPdfCreator.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm:ss");

    private static final int FONT_SIZE = 10;
    private static final int TITLE_FONT_SIZE = 14;
    private static final int PADDING_BOTTOM = 6;

    private Document document;
    private BaseFont normalFont;
    private BaseFont boldFont;

    private ReceiptPrintModel receiptPrintModel;
    private boolean aggregated = false;
    private PdfPTable productsTable;

    byte[] createReceiptPdf(ReceiptPrintModel receiptPrintModel) {
        this.receiptPrintModel = receiptPrintModel;
        aggregated = false;
        return createReceiptPdf();
    }

    private byte[] createReceiptPdf() {
        try {
            initFonts();
            return createPdf();
        } catch (DocumentException e) {
            logger.error("DocumentException in createReceiptPdf.", e);
        } catch (IOException e) {
            logger.error("IOException in createReceiptPdf.", e);
        }
        return new byte[0];
    }

    private void initFonts() throws DocumentException, IOException {
        initNormalFont();
        initBoldFont();
    }

    private void initNormalFont() throws DocumentException, IOException {
        Resource fontResource = new ClassPathResource("fop/fonts/SourceSansPro-Light.otf");
        File fontFile = getResourceAsFile(fontResource, "temp/SourceSansPro-Light.otf");
        normalFont = BaseFont.createFont(fontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

    }

    private void initBoldFont() throws DocumentException, IOException {
        Resource fontResource = new ClassPathResource("fop/fonts/SourceSansPro-Bold.otf");
        File fontFile = getResourceAsFile(fontResource, "temp/SourceSansPro-Bold.otf");
        boldFont = BaseFont.createFont(fontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    private File getResourceAsFile(Resource resource, String filePath) throws IOException {
        try (InputStream fontStream = resource.getInputStream()){
            File fontFile = new File(filePath);
            if (!fontFile.exists()) {
                FileUtils.copyInputStreamToFile(fontStream, fontFile);
            }
            return fontFile;
        }
    }

    private byte[] createPdf() throws DocumentException, IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            initDocument(os);
            createPdfDocument();
            document.close();
            return os.toByteArray();
        }
    }

    private void initDocument(ByteArrayOutputStream os) throws DocumentException {
        document = new Document(new RectangleReadOnly(227, 600));
        document.setMargins(0f, 24f, 24f, 6f);
        PdfWriter.getInstance(document, os);
        document.open();
    }

    private void createPdfDocument() throws IOException, DocumentException {
        createHeader();
        createBody();
        createFooter();
    }

    private void createHeader() throws IOException, DocumentException {
        createLogo();
        createRestaurantName();
        createRestaurantInfo();
        createConsumptionHeader();
    }

    private void createLogo() throws IOException, DocumentException {
        Image image = getLogoFromResource();
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(image, 0, 0, true));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

    }

    private Image getLogoFromResource() throws IOException, BadElementException {
        Resource imageResource = new ClassPathResource("images/game_up_logo.png");
        File imageFile = getResourceAsFile(imageResource, "temp/game_up_logo.png");
        Image image = Image.getInstance(imageFile.getAbsolutePath());
        image.setAlignment(Image.LEFT | Image.TEXTWRAP);
        return image;
    }

    private void createRestaurantName() throws DocumentException {
        Paragraph paragraph = new Paragraph(receiptPrintModel.getRestaurantName(), new Font(boldFont, TITLE_FONT_SIZE));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    private void createRestaurantInfo() throws DocumentException {
        String restaurantInfo = receiptPrintModel.getRestaurantAddress() + "\n" + receiptPrintModel.getRestaurantPhoneNumber()
                + "\n" + receiptPrintModel.getRestaurantSocialMediaInfo() + "\n"+ receiptPrintModel.getRestaurantWebsite();
        Paragraph paragraph = getNormalParagraph(restaurantInfo);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    private Paragraph getNormalParagraph(String restaurantInfo) {
        return new Paragraph(restaurantInfo, new Font(normalFont, FONT_SIZE, Font.BOLD));
    }

    private void createConsumptionHeader() throws DocumentException {
        addDoubleLineSeparator(-4, -7);
        Paragraph paragraph = new Paragraph("Fogyasztás összesítő", new Font(boldFont, 12));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        addDoubleLineSeparator(-5, -8);
    }

    private void createBody() throws DocumentException {
        createProductsTable();
        createHeaderRow();
        receiptPrintModel.getReceiptRecordsPrintModels().forEach(this::createProductRow);
        if(aggregated) {
            createAggregatedSummaryRows();
        } else {
            createSummaryRows();
        }
        document.add(productsTable);
        addDoubleLineSeparator(-4, -7);
    }

    private void addDoubleLineSeparator(float offsetOne, float offsetTwo) throws DocumentException {
        LineSeparator lineSeparator1 = new LineSeparator(1.5f, 100, null, 0, offsetOne);
        document.add(lineSeparator1);
        LineSeparator lineSeparator2 = new LineSeparator(1.5f, 100, null, 0, offsetTwo);
        document.add(lineSeparator2);
    }

    private void createProductsTable() throws DocumentException {
        productsTable = new PdfPTable(4);
        productsTable.setSpacingBefore(10f);
        productsTable.setWidthPercentage(100);
        int[] columnWidths = {1, 1, 1, 1};
        productsTable.setWidths(columnWidths);
    }

    private void createHeaderRow() {
        PdfPCell productNameCell = createHeaderCell("Megnevezés");
        productNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        productsTable.addCell(productNameCell);
        productsTable.addCell(createHeaderCell("Mennyiség"));
        productsTable.addCell(createHeaderCell("Egységár"));
        productsTable.addCell(createHeaderCell("Összesen"));
    }

    private PdfPHeaderCell createHeaderCell(String text) {
        PdfPHeaderCell header = new PdfPHeaderCell();
        Phrase phrase = getHeaderPhrase(text);
        header.setPhrase(phrase);
        header.setPaddingBottom(PADDING_BOTTOM);
        header.setHorizontalAlignment(Element.ALIGN_RIGHT);
        header.setBorder(Rectangle.BOTTOM);
        header.setBorderWidth(1);
        return header;
    }

    private Phrase getHeaderPhrase(String value) {
        return getHeaderPhrase(value, Font.NORMAL);
    }

    private Phrase getHeaderPhrase(String value, int font) {
        return new Phrase(value, new Font(boldFont, 8, font));
    }

    private void createProductRow(ReceiptRecordPrintModel model) {
        createProductNameCell(model);
        createProductQuantityCell(model);
        createProductPriceCell(model);
        createTotalPriceCell(model);
    }

    private void createProductNameCell(ReceiptRecordPrintModel model) {
        PdfPCell productNameCell = createProductRowCell(model.getProductName() + (model.isDiscount() ? " *" : ""));
        productNameCell.setColspan(4);
        productNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        productNameCell.setBorder(Rectangle.NO_BORDER);
        productsTable.addCell(productNameCell);
    }

    private void createProductQuantityCell(ReceiptRecordPrintModel model) {
        PdfPCell productQuantityCell = createProductRowCell(model.getSoldQuantity() + " x");
        productQuantityCell.setColspan(2);
        productQuantityCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        productsTable.addCell(productQuantityCell);
    }

    private void createProductPriceCell(ReceiptRecordPrintModel model) {
        PdfPCell productPriceCell = createProductRowCell(model.getProductPrice() + " =");
        productPriceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        productsTable.addCell(productPriceCell);
    }

    private void createTotalPriceCell(ReceiptRecordPrintModel model) {
        PdfPCell totalPriceCell = createProductRowCell(String.valueOf(model.getTotalPrice()));
        totalPriceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalPriceCell.setPaddingBottom(3);
        productsTable.addCell(totalPriceCell);
    }

    private PdfPCell createProductRowCell(String text) {
        Phrase phrase = getProductPhrase(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderWidth(1);
        return cell;
    }

    private Phrase getProductPhrase(String value) {
        return new Phrase(value, new Font(normalFont, 12, Font.BOLD));
    }

    private void createSummaryRows() {
        createTotalPriceRow();
        createServiceFeeRows();
        createDiscountRows();
        createRoundedTotalPriceRow();
        createCurrencyRow();
        createPaymentMethodRow();
    }

    private void createTotalPriceRow() {
        createTableSummaryRow("Összesen:", receiptPrintModel.getTotalPriceNoServiceFee());
    }

    private void createTableSummaryRow(String text, int totalPrice) {
        createTableSummaryTextCell(text);
        createTableSummaryValueCell(totalPrice);
    }

    private void createTableSummaryTextCell(String text) {
        PdfPCell summaryCell = createTableSummaryCell(text);
        summaryCell.setColspan(3);
        summaryCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        summaryCell.setBorder(Rectangle.NO_BORDER);
        productsTable.addCell(summaryCell);
    }

    private PdfPCell createTableSummaryCell(String text) {
        Phrase phrase = getTableSummaryPhrase(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private Phrase getTableSummaryPhrase(String value) {
        return new Phrase(value, new Font(boldFont, 12));
    }

    private void createTableSummaryValueCell(int totalPrice) {
        PdfPCell totalPriceCell = createTableSummaryCell(String.valueOf(totalPrice));
        totalPriceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        productsTable.addCell(totalPriceCell);
    }

    private void createServiceFeeRows() {
        int serviceFee = receiptPrintModel.getServiceFee();
        if(serviceFee == 0) {
            return;
        }
        String serviceFeeText = "Szervízdíj / Service (" + receiptPrintModel.getServiceFeePercent() + " %)";
        createTableSummaryRow(serviceFeeText, serviceFee);
        createTableSummaryRow("Összesen(szerv.):", receiptPrintModel.getTotalPriceWithServiceFee());
    }

    private void createDiscountRows() {
        int totalDiscount = receiptPrintModel.getTotalDiscount();
        if(totalDiscount == 0) {
            return;
        }
        createTableSummaryRow("Kedvezmény:", -1 * receiptPrintModel.getTotalDiscount());
        createTableSummaryRow("Összesen(kedv.):", receiptPrintModel.getDiscountedTotalPrice());
    }

    private void createRoundedTotalPriceRow() {
        int roundedTotalPrice = receiptPrintModel.getRoundedTotalPrice();
        if(roundedTotalPrice == receiptPrintModel.getDiscountedTotalPrice()) {
            return;
        }
        createTableSummaryRow("Összesen(kerekít):", roundedTotalPrice);
    }

    private void createCurrencyRow() {
        createTableSummaryTextCell("Pénznem:");

        PdfPCell currencyCell = createTableSummaryCell("HUF");
        currencyCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        productsTable.addCell(currencyCell);

    }

    private void createPaymentMethodRow() {
        PdfPCell paymentMethodTextCell = createTableSummaryCell("Fizetési mód:");
        paymentMethodTextCell.setColspan(2);
        paymentMethodTextCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        productsTable.addCell(paymentMethodTextCell);

        PdfPCell paymentMethodCell = createTableSummaryCell(receiptPrintModel.getPaymentMethod());
        paymentMethodCell.setColspan(2);
        paymentMethodCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        productsTable.addCell(paymentMethodCell);
    }

    private void createFooter() throws DocumentException {
        addFooterParagraph(receiptPrintModel.getReceiptDisclaimer());
        addFooterParagraph(receiptPrintModel.getReceiptNote());
        addFooterParagraph(receiptPrintModel.getReceiptGreet());
        addFooterParagraph(receiptPrintModel.getClosureTime().format(DATE_TIME_FORMATTER)
                + "\nBizonylat azonostó: " + receiptPrintModel.getReceiptId(), Element.ALIGN_RIGHT);
        addFooterParagraph("© InspirationLogical, Receipt v4.3");
    }

    private void addFooterParagraph(String text) throws DocumentException {
        addFooterParagraph(text, Element.ALIGN_CENTER);
    }

    private void addFooterParagraph(String text, int alignment) throws DocumentException {
        Paragraph paragraph = getNormalParagraph(text);
        paragraph.setAlignment(alignment);
        paragraph.setSpacingBefore(5);
        document.add(paragraph);
    }

    byte[] createAggregatedReceiptPdf(ReceiptPrintModel receiptPrintModel) {
        this.receiptPrintModel = receiptPrintModel;
        aggregated = true;
        return createReceiptPdf();
    }

    private void createAggregatedSummaryRows() {
        createTableSummaryRow("Nyitott:", receiptPrintModel.getOpenConsumption());
        createTableSummaryRow("Készpénz:", receiptPrintModel.getConsumptionCash());
        createTableSummaryRow("Bankkártya:", receiptPrintModel.getConsumptionCreditCard());
        createTableSummaryRow("Kupon:", receiptPrintModel.getConsumptionCoupon());
        createTableSummaryRow("Összesen:", receiptPrintModel.getTotalConsumption());

        createTableSummaryRow("Termék kedvezmény:", receiptPrintModel.getProductDiscount());
        createTableSummaryRow("Asztal kedvezmény:", receiptPrintModel.getTableDiscount());
        createTableSummaryRow("Összes kedvezmény:", receiptPrintModel.getTotalDiscount());
    }
}
