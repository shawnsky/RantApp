package com.xt.android.rant.wrapper;


import java.util.List;

/**
 * Created by admin on 2017/5/21.
 */
public class UserItem {
    private Integer userId;

    private Integer userRole;

    private String userName;

    private String userPassword;

    private String userAvatar;

    private String userLocation;

    private String userBio;

    private Integer userValue;

    //new

    private boolean isMyself;

    public boolean isMyself() {
        return isMyself;
    }

    public void setMyself(boolean myself) {
        isMyself = myself;
    }

    private List<RantItem> userRants;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public Integer getUserValue() {
        return userValue;
    }

    public void setUserValue(Integer userValue) {
        this.userValue = userValue;
    }

    public List<RantItem> getUserRants() {
        return userRants;
    }

    public void setUserRants(List<RantItem> userRants) {
        this.userRants = userRants;
    }
}
