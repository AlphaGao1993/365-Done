package com.alphagao.done365.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Alpha on 2017/3/10.
 */
@Entity
public class ProjectEvent {

    public static final int PROJECT_NEW = 30090;
    public static final int PROJECT_FOLDER_NEW = 30091;
    public static final int YES = 1;
    public static final int NO = 0;

    @Id(autoincrement = true)
    private Long id;
    private String name;//项目名称
    private Long upProjectId;//上级项目
    private Integer isDir;//是否目录
    private Float completion;//完成率
    private String relatedMsg;//关联信息
    private String createDate;//创建日期
    private String completeTime;//完成时间


    @Keep
    public ProjectEvent(Long id, String name, Long upProjectId, Integer isDir,
                        Float completion, String relatedMsg, String createDate,
                        String completeTime) {
        this.id = id;
        this.name = name;
        this.upProjectId = upProjectId;
        this.isDir = isDir;
        this.completion = completion;
        this.relatedMsg = relatedMsg;
        this.createDate = createDate;
        this.completeTime = completeTime;
    }

    @Keep
    public ProjectEvent() {
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

    public Long getUpProjectId() {
        return this.upProjectId;
    }

    public void setUpProjectId(Long upProjectId) {
        this.upProjectId = upProjectId;
    }

    public Integer getIsDir() {
        return this.isDir;
    }

    public void setIsDir(Integer isDir) {
        this.isDir = isDir;
    }

    public Float getCompletion() {
        return this.completion;
    }

    public void setCompletion(Float completion) {
        this.completion = completion;
    }

    public String getRelatedMsg() {
        return this.relatedMsg;
    }

    public void setRelatedMsg(String relatedMsg) {
        this.relatedMsg = relatedMsg;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCompleteTime() {
        return this.completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }
}
