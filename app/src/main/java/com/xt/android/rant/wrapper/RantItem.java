package com.xt.android.rant.wrapper;

import java.util.Date;

/**
 * Created by admin on 2017/5/1.
 */

public class RantItem {
    private Integer rantId;

    private Integer userId;

    private Integer rantHidden;

    private String rantContent;

    private Integer rantValue;

    private Date rantDate;

    private String rantAvatar;

    private String userName;

    private Integer commentsNum;

    private Integer thumbFlag;

    public Integer getThumbFlag() {
        return thumbFlag;
    }

    public void setThumbFlag(Integer thumbFlag) {
        this.thumbFlag = thumbFlag;
    }

    public Integer getRantId() {
        return rantId;
    }

    public void setRantId(Integer rantId) {
        this.rantId = rantId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRantHidden() {
        return rantHidden;
    }

    public void setRantHidden(Integer rantHidden) {
        this.rantHidden = rantHidden;
    }

    public String getRantContent() {
        return rantContent;
    }

    public void setRantContent(String rantContent) {
        this.rantContent = rantContent;
    }

    public Integer getRantValue() {
        return rantValue;
    }

    public void setRantValue(Integer rantValue) {
        this.rantValue = rantValue;
    }

    public Date getRantDate() {
        return rantDate;
    }

    public void setRantDate(Date rantDate) {
        this.rantDate = rantDate;
    }

    public String getRantAvatar() {
        return rantAvatar;
    }

    public void setRantAvatar(String rantAvatar) {
        this.rantAvatar = rantAvatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(Integer commentsNum) {
        this.commentsNum = commentsNum;
    }
}
