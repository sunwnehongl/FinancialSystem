package com.sun.swh.work.tool;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;

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
            if (!isName) {
                inputStream = new FileInputStream(new File(filePath));
            } else {
                Resource rs = new ClassPathResource(filePath);
                inputStream = rs.getInputStream();
            }
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
}
