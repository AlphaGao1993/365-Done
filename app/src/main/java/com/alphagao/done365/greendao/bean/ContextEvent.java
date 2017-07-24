package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Alpha on 2017/3/10.
 */
@Entity
public class ContextEvent {
    public static final int CONTEXT_UPDATE = 100013;
    public static final int CONTEXT_ADD = 100014;


    @Id(autoincrement = true)
    private Long id;
    private String name;
    private Long upContextId;//父情境
    private Integer level;//当前情境级别
    private String longitude;//经度
    private String latitude;//纬度
    private String position;//位置描述

    @Generated(hash = 1432952682)
    public ContextEvent(Long id, String name, Long upContextId, Integer level,
                        String longitude, String latitude, String position) {
        this.id = id;
        this.name = name;
        this.upContextId = upContextId;
        this.level = level;
        this.longitude = longitude;
        this.latitude = latitude;
        this.position = position;
    }

    @Generated(hash = 1025605737)
    public ContextEvent() {
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

    public Long getUpContextId() {
        return this.upContextId;
    }

    public void setUpContextId(Long upContextId) {
        this.upContextId = upContextId;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "ContextEvent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", upContextId=" + upContextId +
                ", level=" + level +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
