package com.sun.swh.work.tool.service.impl;

import com.sun.swh.work.tool.bean.StoreExcel;
import com.sun.swh.work.tool.cache.SpecificationsCache;
import com.sun.swh.work.tool.service.TurnoverService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: swh
 * @Date: 2019/8/26 21:48
 * @Description:
 */
public class TurnoverServiceImpl implements TurnoverService {


    private static final String MODEL_EXCEL_FILE_NAME = "/营业额汇总.xlsx";

    private static Map<String, Map<String, Double>> SPECIFICATIONS = SpecificationsCache.getInstinse().getSpecificationsCache();

    @Override
    public void establish(String filePath) {
        List<StoreExcel> storeExcelList = getStoreExcelList(filePath);
        XSSFWorkbook workbook = getWorkbook(getExcelModelFilePath());
        copyTurnoverData(workbook,storeExcelList);
        saveExcel(workbook,filePath);

    }

    /**
     * 获取指定目录下的每家店的报表对象
     * @param filePath 报表的目录
     * @return 每家店的月度报表对象集合
     */
    private List<StoreExcel> getStoreExcelList(String filePath){
        List<StoreExcel> storeExcelList = new ArrayList<>(100);
        File directory = new File(filePath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                String storeName = getStorName(file.getName());
                if (!SPECIFICATIONS.containsKey(storeName)) {
                    continue;
                }
                StoreExcel storeExcel = new StoreExcel(storeName,file.getPath());
                storeExcelList.add(storeExcel);
            }
        }
        return  storeExcelList;
    }

    /**
     * 通过文件名得到对应店的名称
     * @param fileName 文件名称
     * @return 店名
     */
    private String getStorName(String fileName) {
        String storeName = "";
        storeName = fileName.split("\\.")[0] ;
        for (int i =  storeName.length() -1 ;i >0; i--) {
            if (storeName.charAt(i) < 48 || storeName.charAt(i) > 57) {
                storeName = storeName.substring(0, i+1);
                break;
            }
        }
        return storeName;
    }

    private XSSFWorkbook getWorkbook(String filePath) {
        XSSFWorkbook sXSSFWorkbook = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
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

    private String getExcelModelFilePath() {
        String filePath = "";
        filePath = this.getClass().getClassLoader().getResource("").getPath();
        filePath += MODEL_EXCEL_FILE_NAME;
        return filePath;
    }

    private void saveExcel(XSSFWorkbook workbook,String filePath) {
        FileOutputStream fileOutputStream = null;
        try {
            Date date = new Date();
            String strDateFormat = "yyyyMMddHHmmss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            File file = new File(filePath+File.separator+"营业额汇总"+sdf.format(date)+".xlsx");
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
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

    private void copyTurnoverData(XSSFWorkbook workbook,List<StoreExcel> storeExcelList) {
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
        Sheet sheet = workbook.getSheetAt(0);
        sheet.setForceFormulaRecalculation(true);
        for (StoreExcel storeExcel : storeExcelList) {
            XSSFWorkbook oldWorkbook = getWorkbook(storeExcel.getFilePath());
            copyTurnoverSheet(sheet,oldWorkbook,cellStyle);
        }
    }

    private void copyTurnoverSheet(Sheet sheet,XSSFWorkbook turnoverWorkbook,CellStyle cellStyle) {

        FormulaEvaluator evaluator = turnoverWorkbook.getCreationHelper().createFormulaEvaluator();
        Sheet turnoverSheet = turnoverWorkbook.getSheet("营业额");
        for (int i = 1; i <= turnoverSheet.getLastRowNum(); i++) {
            Row oldRow = turnoverSheet.getRow(i);
            if (oldRow != null) {
                Row row  = sheet.createRow(sheet.getLastRowNum()+1);
                copyRow(row,oldRow,cellStyle,evaluator);
            }

        }
    }

    private void copyRow(Row a,Row b,CellStyle cellStyle,FormulaEvaluator evaluator) {

        for (int i =0;i<b.getLastCellNum(); i++){
            Cell bCell = b.getCell(i);

            if (bCell != null) {
                int bType = bCell.getCellType();
                Cell aCell = a.createCell(i);
                aCell.setCellType(bCell.getCellType());
                if (i==0) {
                    aCell.setCellStyle(cellStyle);
                }

                if (bType == Cell.CELL_TYPE_STRING) {
                    aCell.setCellValue(bCell.getStringCellValue());
                } else if (bType == Cell.CELL_TYPE_NUMERIC) {
                    aCell.setCellValue(bCell.getNumericCellValue());

                }
                if (bType == Cell.CELL_TYPE_FORMULA) {
                    if (i>17) {
                        int type = evaluator.evaluateFormulaCell(bCell);
                        aCell.setCellType(type);
                        if (type == Cell.CELL_TYPE_STRING) {
                            aCell.setCellValue(bCell.getStringCellValue());
                            System.out.println(aCell.getStringCellValue());
                        } else if (type == Cell.CELL_TYPE_NUMERIC) {
                            aCell.setCellValue(bCell.getNumericCellValue());
                            System.out.println(aCell.getNumericCellValue());
                        }

                    }else{
                        aCell.setCellFormula(bCell.getCellFormula());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        TurnoverService turnoverService = new TurnoverServiceImpl();
        turnoverService.establish("C:\\Users\\swh\\Desktop\\2019-09");
    }
}
