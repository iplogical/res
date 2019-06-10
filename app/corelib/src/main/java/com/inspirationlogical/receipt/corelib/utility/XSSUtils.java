package com.inspirationlogical.receipt.corelib.utility;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class XSSUtils {

    public static void createStringHeaderCell(XSSFSheet sheet, XSSFCellStyle boldCellStyle, XSSFRow row, int columnIndex, String title, int columnWidth) {
        createStringCell(sheet, boldCellStyle, row, columnIndex, null, title);
        sheet.setColumnWidth(columnIndex, columnWidth);
    }

    public static void createStringCell(XSSFSheet sheet, XSSFCellStyle cellStyle, XSSFRow row, int columnIndex,
                                        CellRangeAddress region, String value) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
        if (region != null) {
            sheet.addMergedRegion(region);
        }
    }

    public static void createNumericCell(XSSFSheet sheet, XSSFCellStyle cellStyle, XSSFRow row, int columnIndex,
                                         CellRangeAddress region, Integer value) {
        createNumericCell(sheet, cellStyle, row, columnIndex, region, (double) value);
    }

    public static void createNumericCell(XSSFSheet sheet, XSSFCellStyle cellStyle, XSSFRow row, int columnIndex,
                                         CellRangeAddress region, Double value) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(value);
        if (region != null) {
            sheet.addMergedRegion(region);
        }
    }

    public static void createThinTopBorder(XSSFSheet sheet, int rowNum, int endColumnIndex) {
        RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 0, endColumnIndex), sheet);
    }

    public static void createMediumTopBorder(XSSFSheet sheet, int rowNum, int endColumnIndex) {
        RegionUtil.setBorderTop(BorderStyle.MEDIUM, new CellRangeAddress(rowNum, rowNum, 0, endColumnIndex), sheet);
    }

    public static void createVerticalBorders(XSSFSheet sheet, int startRowNum, int endRowNum, int startColumnIndex, int endColumnIndex) {
        RegionUtil.setBorderLeft(BorderStyle.MEDIUM, new CellRangeAddress(startRowNum, endRowNum, startColumnIndex, startColumnIndex), sheet);
        for (int i = startColumnIndex; i < endColumnIndex; i++) {
            RegionUtil.setBorderRight(BorderStyle.THIN, new CellRangeAddress(startRowNum, endRowNum, i, i), sheet);
        }
        RegionUtil.setBorderRight(BorderStyle.MEDIUM, new CellRangeAddress(startRowNum, endRowNum, endColumnIndex, endColumnIndex), sheet);
    }
}
