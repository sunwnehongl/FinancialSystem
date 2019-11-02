package com.sun.swh.work.tool.service.impl;

import com.sun.swh.work.tool.bean.Royalty;
import com.sun.swh.work.tool.bean.StorePromotion;
import com.sun.swh.work.tool.bean.Target;
import com.sun.swh.work.tool.service.RoyaltyService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: swh
 * @Date: 2019/8/30 22:30
 * @Description:
 */
public class RoyaltyServiceImpl implements RoyaltyService {


    @Override
    public void computationalRoyalty(String filePath) {
        XSSFWorkbook workbook = getWorkbook(filePath);
        List<Royalty> list = getRoyalty(workbook);


        List<StorePromotion> storePromotionList = listStorePromotion(list);
        String path = this.getClass().getClassLoader().getResource("").getPath()+"任务.xlsx";
        Map<String, Target> targetMap = stringTargetMap(path);
        setStorePromotion(storePromotionList, targetMap);
        storePromotionList = formatRoyalty(storePromotionList);
        storePromotionList = mergeBySalesman(storePromotionList);
        XSSFWorkbook dataWorkbook = writeRoyaltyToWorkbook(storePromotionList);
        writeRoyaltyToExcel(dataWorkbook,filePath.substring(0,filePath.lastIndexOf(File.separator)));
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

    /**
     * 解析营业额汇总中的数据
     *
     * @param workbook 营业额的Excel
     * @return 营业额数据
     */
    private List<Royalty> getRoyalty(XSSFWorkbook workbook) {
        List<Royalty> royaltyList = new ArrayList<>();
        Sheet sheet = workbook.getSheet("营业额");
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Cell timeCell = row.getCell(0);
            if (timeCell == null || timeCell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                continue;
            }
            Date date = timeCell.getDateCellValue();

            Cell storeCell = row.getCell(20);
            if (storeCell == null || storeCell.getCellType() != Cell.CELL_TYPE_STRING) {
                continue;
            }
            String storeName = storeCell.getStringCellValue();

            Cell salesmanCell = row.getCell(1);
            if (salesmanCell == null || salesmanCell.getCellType() != Cell.CELL_TYPE_STRING) {
                continue;
            }
            String salesman = salesmanCell.getStringCellValue();

            Cell turnoverCell = row.getCell(18);
            if (turnoverCell == null || turnoverCell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                continue;
            }
            double turnover = turnoverCell.getNumericCellValue();

            Royalty royalty = new Royalty(storeName, date, salesman, turnover);
            royaltyList.add(royalty);

        }
        return royaltyList;
    }

    private List<StorePromotion> formatRoyalty(List<StorePromotion> storePromotionList) {
        return storePromotionList.stream()
                .map(storePromotion -> {
                    String[] salesmanArray = storePromotion.getSalesman().split("/");
                    StorePromotion[] royalties = new StorePromotion[salesmanArray.length];
                    double sumTime = 0;
                    for (int i = 0; i < salesmanArray.length; i++) {

                        StorePromotion storePromotionTemp = new StorePromotion();
                        storePromotionTemp.setStoreName(storePromotion.getStoreName());
                        String salesman = salesmanArray[i];

                        if (salesman.contains("半")) {
                            storePromotionTemp.setAttendance(storePromotion.getAttendance()/2);
                        }else{
                            storePromotionTemp.setAttendance(storePromotion.getAttendance());
                        }
                        int index = -1;
                        if ((index = salesman.indexOf("(")) > 0) {
                            salesman = salesman.substring(0,index);
                        }
                        storePromotionTemp.setSalesman(salesman);
                        storePromotionTemp.setTurnover(storePromotion.getTurnover());

                        storePromotionTemp.setFirstRoyalty(storePromotion.getFirstRoyalty());
                        storePromotionTemp.setSecondRoyalty(storePromotion.getSecondRoyalty());
                        storePromotionTemp.setRoyalty(storePromotion.getFirstRoyalty());
                        sumTime += storePromotionTemp.getAttendance();
                        royalties[i] = storePromotionTemp;
                    }
                   for (StorePromotion royaltyTemp : royalties) {
                       royaltyTemp.setFirstRoyalty(royaltyTemp.getFirstRoyalty()*(royaltyTemp.getAttendance()/sumTime));
                       royaltyTemp.setSecondRoyalty(royaltyTemp.getSecondRoyalty()*(royaltyTemp.getAttendance()/sumTime));
                       royaltyTemp.setRoyalty(royaltyTemp.getFirstRoyalty() + royaltyTemp.getSecondRoyalty());
                   }
                    return royalties;
                })
                .flatMap(l -> Arrays.stream(l))
                .collect(Collectors.toList());
    }

    private List<StorePromotion> mergeBySalesman(List<StorePromotion> storePromotionList) {
        List<StorePromotion> list = new ArrayList<>();
        Map<String,List<StorePromotion>> mapRoyalty = storePromotionList.stream()
                .collect(Collectors.groupingBy(storePromotion->storePromotion.getStoreName()+ "_" + storePromotion.getSalesman()));


        mapRoyalty.forEach((k,v)->{
            double turnover = 0;
            double attendance = 0;
            double firstRoyalty = 0;
            double secondRoyalty = 0;
            for (StorePromotion storePromotion : v) {
                turnover += storePromotion.getTurnover();
                attendance += storePromotion.getAttendance();
                firstRoyalty += storePromotion.getFirstRoyalty();
                secondRoyalty +=storePromotion.getSecondRoyalty();
            }
            String storeName = k.split("_")[0];
            String salesman = k.split("_")[1];
            StorePromotion storePromotion = new StorePromotion(storeName,salesman,attendance,turnover);
            storePromotion.setFirstRoyalty(firstRoyalty);
            storePromotion.setSecondRoyalty(secondRoyalty);
            storePromotion.setRoyalty(firstRoyalty + secondRoyalty);
            list.add(storePromotion);

        });
        return list;
    }

    /**
     * 按门店和销售员分类营业额和出勤
     * @param royaltyList
     * @return
     */
    private List<StorePromotion> listStorePromotion(List<Royalty> royaltyList ){
        List<StorePromotion> storePromotionList = new ArrayList<>();
        Map<String,List<Royalty>> mapRoyalty = royaltyList.stream()
                .collect(Collectors.groupingBy(royalty -> royalty.getStoreName() + "_" + royalty.getSalesman()));

        mapRoyalty.forEach((k,v)->{
            double turnover = 0;
            double attendance = 0;
            int maxDate = 0;
            for (Royalty royalty : v) {
                turnover += royalty.getTurnover();
                attendance += royalty.getTimeType();
                maxDate = getDayOfMonth(royalty.getTime());
            }
            String storeName = k.split("_")[0];
            String salesman = k.split("_")[1];
            StorePromotion storePromotion = new StorePromotion(storeName,salesman,attendance,turnover);
            storePromotion.setDaysOfMonth(maxDate);
            storePromotionList.add(storePromotion);

        });
        return storePromotionList;
    }

    private int getDayOfMonth(Date date) {
        Calendar a = Calendar.getInstance();
        a.setTime(date);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 解析对应店设定的目标，
     * @param filePath 文件目录
     * @return 门店名称对应设定的目标
     */
    private Map<String, Target> stringTargetMap(String filePath) {
        Map<String, Target> targetMap = new HashMap<>();
        XSSFWorkbook workbook = getWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i= 2;i <sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            String storeName = row.getCell(0).getStringCellValue();
            double firstGoal =  row.getCell(1).getNumericCellValue();
            double secondGoal =  row.getCell(2).getNumericCellValue();
            Target target = new Target(storeName,firstGoal,secondGoal);
            targetMap.put(storeName, target);
        }
        return targetMap;
    }

    /**
     * 根据门店的任务来计算每个门店销售员的提成工资
     * @param storePromotionList 工资提成
     * @param targetMap 门店任务
     */
    private void setStorePromotion(List<StorePromotion> storePromotionList,Map<String, Target> targetMap){

        storePromotionList.forEach(storePromotion -> {
            String storeName = storePromotion.getStoreName();
            Target target = targetMap.get(storeName);
            if (target != null) {
                int dayOfMonth = storePromotion.getDaysOfMonth();

                double turnover = storePromotion.getTurnover();
                double attendance = storePromotion.getAttendance();
                double firstGoal =  (target.getFirstGoal()/dayOfMonth)*attendance;
                double secondGoal = (target.getSecondGoal()/dayOfMonth)*attendance;
                double firstRoyalty = 0;
                double secondRoyalty = 0;
                if (storeName.equals("地铁一店")){
                    System.out.println("11");
                }
                if (turnover<=secondGoal&&turnover>firstGoal) {
                    firstRoyalty = (turnover - firstGoal) * 0.1;
                } else if (turnover >secondGoal) {
                    firstRoyalty = (secondGoal- firstGoal) * 0.1;
                    secondRoyalty = (turnover - secondGoal) * 0.15;
                }
                storePromotion.setFirstRoyalty(firstRoyalty);
                storePromotion.setSecondRoyalty(secondRoyalty);
                storePromotion.setRoyalty(firstRoyalty + secondRoyalty);
            }
        });

    }

    /**
     * 把Excel写到入到文件
     * @param workbook
     * @param filePath
     */
    private void writeRoyaltyToExcel(XSSFWorkbook workbook,String filePath) {
        FileOutputStream fileOutputStream = null;
        try {
            Date date = new Date();
            String strDateFormat = "yyyyMMddHHmmss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            File file = new File(filePath+File.separator+"工资提成"+sdf.format(date)+".xlsx");
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

    private XSSFWorkbook writeRoyaltyToWorkbook(List<StorePromotion> storePromotionList) {
        String path = this.getClass().getClassLoader().getResource("").getPath()+"提成.xlsx";
        XSSFWorkbook workbook = getWorkbook(path);
        Sheet sheet1 = workbook.getSheetAt(0);
        int i =1;
        for (StorePromotion storePromotion : storePromotionList) {
            Row row = sheet1.createRow(i );

            Cell cell =row.createCell(0);
            cell.setCellValue(storePromotion.getStoreName());

            cell =row.createCell(1);
            cell.setCellValue(storePromotion.getSalesman());

            cell =row.createCell(2);
            cell.setCellValue(storePromotion.getAttendance());

            cell =row.createCell(3);
            cell.setCellValue(storePromotion.getTurnover());

            cell =row.createCell(4);
            cell.setCellValue(storePromotion.getFirstRoyalty());

            cell =row.createCell(5);
            cell.setCellValue(storePromotion.getSecondRoyalty());

            cell =row.createCell(6);
            cell.setCellValue(storePromotion.getRoyalty());
            i++;
        }
        return workbook;
    }

    public static void main(String[] args) {
        RoyaltyServiceImpl royaltyServic = new RoyaltyServiceImpl();
        royaltyServic.computationalRoyalty("D:\\营业额汇总20191005191701.xlsx");
    }


}
