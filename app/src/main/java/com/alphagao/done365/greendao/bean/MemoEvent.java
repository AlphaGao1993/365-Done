package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Alpha on 2017/3/10.
 */
@Entity
public class MemoEvent {

    public static final int MEMO_UPDATE = 10020;

    public static final int YES = 1;
    public static final int NO = 0;

    @Id(autoincrement = true)
    private Long id;
    private String content;//内容
    private Long dirID;//所属目录
    private Integer isDir;//是否是目录
    private String createDate;//创建日期
    private Integer deleted;//是否删除
    private String deleteTime;//删除时间


    @Keep
    public MemoEvent(Long id, String content, Long dirID, Integer isDir,
                     String createDate, Integer deleted, String deleteTime) {
        this.id = id;
        this.content = content;
        this.dirID = dirID;
        this.isDir = isDir;
        this.createDate = createDate;
        this.deleted = deleted;
        this.deleteTime = deleteTime;
    }

    @Keep
    public MemoEvent() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDirID() {
        return this.dirID;
    }

    public void setDirID(Long dirID) {
        this.dirID = dirID;
    }

    public Integer getIsDir() {
        return this.isDir;
    }

    public void setIsDir(Integer isDir) {
        this.isDir = isDir;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getDeleteTime() {
        return this.deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }
}
