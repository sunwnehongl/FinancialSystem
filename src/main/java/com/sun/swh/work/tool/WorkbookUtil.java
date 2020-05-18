package com.sun.swh.work.tool;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: swh
 * @Date: 2019/12/6 21:27
 * @Description:
 */
public class WorkbookUtil {

    public static XSSFWorkbook getWorkbook(String filePath, boolean isName) {
        XSSFWorkbook sXSSFWorkbook = null;
        InputStream inputStream = null;
        try {
            if (isName) {
                filePath = System.getProperty("user.dir") + "/config/" + filePath;
            }
            inputStream = new FileInputStream(new File(filePath));
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

    public static void writeExcel(XSSFWorkbook xssfWorkbook, String filePath) {


        FileOutputStream fileOutputStream = null;
        try {
            File storeFile = new File(filePath);
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

    /**
     * 通过文件名得到对应店的名称
     * @param fileName 文件名称
     * @return 店名
     */
    public static String getStorName(String fileName) {
        String storeName = fileName.split("\\.")[0];
        if (storeName.length() > 7) {
            storeName = storeName.substring(0,storeName.length() - 7);
        }
        return storeName;
    }
}
