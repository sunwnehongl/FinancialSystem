package com.sun.swh.work.tool.service.impl;

import com.sun.swh.work.tool.cache.SpecificationsCache;
import com.sun.swh.work.tool.service.ExcelToolService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: swh
 * @Date: 2019/8/1 22:22
 * @Description:
 */
public class ExcelToolServiceImpl implements ExcelToolService {

    private static final String MODEL_EXCEL_FILE_NAME = "/model.xlsx";

    private static Map<String, Map<String, Double>> SPECIFICATIONS = SpecificationsCache.getInstinse().getSpecificationsCache();

    @Override
    public void createMothExcel(String path) {

        SPECIFICATIONS.forEach((storeName,specificationsMap) -> {
            XSSFWorkbook xssfSheets = getModelExcel();
            changModelExcel(xssfSheets,storeName,specificationsMap);
            writeExcel(xssfSheets, path, storeName);
        });

    }

    private void writeExcel(XSSFWorkbook xssfWorkbook, String filePath, String fileName) {


        FileOutputStream fileOutputStream = null;
        try {
            File fild = new File(filePath);
            if (!fild.exists()) {
                fild.mkdirs();
            }
            Calendar cale = Calendar.getInstance();
            File storeFile = new File(filePath + File.separator + fileName+ (cale.get(Calendar.MONTH)+1)+ ".xlsx");
            if (!storeFile.exists()) {
                storeFile.createNewFile();
            }
            fileOutputStream = new FileOutputStream(storeFile);
            xssfWorkbook.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changModelExcel(XSSFWorkbook xssfWorkbook,String storeName,Map<String, Double> specificationsMap) {
        XSSFSheet dayReportSheet = xssfWorkbook.getSheetAt(0);
        dayReportSheet.setForceFormulaRecalculation(true);
        changeDayReportSheet(dayReportSheet,specificationsMap);
        changePurchase(xssfWorkbook.getSheetAt(1),storeName);
        changeBusiness(xssfWorkbook.getSheetAt(2),storeName);
    }

    private void changeDayReportSheet(XSSFSheet dayReportSheet,Map<String, Double> specificationsMap) {
        int length = dayReportSheet.getLastRowNum();
        List<Date> times = getAllMothDayTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/M/d");
        for (int i = 0; i <= length; i++) {
            Row row = dayReportSheet.getRow(i);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(2);
            if (cell == null) {
                continue;
            }
            if (cell.getCellType()==Cell.CELL_TYPE_STRING) {
                String sales = cell.getStringCellValue();

                if ("营业员".equals(sales) && times.size() > 0) {
                    Cell timeCell = row.getCell(0);
                    try {
                        timeCell.setCellValue(simpleDateFormat.parse(simpleDateFormat.format(times.get(0))));
                    } catch (Exception e) {
                    }
                    times.remove(0);
                    continue;
                }
            }

           cell = row.getCell(0);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            String typeName = cell.getStringCellValue();
            Double value = specificationsMap.get(typeName);
            if (value != null) {
                Cell specificationsCell =  row.getCell(14);
                specificationsCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                specificationsCell.setCellValue(value);
            }
        }
    }

    /**
     * 自动生成进货额报表的日期和店名
     * @param purchase
     */
    private void changePurchase(XSSFSheet purchase,String storeName) {
        changeDateAndStoreName(purchase,storeName,0,1);
    }

    /**
     *
     */
    private void changeBusiness(XSSFSheet business,String storeName) {
        changeDateAndStoreName(business,storeName,0,20);
    }

    /**
     * 自动生成进货额报表的日期和店名
     * @param purchase
     */
    private void changeDateAndStoreName(XSSFSheet purchase,String storeName,int a,int b) {
        List<Date> times = getAllMothDayTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/M/d");
        for (int i = 1; i <= times.size(); i++) {
            Row row = purchase.getRow(i);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(a);
            if (cell == null) {
                continue;
            }
            try {
                cell.setCellValue(simpleDateFormat.parse(simpleDateFormat.format(times.get(i-1))));
            } catch (Exception e) {
            }
            cell= row.getCell(b);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(storeName);
        }
    }

    private List<Date> getAllMothDayTime() {
        List<Date> times = new ArrayList<>();

        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.DAY_OF_MONTH, 1);
        cale.set(Calendar.HOUR, 0);
        cale.set(Calendar.MINUTE, 0);
        cale.set(Calendar.MILLISECOND, 0);
        int currentMoth = cale.get(Calendar.MONTH);
        do {
            times.add(cale.getTime());
            cale.add(Calendar.DAY_OF_MONTH, 1);
        } while (currentMoth == cale.get(Calendar.MONTH));
        return times;
    }

    private XSSFWorkbook getModelExcel() {
        XSSFWorkbook sXSSFWorkbook = null;
        InputStream inputStream = null;
        try {
            String path = this.getClass().getClassLoader().getResource("").getPath();
            inputStream = new FileInputStream(path + MODEL_EXCEL_FILE_NAME);
            sXSSFWorkbook = new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sXSSFWorkbook;
    }
}
