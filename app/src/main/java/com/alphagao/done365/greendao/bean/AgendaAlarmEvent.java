package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Alpha on 2017/3/14.
 */
@Entity
public class AgendaAlarmEvent {

    public static final int YES = 1;
    public static final int NO = 0;

    public static final int WEEK_MON = 1;
    public static final int WEEK_TUE = 2;
    public static final int WEEK_WED = 3;
    public static final int WEEK_THU = 4;
    public static final int WEEK_FRI = 5;
    public static final int WEEK_SAT = 6;
    public static final int WEEK_SUN = 7;

    @Id(autoincrement = true)
    private Long id;
    private Long agendaId;//相关的日程表 ID
    private Integer year;//哪一年
    private Integer month;//一年的哪一月
    private Integer weekday;//一周的哪一天
    private Integer day;//一月的哪一天
    private Integer hour;//一天的哪一个小时
    private Integer minute;//一个小时的哪一分钟
    private Integer vibrative;//是否震动
    private Integer intervalTime;//间隔数量
    private Integer intervalType;//间隔方式：天、周、月、年、小时、分钟
    private String nextAlarmDate;//下一次提醒时间

    @Generated(hash = 694498849)
    public AgendaAlarmEvent(Long id, Long agendaId, Integer year, Integer month,
                            Integer weekday, Integer day, Integer hour, Integer minute,
                            Integer vibrative, Integer intervalTime, Integer intervalType,
                            String nextAlarmDate) {
        this.id = id;
        this.agendaId = agendaId;
        this.year = year;
        this.month = month;
        this.weekday = weekday;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.vibrative = vibrative;
        this.intervalTime = intervalTime;
        this.intervalType = intervalType;
        this.nextAlarmDate = nextAlarmDate;
    }

    @Generated(hash = 222943457)
    public AgendaAlarmEvent() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgendaId() {
        return this.agendaId;
    }

    public void setAgendaId(Long agendaId) {
        this.agendaId = agendaId;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return this.month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getWeekday() {
        return this.weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public Integer getDay() {
        return this.day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return this.hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return this.minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getVibrative() {
        return this.vibrative;
    }

    public void setVibrative(Integer vibrative) {
        this.vibrative = vibrative;
    }

    public Integer getIntervalTime() {
        return this.intervalTime;
    }

    public void setIntervalTime(Integer intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Integer getIntervalType() {
        return this.intervalType;
    }

    public void setIntervalType(Integer intervalType) {
        this.intervalType = intervalType;
    }

    public String getNextAlarmDate() {
        return this.nextAlarmDate;
    }

    public void setNextAlarmDate(String nextAlarmDate) {
        this.nextAlarmDate = nextAlarmDate;
    }
}
