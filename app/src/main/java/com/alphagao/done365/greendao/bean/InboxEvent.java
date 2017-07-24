package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Alpha on 2017/3/2.
 * 收件箱卡片，
 */
@Entity
public class InboxEvent {

    public static final int YES = 1;
    public static final int NO = 0;

    public static final int INBOX_ADD = 10002;
    public static final int INBOX_UPDATE = 10003;
    public static final int INBOX_DELETED = 10004;
    public static final int INBOX_REVOKE = 10005;
    public static final int INBOX_REMOVED = 10006;

    @Id(autoincrement = true)
    private Long id;
    private String content;//内容
    private String createTime;//创建日期
    private Integer deleted;//已删除
    private String deleteTime;//删除时间

    @Keep
    public InboxEvent(Long id, String content, String createTime, Integer deleted, String deleteTime) {
        this.id = id;
        this.content = content;
        this.createTime = createTime;
        this.deleted = deleted;
        this.deleteTime = deleteTime;
    }

    @Keep
    public InboxEvent() {
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

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
