package com.xt.android.rant.wrapper;

/**
 * Created by admin on 2017/5/10.
 */
public class StarNotifyItem {
    private Integer starId;

    private Integer userId;

    private Integer rantId;

    private Integer starValue;

    private Integer starRead;

    //new

    private String userName;

    private String userAvatar;

    private String rantContent;

    public Integer getStarId() {
        return starId;
    }

    public void setStarId(Integer starId) {
        this.starId = starId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRantId() {
        return rantId;
    }

    public void setRantId(Integer rantId) {
        this.rantId = rantId;
    }

    public Integer getStarValue() {
        return starValue;
    }

    public void setStarValue(Integer starValue) {
        this.starValue = starValue;
    }

    public Integer getStarRead() {
        return starRead;
    }

    public void setStarRead(Integer starRead) {
        this.starRead = starRead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getRantContent() {
        return rantContent;
    }

    public void setRantContent(String rantContent) {
        this.rantContent = rantContent;
    }
}
