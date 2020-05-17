package com.lxc.Competitioninformationsystem.entity;

import java.io.Serializable;

public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cmptName;
    private String brief;
    private String coverImageUrl;
    private String detailPageUrl;
    private String notifyTime;

    public String getCmptName() {
        return cmptName;
    }

    public void setCmptName(String cmptName) {
        this.cmptName = cmptName;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getDetailPageUrl() {
        return detailPageUrl;
    }

    public void setDetailPageUrl(String detailPageUrl) {
        this.detailPageUrl = detailPageUrl;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }
}
