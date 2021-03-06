package com.sun.swh.work.tool.service.impl;

import com.sun.swh.work.tool.WorkbookUtil;
import com.sun.swh.work.tool.bean.Repetition;
import com.sun.swh.work.tool.service.RepetitionService;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: swh
 * @Date: 2019/12/6 22:57
 * @Description:
 */
@Component
public class RepetitionServiceImpl implements RepetitionService {

    @Override
    public void writeRepetition(String path, String filePath) {
        Repetition repetition = getRepetition(filePath);
        writeData(path, repetition);
    }

    private void writeData(String path, Repetition repetition) {
        Date time = getTime(repetition.getTime());
        File file = new File(path);
        File[] storeFiles = file.listFiles();
        Map<String, List<Map<String, Double>>> data = repetition.getData();
        for (File sotreFile : storeFiles) {

            if (sotreFile.isFile()) {
                String storeName = getStoreName(sotreFile.getName());
                List<Map<String, Double>> dataList = data.get(storeName);
                if (dataList == null) {
                    continue;
                }

                System.out.println(sotreFile.getPath());
                for (Map<String, Double> dataMap : dataList) {
                    Double timeValue = dataMap.get("TIME");
                    long timelong = Double.valueOf(timeValue).longValue();
                    writeExcel(sotreFile.getPath(),dataMap,new Date(timelong));
                }

            }
        }
    }

    private void writeExcel(String filePath, Map<String, Double> dataMap, Date time) {
        XSSFWorkbook workbook = WorkbookUtil.getWorkbook(filePath, false);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Date date = null;
        int length = sheet.getLastRowNum();
        for (int i = 1; i < length; i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell timeCell = null;
            if (row != null && (timeCell = row.getCell(0)) != null && timeCell.getCellType()==XSSFCell.CELL_TYPE_NUMERIC ) {
                date = timeCell.getDateCellValue();
                continue;
            }
            if (timeCell == null || date == null) {
                continue;
            }

            if (compare(date, time) == 0 && timeCell.getCellType()== XSSFCell.CELL_TYPE_STRING) {
                String type = timeCell.getStringCellValue();
                if (type == null) {
                    continue;
                }
                Double data = dataMap.get(type);
                if (data != null) {
                    row.getCell(9).setCellValue(data);
                }

            }
            if (compare(date, time) < 0) {
                break;
            }
        }
        sheet.setForceFormulaRecalculation(true);
        WorkbookUtil.writeExcel(workbook,filePath);
    }

    public int compare(Date date1,Date date2) {
        setTime(date1);
        setTime(date2);
        if (date1.before(date2)) {
            return 1;
        } else if (date2.before(date1)) {
            return -1;
        }
        return 0;
    }

    private void setTime(Date date1) {
        date1.setHours(0);
        date1.setMinutes(0);
        date1.setSeconds(0);
    }

    private String getStoreName(String fileName) {
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]{0,100}");
        Matcher m = p.matcher(fileName);
        while (m.find()) {
            return m.group(0);
        }
        return "";
    }
    private Date getTime(String time){
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 mm:ss", Locale.CHINA);
        try {
            date = df.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
            date = calendar.getTime();
        } catch (ParseException e) {
            System.out.println("解析出错！");
        }
        return date;

    }

    private Repetition getRepetition(String filePath) {
        HSSFWorkbook workbook = getWorkbook(filePath, false);
        HSSFSheet sheet = workbook.getSheetAt(0);

        Map<Integer, String> handMap = getHanderList(sheet.getRow(0));
        String storeName = null;
        String time = "";
        Map<String, Date> cach = new HashMap<>();
        Map<String,List<Map<String, Double>>> dataMap = new HashMap<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                break;

            }
            time = row.getCell(3).getStringCellValue();
            Date date = cach.get(time);
            if (date == null) {
                date = getTime(time);
            }
            storeName = row.getCell(5).getStringCellValue();
            List<Map<String, Double>> dataList = dataMap.computeIfAbsent(storeName, k -> new ArrayList<>());
            Map<String, Double> map = new HashMap<>();
            for (int j = 9; j < row.getLastCellNum(); j++) {
                String type = handMap.get(j);
                if ("营业额合计".equals(type)) {
                    break;
                }
                map.put(type,row.getCell(j).getNumericCellValue());
            }
            map.put("TIME", Double.valueOf(date.getTime()));
            dataList.add(map);

        }
        Repetition repetition = new Repetition();

        repetition.setTime(time);
        repetition.setData(dataMap);
        return repetition;
    }

    private Map<Integer, String> getHanderList(HSSFRow row) {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 7; i < row.getLastCellNum(); i++) {
            map.put(i, row.getCell(i).getStringCellValue());
        }
        return map;
    }

    public static HSSFWorkbook getWorkbook(String filePath, boolean isName) {
        HSSFWorkbook sXSSFWorkbook = null;
        InputStream inputStream = null;
        try {
            if (!isName) {
                inputStream = new FileInputStream(filePath);
            } else {
                Resource rs = new ClassPathResource(filePath);
                inputStream = rs.getInputStream();
            }

            sXSSFWorkbook = new HSSFWorkbook(inputStream);
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

    public static void main(String[] args) {
        RepetitionService repetitionService = new RepetitionServiceImpl();
        repetitionService.writeRepetition("D:\\2020-04",
                "C:\\Users\\swh\\Desktop\\20200405151044826.xls");
    }
}
