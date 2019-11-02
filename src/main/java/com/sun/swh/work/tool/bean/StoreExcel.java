package com.sun.swh.work.tool.bean;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @Auther: swh
 * @Date: 2019/8/23 22:10
 * @Description: 每家店的月度报表对象
 */
public class StoreExcel {
    /**
     * 点名
     */
    private String storeName;

    /**
     * 每家店的Excel对应的XSSFWorkbook
     */
    private String filePath;

    public StoreExcel(String storeName, String filePath) {
        this.storeName = storeName;
        this.filePath = filePath;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
