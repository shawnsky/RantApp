package com.xt.android.rant.wrapper;

import java.util.Date;

/**
 * Created by admin on 2017/5/2.
 */
public class CommentItem {
    private Integer commentId;

    private Integer userId;

    private Integer commentHidden;

    private String commentContent;

    private Integer commentValue;

    private Date commentDate;

    private Integer rantId;

    private Integer commentRead;

    //新增属性
    private String userName;

    private String userAvatar;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCommentHidden() {
        return commentHidden;
    }

    public void setCommentHidden(Integer commentHidden) {
        this.commentHidden = commentHidden;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Integer getCommentValue() {
        return commentValue;
    }

    public void setCommentValue(Integer commentValue) {
        this.commentValue = commentValue;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public Integer getRantId() {
        return rantId;
    }

    public void setRantId(Integer rantId) {
        this.rantId = rantId;
    }

    public Integer getCommentRead() {
        return commentRead;
    }

    public void setCommentRead(Integer commentRead) {
        this.commentRead = commentRead;
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
}
