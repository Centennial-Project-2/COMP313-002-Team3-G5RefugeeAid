package com.comp313002.team3.g5refugeeaid.models;

import java.util.Date;

public class G5UserData {

    public UserType userType;
    public String email;
    public String fName;
    public String lName;
    public String unNumber;
    public Date dateOfBirth;
    public String nationality;
    public String phoneNumber;

    public G5UserData(){

    }
    public G5UserData( String email, UserType userType) {
        this.email = email;
        this.userType = userType;
    }


}
