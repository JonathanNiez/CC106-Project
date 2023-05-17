package com.example.cc106project.Model;

public class UsersModel {

    private String firstName;
    private String lastName;
    private String profilePicUrl;
    private String userID;
    private boolean isOnline;

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getUserID() {
        return userID;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public void setProfilePicUrl(String profilePic) {
        this.profilePicUrl = profilePic;
    }

    public UsersModel() {
    }

    public UsersModel(String firstName, String lastName,
                      String profilePicUrl, String userID, boolean isOnline) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicUrl = profilePicUrl;
        this.userID = userID;
        this.isOnline = isOnline;
    }
}
