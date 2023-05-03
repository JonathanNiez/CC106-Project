package com.example.cc106project;

import com.google.firebase.firestore.auth.User;

public class Users {

    String firstName;
    String lastName;
    String profilePic;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePic() {
        return profilePic;
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

    public Users(){}

    public Users(String firstName, String lastName, String profilePic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePic = profilePic;
    }
}
