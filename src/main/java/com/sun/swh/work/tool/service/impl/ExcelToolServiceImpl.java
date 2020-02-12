package com.sun.swh.work.tool.service.impl;

import com.sun.swh.work.tool.WorkbookUtil;
import com.sun.swh.work.tool.bean.Parameter;
import com.sun.swh.work.tool.cache.SpecificationsCache;
import com.sun.swh.work.tool.service.ExcelToolService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: swh
 * @Date: 2019/8/1 22:22
 * @Description:
 */
@Service
public class ExcelToolServiceImpl implements ExcelToolService {

    private static final String MODEL_EXCEL_FILE_NAME = "model.xlsx";

    private static Map<String, Map<String, Double>> SPECIFICATIONS = SpecificationsCache.getInstinse().getSpecificationsCache();

    @Override
    public void createMothExcel(Parameter parameter) {

        SPECIFICATIONS.forEach((storeName,specificationsMap) -> {
            XSSFWorkbook xssfSheets = WorkbookUtil.getWorkbook(MODEL_EXCEL_FILE_NAME,true);
            changModelExcel(xssfSheets,storeName,specificationsMap,parameter.getTime());
            writeExcel(xssfSheets, parameter.getPath()+parameter.getTime(), storeName,parameter.getTime());
        });

    }

    private void writeExcel(XSSFWorkbook xssfWorkbook, String filePath, String fileName,String time) {


        FileOutputStream fileOutputStream = null;
        try {
            File fild = new File(filePath);
            if (!fild.exists()) {
                fild.mkdirs();
            }
            File storeFile = new File(filePath + File.separator + fileName+ time + ".xlsx");
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

    private void changModelExcel(XSSFWorkbook xssfWorkbook,String storeName,Map<String, Double> specificationsMap,String time) {
        XSSFSheet dayReportSheet = xssfWorkbook.getSheetAt(0);
        dayReportSheet.setForceFormulaRecalculation(true);
        changeDayReportSheet(dayReportSheet,specificationsMap,time);
        changePurchase(xssfWorkbook.getSheetAt(1),storeName,time);
        changeBusiness(xssfWorkbook.getSheetAt(2),storeName,time);
    }

    private void changeDayReportSheet(XSSFSheet dayReportSheet,Map<String, Double> specificationsMap,String time) {
        int length = dayReportSheet.getLastRowNum();
        List<Date> times = getAllMothDayTime(time);
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
                Cell specificationsCell =  row.getCell(15);
                specificationsCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                specificationsCell.setCellValue(value);
            }
        }
    }

    /**
     * 自动生成进货额报表的日期和店名
     * @param purchase
     */
    private void changePurchase(XSSFSheet purchase,String storeName,String time) {
        changeDateAndStoreName(purchase,storeName,0,1,time);
    }

    /**
     *
     */
    private void changeBusiness(XSSFSheet business,String storeName,String time) {
        changeDateAndStoreName(business,storeName,0,20,time);
    }

    /**
     * 自动生成进货额报表的日期和店名
     * @param purchase
     */
    private void changeDateAndStoreName(XSSFSheet purchase,String storeName,int a,int b,String time) {
        List<Date> times = getAllMothDayTime(time);
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

    private List<Date> getAllMothDayTime(String time) {
        List<Date> times = new ArrayList<>();

        Calendar cale = Calendar.getInstance();
        String[] timeArray = time.split("-");
        cale.set(Integer.valueOf(timeArray[0]),Integer.valueOf(timeArray[1])-1,1);
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
}
