package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Alpha on 2017/4/10.
 */

@Entity
public class Festival {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String date;
    @Keep
    public Festival(Long id, String name, String date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }
    @Keep
    public Festival() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
