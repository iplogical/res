package com.inspirationlogical.receipt.corelib.service.daily_closure;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosureNew;
import com.inspirationlogical.receipt.corelib.repository.DailyClosureNewRepository;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.service.daily_closure.DailyConsumptionServiceImpl.getCreditCardOver;
import static com.inspirationlogical.receipt.corelib.utility.XSSUtils.*;

@Service
@Transactional
public class DailyClosureReportGeneratorImpl implements DailyClosureReportGenerator {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd. EEE");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FILE_NAME_SUFFIX_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    private static final int END_COLUMN_INDEX = 10;

    @Autowired
    private DailyClosureNewRepository dailyClosureNewRepository;

    private XSSFFont boldFont;
    private XSSFFont boldHeaderFont;
    private XSSFFont normalFont;

    private XSSFCellStyle rightAlignedStyle;
    private XSSFCellStyle leftAlignedStyle;
    private XSSFCellStyle centerAlignedStyle;
    private XSSFCellStyle boldStyle;

    @Override
    public String createDailyClosureReport(LocalDate startDate, LocalDate endDate) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        initFonts(workbook);
        createWorkbookSheet(startDate, endDate, workbook);
        return saveReportAsFile(workbook, getReportFileName(startDate, endDate));
    }


    protected void initFonts(XSSFWorkbook workbook) {
        boldFont = workbook.createFont();
        boldFont.setBold(true);

        boldHeaderFont = workbook.createFont();
        boldHeaderFont.setBold(true);
        boldHeaderFont.setFontHeight(18);

        normalFont = workbook.createFont();
        normalFont.setFontHeight(9);
        rightAlignedStyle = createNormalCellStyle(workbook);
        rightAlignedStyle.setAlignment(HorizontalAlignment.RIGHT);
        leftAlignedStyle = createNormalCellStyle(workbook);
        leftAlignedStyle.setAlignment(HorizontalAlignment.LEFT);
        centerAlignedStyle = createNormalCellStyle(workbook);
        centerAlignedStyle.setWrapText(true);
        centerAlignedStyle.setAlignment(HorizontalAlignment.CENTER);
        boldStyle = createBoldCellStyle(workbook, 11);
        boldStyle.setAlignment(HorizontalAlignment.RIGHT);
    }

    protected XSSFCellStyle createNormalCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle normalCellStyle = workbook.createCellStyle();
        normalCellStyle.setFont(normalFont);
        return normalCellStyle;
    }

    protected XSSFCellStyle createBoldCellStyle(XSSFWorkbook workbook, int fontSize) {
        XSSFCellStyle headerCellStyle = workbook.createCellStyle();
        boldFont.setFontHeight(fontSize);
        headerCellStyle.setFont(boldFont);
        return headerCellStyle;
    }

    private void createWorkbookSheet(LocalDate startDate, LocalDate endDate, XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, "Napi zárások");
        createHeaderRow(sheet);
        int nextRowNum = createDailyClosureRows(sheet, startDate, endDate);
        createVerticalBorders(sheet, 0, nextRowNum - 1, 0, END_COLUMN_INDEX);
    }

    private void createHeaderRow(XSSFSheet sheet) {
        int rowNum = 0;
        XSSFRow row = sheet.createRow(rowNum);
        row.setHeightInPoints(25);

        createStringHeaderCell(sheet, centerAlignedStyle, row, 0, "Nap", 3000);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 1, "Zárás ideje", 2300);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 2, "KP", 1800);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 3, "Kártya", 1800);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 4, "Kupon", 1800);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 5, "Nettó Szervízdíj", 2000);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 6, "Over KP", 1800);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 7, "Over BK", 1800);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 8, "Jatt össz", 1800);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 9, "Összesen", 2100);
        createStringHeaderCell(sheet, centerAlignedStyle, row, 10, "Boríték", 2100);
        createMediumTopBorder(sheet, rowNum, END_COLUMN_INDEX);
    }

    private int createDailyClosureRows(XSSFSheet sheet, LocalDate startDate, LocalDate endDate) {
        HashMap<Integer, Integer> rowNumMap = new HashMap<>();
        rowNumMap.put(0, 1);
        LocalDateTime startTime = startDate.atTime(LocalTime.of(12, 0));
        LocalDateTime endTime = endDate.plusDays(1).atTime(LocalTime.of(4, 0));
        List<DailyClosureNew> dailyClosureNewList = dailyClosureNewRepository.findAllByClosureTimeBetween(startTime, endTime);
        dailyClosureNewList.forEach(dailyClosureNew -> createDailyClosureRow(sheet, rowNumMap, dailyClosureNew));
        DailyClosureNew dailyClosureNewAggregate = dailyClosureNewList.stream().reduce(dailyClosureNewList.get(0), this::dailyClosureAggregator);
        createDailyClosureRow(sheet, rowNumMap, dailyClosureNewAggregate);
        createMediumTopBorder(sheet, rowNumMap.get(0) - 1, END_COLUMN_INDEX);
        createMediumTopBorder(sheet, rowNumMap.get(0), END_COLUMN_INDEX);
        return rowNumMap.get(0);
    }

    private void createDailyClosureRow(XSSFSheet sheet, HashMap<Integer, Integer> rowNumMap, DailyClosureNew dailyClosureNew) {
        int rowNum = rowNumMap.get(0);
        XSSFRow row = sheet.createRow(rowNum);
        createStringCell(sheet, leftAlignedStyle, row, 0, null, getDateString(dailyClosureNew));
        createStringCell(sheet, centerAlignedStyle, row, 1, null, getTimeString(dailyClosureNew));
        createNumericCell(sheet, rightAlignedStyle, row, 2, null, dailyClosureNew.getTotalCash());
        createNumericCell(sheet, rightAlignedStyle, row, 3, null, dailyClosureNew.getTotalCreditCard());
        createNumericCell(sheet, rightAlignedStyle, row, 4, null, dailyClosureNew.getTotalCoupon());
        createNumericCell(sheet, rightAlignedStyle, row, 5, null, dailyClosureNew.getServiceFeeNet());
        createNumericCell(sheet, rightAlignedStyle, row, 6, null, dailyClosureNew.getServiceFeeOver());
        createNumericCell(sheet, rightAlignedStyle, row, 7, null, getCreditCardOver(dailyClosureNew));
        createNumericCell(sheet, rightAlignedStyle, row, 8, null, getTotalServiceFee(dailyClosureNew));
        createNumericCell(sheet, rightAlignedStyle, row, 9, null, getTotalCommerce(dailyClosureNew));
        createNumericCell(sheet, rightAlignedStyle, row, 10, null, getEnvelope(dailyClosureNew));
        createThinTopBorder(sheet, rowNum, END_COLUMN_INDEX);
        rowNumMap.put(0, ++rowNum);
    }

    private int getTotalServiceFee(DailyClosureNew dailyClosureNew) {
        return dailyClosureNew.getServiceFeeNet() + dailyClosureNew.getServiceFeeOver() + getCreditCardOver(dailyClosureNew);
    }

    private int getTotalCommerce(DailyClosureNew dailyClosureNew) {
        return dailyClosureNew.getTotalCash() + dailyClosureNew.getTotalCreditCard() + dailyClosureNew.getTotalCoupon() +
                getTotalServiceFee(dailyClosureNew);
    }

    private int getEnvelope(DailyClosureNew dailyClosureNew) {
        return dailyClosureNew.getTotalCash() + dailyClosureNew.getTotalCoupon() + dailyClosureNew.getServiceFeeCash() +
                dailyClosureNew.getServiceFeeCoupon() + dailyClosureNew.getServiceFeeOver();
    }

    private String getDateString(DailyClosureNew dailyClosureNew) {
        if (dailyClosureNew.getId() == 0) {
            return "Összesen";
        }
        LocalDateTime closureDateTime = dailyClosureNew.getClosureTime();
        LocalTime closureTime = closureDateTime.toLocalTime();
        if (closureTime.isBefore(LocalTime.of(4, 0))) {
            return closureDateTime.toLocalDate().minusDays(1).format(DAY_FORMATTER);
        } else {
            return closureDateTime.toLocalDate().format(DAY_FORMATTER);
        }
    }

    private String getTimeString(DailyClosureNew dailyClosureNew) {
        if (dailyClosureNew.getId() == 0) {
            return "";
        }
        return dailyClosureNew.getClosureTime().format(TIME_FORMATTER);
    }

    private DailyClosureNew dailyClosureAggregator(DailyClosureNew d1, DailyClosureNew d2) {
        DailyClosureNew aggregated = new DailyClosureNew();
        aggregated.setTotalCash(d1.getTotalCash() + d2.getTotalCash());
        aggregated.setTotalCreditCard(d1.getTotalCreditCard() + d2.getTotalCreditCard());
        aggregated.setTotalCoupon(d1.getTotalCoupon() + d2.getTotalCoupon());
        aggregated.setServiceFeeCash(d1.getServiceFeeCash() + d2.getServiceFeeCash());
        aggregated.setServiceFeeCreditCard(d1.getServiceFeeCreditCard() + d2.getServiceFeeCreditCard());
        aggregated.setServiceFeeCoupon(d1.getServiceFeeCoupon() + d2.getServiceFeeCoupon());
        aggregated.setServiceFeeNet(d1.getServiceFeeNet() + d2.getServiceFeeNet());
        aggregated.setServiceFeeTotal(d1.getServiceFeeTotal() + d2.getServiceFeeTotal());
        aggregated.setTotalCommerce(d1.getTotalCommerce() + d2.getTotalCommerce());
        aggregated.setOtherIncome(d1.getOtherIncome() + d2.getOtherIncome());
        aggregated.setCreditCardTerminal(d1.getCreditCardTerminal() + d2.getCreditCardTerminal());
        aggregated.setServiceFeeOver(d1.getServiceFeeOver() + d2.getServiceFeeOver());
        return aggregated;
    }

    private String getReportFileName(LocalDate startDate, LocalDate endDate) {
        return "Napi zárások - " + startDate.format(dtf) + " - " + endDate.format(dtf);
    }

    private String saveReportAsFile(XSSFWorkbook workbook, String reportFileName) {
        try {
            String fullReportFileName = reportFileName + ".xlsx";
            FileOutputStream out = new FileOutputStream(fullReportFileName);
            workbook.write(out);
            out.close();
            return fullReportFileName;
        } catch (IOException e) {
            return saveReportAsFile(workbook, reportFileName + " - " + LocalTime.now().format(FILE_NAME_SUFFIX_FORMATTER));
        }
    }
}
