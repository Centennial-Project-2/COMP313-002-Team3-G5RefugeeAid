package com.comp313002.team3.models;

public class G5UserData {

    public UserType userType;
    public String email;

    public G5UserData(){

    }
    public G5UserData( String email, UserType userType) {
        this.email = email;
        this.userType = userType;
    }


}
