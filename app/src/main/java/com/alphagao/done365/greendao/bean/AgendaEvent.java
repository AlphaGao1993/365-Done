package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Alpha on 2017/3/10.
 */
@Entity
public class AgendaEvent {

    public static final int YES = 1;
    public static final int NO = 0;

    public static final int TYPE_MINUTE = 1;
    public static final int TYPE_HOUR = 2;
    public static final int TYPE_DAY = 3;
    public static final int TPYE_WEEK = 4;
    public static final int TPYE_MONTH = 5;
    public static final int TPYE_YEAR = 6;

    public static final int SORT = 10041;
    public static final int AGENDA_UPDATE = 10010;
    public static final int AGENDA_CALENDAR_TODAY = 10011;
    public static final int AGENDA_CALENDAR_WEEK = 10012;
    public static final int AGENDA_CALENDAR_MONTH = 10013;
    public static final int AGENDA_CALENDAR_LIST = 10014;

    @Id(autoincrement = true)
    private Long id;// 日程表 id
    private String content;//日程内容
    private String createDate;//创建日期
    private String alarmTime;//提醒时间
    private String alarmDate;//提醒日期
    private Long alarmTimeMills;//完整提醒日期的毫秒值
    private Integer needTimeNum;//持续时间的数量
    private Integer needTimeType;//持续时间的单位类型
    private Long neddTimeMills;//持续时间毫秒数
    private Integer delayTime;//推迟次数
    private Long contextId;//情境ID
    private Long projectId;//所属项目，可以为空
    private Integer overdued;//是否过期
    private Integer finished;//是否完成
    private String finishTime;//完成时间
    private Integer deleted;//是否删除
    private String deleteTime;//删除时间


    @Keep
    public AgendaEvent(Long id, String content, String createDate, String alarmTime,
                       String alarmDate, Long alarmTimeMills, Integer needTimeNum,
                       Integer needTimeType, Long neddTimeMills,
                       Integer delayTime, Long contextId, Long projectId, Integer overdued,
                       Integer finished, String finishTime, Integer deleted, String deleteTime) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.alarmTime = alarmTime;
        this.alarmDate = alarmDate;
        this.alarmTimeMills = alarmTimeMills;
        this.needTimeNum = needTimeNum;
        this.needTimeType = needTimeType;
        this.neddTimeMills = neddTimeMills;
        this.delayTime = delayTime;
        this.contextId = contextId;
        this.projectId = projectId;
        this.overdued = overdued;
        this.finished = finished;
        this.finishTime = finishTime;
        this.deleted = deleted;
        this.deleteTime = deleteTime;
    }

    @Keep
    public AgendaEvent() {
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

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getAlarmTime() {
        return this.alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmDate() {
        return this.alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }

    public Long getAlarmTimeMills() {
        return alarmTimeMills;
    }

    public void setAlarmTimeMills(Long alarmTimeMills) {
        this.alarmTimeMills = alarmTimeMills;
    }

    public Integer getNeedTimeNum() {
        return this.needTimeNum;
    }

    public void setNeedTimeNum(Integer needTimeNum) {
        this.needTimeNum = needTimeNum;
    }

    public Integer getNeedTimeType() {
        return this.needTimeType;
    }

    public void setNeedTimeType(Integer needTimeType) {
        this.needTimeType = needTimeType;
    }

    public Long getNeddTimeMills() {
        return this.neddTimeMills;
    }

    public void setNeddTimeMills(Long neddTimeMills) {
        this.neddTimeMills = neddTimeMills;
    }

    public Integer getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(Integer delayTime) {
        this.delayTime = delayTime;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
