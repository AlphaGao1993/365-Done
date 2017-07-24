package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Calendar;

/**
 * Created by Alpha on 2017/3/10.
 */
@Entity
public class AlarmType {

    public static final int TYPE_MINUTE = Calendar.MINUTE;
    public static final int TYPE_HOUR = Calendar.HOUR;
    public static final int TYPE_DAY = Calendar.DAY_OF_YEAR;
    public static final int TPYE_WEEK = Calendar.WEEK_OF_YEAR;
    public static final int TPYE_MONTH = Calendar.MONTH;
    public static final int TPYE_YEAR = Calendar.YEAR;

    @Id(autoincrement = true)
    private Long id;
    private String name;//显示名称
    private Integer interval_num;//几日、几周、几月、几年
    private Integer interval_type;//日、周、月、年

    @Generated(hash = 812965345)
    public AlarmType(Long id, String name, Integer interval_num, Integer interval_type) {
        this.id = id;
        this.name = name;
        this.interval_num = interval_num;
        this.interval_type = interval_type;
    }

    @Generated(hash = 255195585)
    public AlarmType() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInterval_num() {
        return this.interval_num;
    }

    public void setInterval_num(Integer interval_num) {
        this.interval_num = interval_num;
    }

    public Integer getInterval_type() {
        return this.interval_type;
    }

    public void setInterval_type(Integer interval_type) {
        this.interval_type = interval_type;
    }
}
