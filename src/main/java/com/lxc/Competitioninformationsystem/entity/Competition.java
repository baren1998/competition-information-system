package com.lxc.Competitioninformationsystem.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "competition")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 比赛名称
    @Column(name = "race_name", nullable =  false)
    private String raceName;

    // 举办方类型(1-图片Url 2-举办方名称)
    @Column(name = "sponsor_type", nullable = false)
    private int sponsorType;

    // 举办方内容
    @Column(name = "sponsor_content", nullable = false)
    private String sponsor;

    // 比赛介绍
    @Column(name = "introduction", nullable = false)
    private String brief;

    // 比赛任务
    private String task;

    // 比赛赛程
    private String schedule;

    // 参赛人数
    @Column(name = "team_num", nullable = false)
    private int teamNum;

    // 比赛奖励
    private String reward;

    // 比赛规则
    private String rule;

    // 比赛类型(1-算法赛 2-创新应用赛 3-学习赛)
    private int type;

    // 比赛标签
    private String tag;

    // 数据来源类型(1-爬虫 2-API)
    @Column(name = "data_source_type", nullable = false)
    private int dataSourceType;

    // 比赛开始时间
    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    // 比赛结束时间
    @Column(name = "end_time", nullable = false)
    private Timestamp endTime;

    // 比赛信息被插入数据库的时间
    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    // 比赛图片Url
    @Column(name = "cover_image_url")
    private String iconUrl;

    // 详情页面Url
    @Column(name = "detail_page_url", nullable = false)
    private String detailPageUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public int getSponsorType() {
        return sponsorType;
    }

    public void setSponsorType(int sponsorType) {
        this.sponsorType = sponsorType;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public int getTeamNum() {
        return teamNum;
    }

    public void setTeamNum(int teamNum) {
        this.teamNum = teamNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(int dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDetailPageUrl() {
        return detailPageUrl;
    }

    public void setDetailPageUrl(String detailPageUrl) {
        this.detailPageUrl = detailPageUrl;
    }
}
