package com.sun.swh.work.tool.bean;

/**
 * @Auther: swh
 * @Date: 2019/9/29 22:30
 * @Description:
 */
public class StorePromotion {

    public StorePromotion(String storeName, String salesman, double attendance, double turnover) {
        this.storeName = storeName;
        this.salesman = salesman;
        this.attendance = attendance;
        this.turnover = turnover;
    }

    public StorePromotion() {
    }

    /**
     * 店名
     */
    private String storeName;

    /**
     * 营业员
     */
    private String salesman;

    /**
     * 出勤天数
     */
    private double attendance;

    /**
     * 营业额
     */
    private double turnover;

    /**
     * 一档提成
     */
    private double firstRoyalty;

    /**
     * 二档提成
     */
    private double secondRoyalty;

    /**
     * 总提成
     */
    private double royalty;

    /**
     * 当月的天数
     */
    private int daysOfMonth;


    public String getStoreName() {
        return storeName;
    }

    public String getSalesman() {
        return salesman;
    }

    public double getAttendance() {
        return attendance;
    }

    public double getTurnover() {
        return turnover;
    }

    public double getFirstRoyalty() {
        return firstRoyalty;
    }

    public double getSecondRoyalty() {
        return secondRoyalty;
    }

    public double getRoyalty() {
        return royalty;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public void setFirstRoyalty(double firstRoyalty) {
        this.firstRoyalty = firstRoyalty;
    }

    public void setSecondRoyalty(double secondRoyalty) {
        this.secondRoyalty = secondRoyalty;
    }

    public void setRoyalty(double royalty) {
        this.royalty = royalty;
    }

    public int getDaysOfMonth() {
        return daysOfMonth;
    }

    public void setDaysOfMonth(int daysOfMonth) {
        this.daysOfMonth = daysOfMonth;
    }

}
