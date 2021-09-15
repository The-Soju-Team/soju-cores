package com.hh.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @A1 Write data to excel
 * @A2 Based Mr.Hiendm1
 *
 * @author Le Van Tien
 * @Description: write data to excel
 */
public class WriteDataToExcel {
    private static final Logger log = Logger.getLogger(WriteDataToExcel.class);

    private WriteDataToExcel() {

    }

    /**
     * Export data from DB and etc.. into excel based file template
     *
     * @param data             data need export
     * @param templateFile     file template - as full path
     * @param outputFile       file excel output - as full path
     * @param sheetIdx         - sheet index
     * @param heightheader     - height 's header
     * @param listOfDataHeader data in header
     * @throws InvalidFormatException happen occur while cann't open file template
     * @throws IOException            happen occur in cases of cann't close OPCPackage...
     */
    public static void importExcel(List<org.apache.spark.sql.Row> data, String outputFile,
            int heightheader, String[] headers) throws InvalidFormatException, IOException {

        FileOutputStream outputStream = null;
        try {

            // 1. Initialize excel

            XSSFWorkbook workbook = new XSSFWorkbook();

            // 2. Create style for workbook
            createStyleForWorkbook(workbook);
            // 3. Get sheet ID
            // 4. Initialize data into each of sheet in workbook.
            XSSFSheet sheet = workbook.createSheet("Report");

            completeHeader(headers, sheet);
            completeBody(data, sheet, heightheader);

            // 5. Write to file
            outputStream = new FileOutputStream(new File(outputFile));
            workbook.write(outputStream);
        } catch (IOException e) {
            throw e;
        } finally {
            if (null != outputStream) {
                try {
                    log.info("Stream is closed");
                    outputStream.close();
                } catch (IOException ioe) {
                    throw ioe;
                }
            }
        }
    }

    /**
     * Create style for workbook
     *
     * @param workbook workbook need apply style
     */
    private static void createStyleForWorkbook(XSSFWorkbook workbook) {
        // Create style for workbook
        XSSFCellStyle style = workbook.createCellStyle();
        // Set attributes
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        XSSFFont xssfFont = workbook.createFont();
        xssfFont.setFontHeightInPoints((short) 13);
        style.setFont(xssfFont);
    }

    /**
     * Complete header for Sheet
     *
     * @param data         data
     * @param heightheader number rows header
     * @param sheet        sheet
     */
    private static void completeHeader(String[] headers, XSSFSheet sheet) {

        if (null == headers) {
            return;
        }
        Row row = sheet.createRow(0);
        int index = 0;
        final int SIZE_LOOPS = headers.length;
        for (int i = 0; i < SIZE_LOOPS; i++) {
            Cell cell = row.createCell(index);
            index++;

            CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
            // Check and validate data before insert into body
            if (null == headers[i]) {
                continue;
            } else if (headers[i] instanceof String) {
                cell.setCellValue(headers[i]);
            } else {
                log.info("!!!ERROR _ Data not in check");
                log.info(headers[i].getClass().getName());
            }

        }

    }

    /**
     * Complete body for sheet
     *
     * @param data         data need export
     * @param sheet        sheet
     * @param heightheader number rows header
     */

    private static void completeBody(List<org.apache.spark.sql.Row> data, XSSFSheet sheet,
            int heightheader) {

        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        short dateFormat = createHelper.createDataFormat().getFormat("dd-MM-yyyy");
        cellStyle.setDataFormat(dateFormat);
        final int SIZE_ROWS = data.size();
        log.info("Size data need write = " + SIZE_ROWS);

        for (int i = 0; i < SIZE_ROWS; i++) {
            Row row = sheet.createRow(i + heightheader);
            int SIZE_COLS = data.get(i).size();
            org.apache.spark.sql.Row rowData = data.get(i);
            for (int j = 0; j < SIZE_COLS; j++) {
                Cell cell = row.createCell(j);

                // Check and validate data before insert into body
                if (null == data.get(i).get(j)) {
                    continue;
                } else if (rowData.get(j) instanceof String) {
                    cell.setCellValue((String) rowData.get(j));
                } else if (rowData.get(j) instanceof Integer) {
                    cell.setCellValue((int) rowData.get(j));
                } else if (rowData.get(j) instanceof Boolean) {
                    cell.setCellValue((boolean) rowData.get(j));
                } else if (rowData.get(j) instanceof Date) {
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Date) rowData.get(j));
                } else if (rowData.get(j) instanceof Double) {
                    cell.setCellValue((Double) rowData.get(j));
                } else if (rowData.get(j) instanceof BigDecimal) {
                    cell.setCellValue(rowData.get(j).toString());
                } else if (rowData.get(j) instanceof Long) {
                    cell.setCellValue((Long) rowData.get(j));
                } else {
                    log.info("!!!ERROR _ Data not in check");
                    log.info(rowData.get(j).getClass().getName());
                }
            }
        }
    }

}
