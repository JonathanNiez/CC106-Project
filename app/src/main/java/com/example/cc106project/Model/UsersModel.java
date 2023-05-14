package com.example.cc106project.Model;

public class UsersModel {

    String firstName;
    String lastName;
    String profilePic;
    String userID;

    public String getUserID() {
        return userID;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePic() {
        return profilePic;
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


    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public UsersModel() {
    }

    public UsersModel(String firstName, String lastName,
                      String profilePic, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePic = profilePic;
        this.userID = userID;
    }
}
