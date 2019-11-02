package com.sun.swh.work.tool.service;

/**
 * @Auther: swh
 * @Date: 2019/8/23 21:43
 * @Description: 进货额汇总，对一个月的进货额进行汇总
 */
public interface PurchaseService {

    /**
     * 对选择目录下的日报表的进货额进行汇总，并把数据保存到此目录下
     * @param filePath 需要汇总的日报表目录
     */
    void establish(String filePath);
}
