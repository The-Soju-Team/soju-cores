/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Ha
 */
public class OfficeUtils {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(OfficeUtils.class.getSimpleName());
    /**
     * Hàm xuất dữ liệu ra file xlsx với template
     *
     * @param data dữ liệu
     * @param templateFile đường dẫn file mẫu
     * @param headerHeight số lượng hàng của Header
     * @param outputFile đường dẫn xuất file
     * @param sheetIndex sheet ghi dữ liệu
     * @param startRow vị trí bắt đầu ghi dữ liệu
     * @param templateParam danh sách giá trị của tham số trong file mẫu
     * @param fontName tên font
     * @param fontSize kích cỡ font
     * 
     */    
    public static void exportToXlsx(List<List> data, String templateFile, int headerHeight, 
            String outputFile, int sheetIndex, int startRow, HashMap templateParam, 
            String fontName, int fontSize, List<Integer> percentColumn) 
            throws Exception {
        if(data.size() > 1000000) {
            log.info("Data size > 1000000 row, cannot write to 1 sheet xlsx");
            return;
        }
        
        if(data.size() <= 0) {
            log.info("Data size <= 0 row, cannot write to xlsx");
            return;            
        }
        
        XSSFWorkbook wb = new XSSFWorkbook();
        log.info("templateFile : " + templateFile);
        log.info("outputFile : " + outputFile);
        if(templateFile != null)
            wb = new XSSFWorkbook(new FileInputStream(new File(templateFile)));
        XSSFSheet sheet = null;
        if(templateFile != null) 
            sheet = (XSSFSheet) wb.getSheetAt(sheetIndex);
        else 
            sheet = (XSSFSheet) wb.createSheet();
        
        // Tạo style
        CellStyle style = wb.createCellStyle();
//        style.setBorderBottom(BorderStyle.THIN);
//        style.setBorderTop(BorderStyle.THIN);
//        style.setBorderRight(BorderStyle.THIN);
//        style.setBorderLeft(BorderStyle.THIN);
//        style.setWrapText(true);
//        Font hSSFFont = wb.createFont();
//        hSSFFont.setFontName(fontName);
//        hSSFFont.setFontHeightInPoints((short) fontSize);
//        style.setFont(hSSFFont);  
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        
        // Điền tham số phần header
        if(templateParam != null) {
            log.info("Dien tham so phan Header.");
            for (int i = 1; i <= headerHeight; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < data.get(0).size(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_NUMERIC:
                                    if (templateParam.get(cell.getStringCellValue()) instanceof Double) {
                                        cell.setCellValue((Double)templateParam.get(cell.getStringCellValue()));
                                    } else {
                                        cell.setCellValue(templateParam.get(cell.getStringCellValue()).toString());
                                    }
                                    break;                            
                                case Cell.CELL_TYPE_STRING:
                                    if (cell.getStringCellValue().equals("$date")) {
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                        Date currentDate = new Date();
                                        String strDate = formatter.format(currentDate);
                                        cell.setCellValue(strDate);
                                    } else if (templateParam.get(cell.getStringCellValue()) instanceof Double) {
                                        cell.setCellValue((Double)templateParam.get(cell.getStringCellValue()));
                                    } else {
                                        cell.setCellValue(templateParam.get(cell.getStringCellValue()).toString());
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }

        // Sao chép phần footer
        List lstFooter = new ArrayList();
        if(headerHeight != 0) {
            int count1 = headerHeight + 3;
            log.info("Sao chep phan Footer.");
            for (int i = 0; i < 10; i++) {
                Row row = sheet.getRow(count1);
                List lstRow = new ArrayList();
                if (row != null) {
                    for (int j = 0; j < data.get(0).size(); j++) {
                        Cell cell = row.getCell(j);
                        List lstCell = new ArrayList();
                        if (cell != null) {
                            lstCell.add(cell.getCellStyle());
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_NUMERIC:
                                    lstCell.add("numeric");
                                    lstCell.add(cell.getNumericCellValue());
                                    break;
                                case Cell.CELL_TYPE_STRING:
                                    lstCell.add("string");
                                    lstCell.add(cell.getStringCellValue());
                                    break;
                            }
                        }
                        lstRow.add(lstCell);
                    }
                }
                lstFooter.add(lstRow);
                count1++;
            }
            int count = headerHeight + 3;
            for (int i = 0; i < lstFooter.size(); i++) {
                Row rowCoppy = sheet.createRow(count + data.size());
                List lstRow = (List) lstFooter.get(i);
                if (!lstRow.isEmpty()) {
                    int countCell = 0;
                    for (int j = 0; j < data.get(0).size(); j++) {
                        Cell cellCopy = rowCoppy.createCell(countCell);
                        List lstCell = (List) lstRow.get(j);
                        if (lstCell.size() == 3) {
                            cellCopy.setCellStyle((CellStyle) lstCell.get(0));
                            if (lstCell.get(1).equals("numeric")) {
                                cellCopy.setCellValue((double) lstCell.get(2));
                            } else {
                                cellCopy.setCellValue(lstCell.get(2).toString());
                            }
                        }
                        countCell++;
                    }
                }
                count++;
            }
        }

        // Ghi dữ liệu báo cáo
        Row templateRow = null;
        if(templateFile != null) templateRow = sheet.getRow(headerHeight + 1);
        else templateRow = sheet.createRow(1);
        List<Integer> lstCellType = new ArrayList();
        for (int i = 0; i < data.get(0).size(); i++) {
            Cell teplateCell = null;
            if(templateFile != null) teplateCell = templateRow.getCell(i);
            else teplateCell = templateRow.createCell(i);
            lstCellType.add(teplateCell.getCellType());
        }
        log.info("Dang ghi du lieu bao cao phan BODY ....");
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + headerHeight + 1);
            List objArr = data.get(i);
            if(i % 1000 == 0) log.info("ghi dong " + i + "/" + data.size());
            for (int j = 0; j < objArr.size(); j++) {
                Cell cell = row.createCell(j);
                if(percentColumn != null && percentColumn.contains(Integer.valueOf(j))) {
                    CellStyle percentStyle = wb.createCellStyle();
                    percentStyle.setDataFormat(wb.createDataFormat().getFormat("0.00%"));
                    cell.setCellStyle(percentStyle);
                } else {
                    cell.setCellStyle(style);
                }
                switch (lstCellType.get(j)) {
                    case Cell.CELL_TYPE_NUMERIC:
                        if(objArr.get(j) != null) {
                            try {
                                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                cell.setCellValue(Double.parseDouble(objArr.get(j).toString()));
                            } catch (Exception ex) {
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                cell.setCellValue(objArr.get(j).toString());
                            }
                        }
                        break;                            
                    default:
                            if(objArr.get(j) != null)
                            cell.setCellValue(objArr.get(j).toString());
                        break;
                }
            }
        }
        log.info("Hoan thanh ghi du lieu BODY.");
        // Điền tham số phần footer
        if(templateParam != null) {
            log.info("Dien tham so phan Footer.");
            int count2 = headerHeight + 3;
            for (int i = 1; i < lstFooter.size(); i++) {
                Row row = sheet.getRow(count2 + data.size());
                if (row != null) {
                    for (int j = 0; j < data.get(0).size(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_NUMERIC:
                                    if (templateParam.get(cell.getStringCellValue()) instanceof Double) {
                                        cell.setCellValue((Double)templateParam.get(cell.getStringCellValue()));
                                    } else {
                                        cell.setCellValue(templateParam.get(cell.getStringCellValue()).toString());
                                    }
                                    break;                            
                                case Cell.CELL_TYPE_STRING:
                                    if (cell.getStringCellValue().equals("$date")) {
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                        Date currentDate = new Date();
                                        String strDate = formatter.format(currentDate);
                                        cell.setCellValue(strDate);
                                    } else if (templateParam.get(cell.getStringCellValue()) instanceof Double) {
                                        cell.setCellValue((Double)templateParam.get(cell.getStringCellValue()));
                                    } else {
                                        cell.setCellValue(templateParam.get(cell.getStringCellValue()).toString());
                                    }
                                    break;
                            }
                        }
                    }
                }
                count2++;
            }
        }
        //Ghi ra file
        FileOutputStream out = new FileOutputStream(new File(outputFile));
        try {
            XSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
            wb.write(out);
            log.info("Write success file : " + outputFile);
        } finally {
            out.close();
        }
    }
    
    /**
     * Hàm xuất dữ liệu ra file xlsx
     *
     * @param data dữ liệu
     * @param templateFile đường dẫn file mẫu
     * @param outputFile đường dẫn xuất file
     * 
     */      
    public static void exportToXlsx(List<List> data, String templateFile, String outputFile) throws Exception {
        exportToXlsx(data, templateFile, 0, outputFile, 0, 0, null, "Times New Roman", 11, null);
    }    
    
    /**
     * Hàm xuất dữ liệu ra file xlsx
     *
     * @param data dữ liệu
     * @param templateFile đường dẫn file mẫu
     * @param outputFile đường dẫn xuất file
     * @param sheetIndex sheet ghi dữ liệu
     * 
     */      
    public static void exportToXlsx(List<List> data, String templateFile, String outputFile, int sheetIndex) throws Exception {
        exportToXlsx(data, templateFile, 0, outputFile, sheetIndex, 0, null, "Times New Roman", 11, null);
    }     
    
    /**
     * Hàm xuất dữ liệu ra file xlsx
     *
     * @param data dữ liệu
     * @param templateFile đường dẫn file mẫu
     * @param outputFile đường dẫn xuất file
     * @param sheetIndex sheet ghi dữ liệu
     * @param templateParam danh sách giá trị của tham số trong file mẫu
     * 
     */      
    public static void exportToXlsx(List<List> data, String templateFile, String outputFile, int sheetIndex, HashMap templateParam) throws Exception {
        exportToXlsx(data, templateFile, 0, outputFile, sheetIndex, 0, templateParam, "Times New Roman", 11, null);
    }      
    
//    public static void main(String[] args) throws Exception {
//        List<List> value = new ArrayList();
//        List headerRow = new ArrayList();
//        headerRow.add("CP_CODE");
//        headerRow.add("TINH");
//        headerRow.add("NGAY");
//        headerRow.add("SO_GIAO_DICH");
//        headerRow.add("TONG_TIEN");            
//        value.add(headerRow);                
//        List row = new ArrayList();
//        row.add("ACSCRDT");
//        row.add("Lâm Đồng");
//        row.add("2018-10-01");
//        row.add("1");
//        row.add("3100000");            
//        value.add(row);        
//        OfficeUtils.exportToXlsx(value , "D:\\kpp-province-channel.xlsx", "D:\\kpp-province-channel1.xlsx", 0);
//    }
}
