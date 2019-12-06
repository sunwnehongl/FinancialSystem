package com.sun.swh.work.tool.service.impl;

import com.sun.swh.work.tool.WorkbookUtil;
import com.sun.swh.work.tool.bean.Purchase;
import com.sun.swh.work.tool.bean.StoreExcel;
import com.sun.swh.work.tool.cache.SpecificationsCache;
import com.sun.swh.work.tool.service.PurchaseService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: swh
 * @Date: 2019/8/23 21:50
 * @Description: 进货额汇总，对一个月的进货额进行汇总接口的实现类
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final String MODEL_EXCEL_FILE_NAME = "进货额汇总.xlsx";


    @Override
    public void establish(String filePath) {
        List<StoreExcel> storeExcelList = getStoreExcelList(filePath);
        List<Purchase> purchaseList = getPurchaseList(storeExcelList);
        XSSFWorkbook workbook = WorkbookUtil.getWorkbook(MODEL_EXCEL_FILE_NAME,true);
        writePurchaseToWorkbook(workbook,purchaseList);
        writePurchaseToExcel(workbook,filePath);
    }

    /**
     * 获取指定目录下的每家店的报表对象
     * @param filePath 报表的目录
     * @return 每家店的月度报表对象集合
     */
    private List<StoreExcel> getStoreExcelList(String filePath){
        Map<String, Map<String, Double>> SPECIFICATIONS = SpecificationsCache.getInstinse().getSpecificationsCache();
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
     *
     * @param storeExcelList
     * @return
     */
    private List<Purchase> getPurchaseList(List<StoreExcel> storeExcelList){
        List<Purchase> purchaseList = new ArrayList<>();
        for (StoreExcel storeExcel : storeExcelList) {
            purchaseList.addAll(analysisExcel(storeExcel));
        }
        return purchaseList;
    }

    private List<Purchase> analysisExcel(StoreExcel storeExcel) {
        List<Purchase> purchaseList = new ArrayList<>();
        XSSFWorkbook workbook = WorkbookUtil.getWorkbook(storeExcel.getFilePath(),false);
        XSSFSheet sheet = workbook.getSheet("日报表");
        if (sheet != null) {
            Date date = null;
            Purchase purchase = null;
            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }
                if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
                    try {
                        date = cell.getDateCellValue();
                    } catch (Exception e) {
                    }
                }

                if (cell.getCellType()==Cell.CELL_TYPE_STRING && "合计".equals(cell.getStringCellValue())) {
                    purchase = getPurchase(storeExcel.getStoreName(),row,date);
                }

                if (cell.getCellType()==Cell.CELL_TYPE_STRING && "折扣".equals(cell.getStringCellValue())) {
                    if (purchase != null) {
                        Cell discountCell = row.getCell(13);
                        if (discountCell != null) {
                            purchase.setDiscount(discountCell.getNumericCellValue());
                        }
                        purchaseList.add(purchase);
                    }
                }
            }
        }
        return purchaseList;
    }

    private Purchase getPurchase(String storeName,Row row,Date date){
        Purchase purchase = new Purchase();
        purchase.setTime(date);
        purchase.setStoreName(storeName);
        purchase.setPurchaseCount(row.getCell(17).getNumericCellValue());
        purchase.setTransferAmount(row.getCell(18).getNumericCellValue());
        purchase.setRedeployedAmount(row.getCell(19).getNumericCellValue());
        purchase.setReportLoss(row.getCell(20).getNumericCellValue());
        purchase.setTrialEatingHospitality(row.getCell(21).getNumericCellValue());
        purchase.setLessGoods(row.getCell(22).getNumericCellValue());
        purchase.setMoreGoods(row.getCell(23).getNumericCellValue());
        return purchase;
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


    private void writePurchaseToWorkbook(XSSFWorkbook workbook,List<Purchase> purchaseList) {
        Sheet sheet = workbook.getSheetAt(0);

        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
        for (int i = 0; i < purchaseList.size(); i++) {
            Purchase purchase = purchaseList.get(i);
            Row row = sheet.createRow(i + 1);

            Cell cell =row.createCell(0);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(purchase.getTime());

            Cell storeNameCell =row.createCell(1);
            storeNameCell.setCellValue(purchase.getStoreName());


            Cell purchaseCountCell =row.createCell(2);
            purchaseCountCell.setCellValue(purchase.getPurchaseCount());

            Cell transferAmountCell =row.createCell(3);
            transferAmountCell.setCellValue(purchase.getTransferAmount());

            Cell redeployedAmountCell =row.createCell(4);
            redeployedAmountCell.setCellValue(purchase.getRedeployedAmount());

            Cell reportLossCell =row.createCell(5);
            reportLossCell.setCellValue(purchase.getReportLoss());

            Cell trialEatingHospitalityCell =row.createCell(6);
            trialEatingHospitalityCell.setCellValue(purchase.getTrialEatingHospitality());

            Cell lessGoodsCell =row.createCell(7);
            lessGoodsCell.setCellValue(purchase.getLessGoods());

            Cell moreGoodsCell =row.createCell(8);
            moreGoodsCell.setCellValue(purchase.getMoreGoods());

            Cell discountCell = row.createCell(9);
            discountCell.setCellValue(purchase.getDiscount());
        }

    }

    private void writePurchaseToExcel(XSSFWorkbook workbook,String filePath) {
        FileOutputStream fileOutputStream = null;
        try {
            Date date = new Date();
            String strDateFormat = "yyyyMMddHHmmss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            File file = new File(filePath+File.separator+"进货额汇总"+sdf.format(date)+".xlsx");
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

}
