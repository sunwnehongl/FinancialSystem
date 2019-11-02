package com.sun.swh.work.tool.bean;

import java.util.Date;

/**
 * @Auther: swh
 * @Date: 2019/8/23 21:52
 * @Description: 进货报表的数据对象
 */
public class Purchase {

    /**
     * 时间
     */
    private Date time;
    /**
     * 店名
     */
    private String storeName;
    /**
     * 进货额
     */
    private double purchaseCount;
    /**
     * 调入金额
     */
    private double transferAmount;
    /**
     * 调出金额
     */
    private double redeployedAmount;
    /**
     * 报损金额
     */
    private double reportLoss;
    /**
     * 试吃招待
     */
    private double trialEatingHospitality;
    /**
     * 少货
     */
    private double lessGoods;
    /**
     * 多额
     */
    private double moreGoods;

    /**
     * 折扣
     */
    private double discount;

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Date getTime() {
        return time;
    }

    public String getStoreName() {
        return storeName;
    }

    public double getPurchaseCount() {
        return purchaseCount;
    }

    public double getTransferAmount() {
        return transferAmount;
    }

    public double getRedeployedAmount() {
        return redeployedAmount;
    }

    public double getReportLoss() {
        return reportLoss;
    }

    public double getTrialEatingHospitality() {
        return trialEatingHospitality;
    }

    public double getLessGoods() {
        return lessGoods;
    }

    public double getMoreGoods() {
        return moreGoods;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setPurchaseCount(double purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public void setTransferAmount(double transferAmount) {
        this.transferAmount = transferAmount;
    }

    public void setRedeployedAmount(double redeployedAmount) {
        this.redeployedAmount = redeployedAmount;
    }

    public void setReportLoss(double reportLoss) {
        this.reportLoss = reportLoss;
    }

    public void setTrialEatingHospitality(double trialEatingHospitality) {
        this.trialEatingHospitality = trialEatingHospitality;
    }

    public void setLessGoods(double lessGoods) {
        this.lessGoods = lessGoods;
    }

    public void setMoreGoods(double moreGoods) {
        this.moreGoods = moreGoods;
    }
}
