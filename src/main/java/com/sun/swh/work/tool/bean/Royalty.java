package com.sun.swh.work.tool.bean;

import java.util.Date;

/**
 * @Auther: swh
 * @Date: 2019/8/30 22:32
 * @Description:
 */
public class Royalty {
    /**
     * 店名
     */
    private String storeName;

    /**
     * 时间
     */
    private Date time;

    /**
     * 营业员
     */
    private String salesman;

    /**
     * 营业额
     */
    private double turnover;

    private double timeType = 1.0;

    public Royalty(String storeName, Date time, String salesman, double turnover) {
        this.storeName = storeName;
        this.time = time;
        this.salesman = salesman;
        this.turnover = turnover;
    }

    public String getStoreName() {
        return storeName;
    }

    public Date getTime() {
        return time;
    }

    public String getSalesman() {
        return salesman;
    }

    public double getTurnover() {
        return turnover;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public double getTimeType() {
        return timeType;
    }

    public void setTimeType(double timeType) {
        this.timeType = timeType;
    }

    public Royalty clone(){
        Royalty royalty = new Royalty(String.valueOf(this.storeName), this.time, String.valueOf(this.salesman), this.turnover);
        return royalty;
    }
}
