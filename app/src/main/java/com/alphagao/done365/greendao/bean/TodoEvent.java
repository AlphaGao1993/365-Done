package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Alpha on 2017/3/10.
 * 待办事项表
 */
@Entity
public class TodoEvent {


    public static final int YES = 1;
    public static final int NO = 0;

    public static final int TODO_UPDATE = 10006;
    public static final int TODO_SORT = 10007;

    @Id(autoincrement = true)
    private Long id;
    private String content;//待办内容
    private String dueDate;//截止日期
    private Long needTimes;//持续时间（毫秒）
    private Long projectId;//关联项目
    private Long contextId;//关联情境ID
    private Integer deadAlarm;//是否临终提醒
    private Integer overdued;//是否逾期
    private Integer finished;//是否完成
    private String finishTime;//完成时间
    private Integer deleted;//是否删除
    private String deleteTime;//删除时间


    @Keep
    public TodoEvent(Long id, String content, String dueDate, Long needTimes,
                     Long projectId, Long contextId, Integer deadAlarm, Integer overdued,
                     Integer finished, String finsihTime, Integer deleted, String deleteTime) {
        this.id = id;
        this.content = content;
        this.dueDate = dueDate;
        this.needTimes = needTimes;
        this.projectId = projectId;
        this.contextId = contextId;
        this.deadAlarm = deadAlarm;
        this.overdued = overdued;
        this.finished = finished;
        this.finishTime = finsihTime;
        this.deleted = deleted;
        this.deleteTime = deleteTime;
    }

    @Keep
    public TodoEvent() {
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

    public String getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Long getNeedTimes() {
        return this.needTimes;
    }

    public void setNeedTimes(Long needTimes) {
        this.needTimes = needTimes;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getDeadAlarm() {
        return this.deadAlarm;
    }

    public void setDeadAlarm(Integer deadAlarm) {
        this.deadAlarm = deadAlarm;
    }

    public Integer getOverdued() {
        return this.overdued;
    }

    public void setOverdued(Integer overdued) {
        this.overdued = overdued;
    }

    public Integer getFinished() {
        return this.finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public String getFinishTime() {
        return this.finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getDeleted() {
        return this.deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Long getContextId() {
        return this.contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    public String getDeleteTime() {
        return this.deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }
}
