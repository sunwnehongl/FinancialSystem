package com.sun.swh.work.tool.bean;

/**
 * @Auther: swh
 * @Date: 2019/9/1 15:16
 * @Description:
 */
public class Target {
    private String storeName;
    private double firstGoal;
    private double secondGoal;

    public Target() {
    }
    public Target(String storeName, double firstGoal, double secondGoal) {
        this.storeName = storeName;
        this.firstGoal = firstGoal;
        this.secondGoal = secondGoal;
    }

    public String getStoreName() {
        return storeName;
    }

    public double getFirstGoal() {
        return firstGoal;
    }

    public double getSecondGoal() {
        return secondGoal;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setFirstGoal(double firstGoal) {
        this.firstGoal = firstGoal;
    }

    public void setSecondGoal(double secondGoal) {
        this.secondGoal = secondGoal;
    }
}
