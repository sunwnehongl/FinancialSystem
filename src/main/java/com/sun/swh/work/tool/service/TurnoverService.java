package com.sun.swh.work.tool.service;

/**
 * @Auther: swh
 * @Date: 2019/8/26 21:49
 * @Description:
 */
public interface TurnoverService {
    /**
     * 对选择目录下的日报表的营业额进行汇总，并把数据保存到此目录下
     * @param filePath 需要汇总的日报表目录
     */
    void establish(String filePath);
}
