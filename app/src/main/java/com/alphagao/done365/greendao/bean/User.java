package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

/**
 * Created by Alpha on 2017/4/10.
 */

@Entity
public class User {
    @Id(autoincrement = true)
    private Long id;
    private String userName;
    private String password;
    private String email;
    private String signDate;
    private String birthday;

    @Keep
    public User(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    @Keep
    public User() {
    }

    @Generated(hash = 1047699857)
    public User(Long id, String userName, String password, String email,
                String signDate, String birthday) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.signDate = signDate;
        this.birthday = birthday;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignDate() {
        return this.signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
