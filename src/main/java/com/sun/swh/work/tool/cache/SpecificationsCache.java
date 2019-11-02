package com.sun.swh.work.tool.cache;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: swh
 * @Date: 2019/8/22 22:24
 * @Description:
 */
public class SpecificationsCache {

    private static final String MODEL_EXCEL_FILE_NAME = "/门店规格.xlsx";

    private static final Map<String, Map<String, Double>> SPECIFICATIONS = new HashMap<>();

    private static SpecificationsCache specificationsCache = new SpecificationsCache();

    private SpecificationsCache(){
        XSSFWorkbook workbook = getModelExcel();
        initDate(workbook);
    }

    /**
     * 得到门店规格Excel的工作簿
     *
     * @return 门店规格Excel的工作簿
     */
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

    /**
     * 初始化缓存的数据
     *
     * @param workbook 门店规格Excel的工作簿
     */
    private void initDate(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheetAt(0);

        List<String> storeList = getStoreName(sheet);

        int rowSize = sheet.getLastRowNum() -2;
        for (int i = 2; i < rowSize; i++) {
            Row row = sheet.getRow(i);
            String typeName = row.getCell(1).getStringCellValue();
            for (int j = 0; j < storeList.size(); j++) {
                String storName = storeList.get(j);
                if (SPECIFICATIONS.get(storName) == null) {
                    SPECIFICATIONS.put(storName,new HashMap<>());
                }
                Cell cell = row.getCell(j+2);
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                SPECIFICATIONS.get(storName).put(typeName,cell.getNumericCellValue());
            }

        }
    }

    private List<String> getStoreName(XSSFSheet sheet) {
        List<String> storeList = new ArrayList<>();
        Row row = sheet.getRow(1);
        for (int i = 2; i < row.getLastCellNum(); i++) {
            storeList.add(row.getCell(i).getStringCellValue());
        }
        return  storeList;
    }

    public static SpecificationsCache getInstinse() {
        return specificationsCache;
    }

    public  Map<String, Map<String, Double>> getSpecificationsCache(){
        return SPECIFICATIONS;
    }

}
