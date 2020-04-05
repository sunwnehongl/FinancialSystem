package com.sun.swh.work.tool.bean;

import java.util.List;
import java.util.Map;

/**
 * @Auther: swh
 * @Date: 2019/12/6 22:53
 * @Description:
 */
public class Repetition {

    private String time;

    private Map<String,List<Map<String, Double>>> data;

    public Map<String, List<Map<String, Double>>> getData() {
        return data;
    }

    public void setData( Map<String,List<Map<String, Double>>> data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
