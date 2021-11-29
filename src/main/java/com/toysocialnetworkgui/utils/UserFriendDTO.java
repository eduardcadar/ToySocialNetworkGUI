package com.toysocialnetworkgui.utils;

import java.time.LocalDate;

public class UserFriendDTO {

    private String firstName, lastName;
    private LocalDate date;
    public  UserFriendDTO(String firstName, String lastName, LocalDate date){
        this.firstName = firstName;
        this.lastName = lastName;
        this.date  = date;
    }

    public String toString(){
        return firstName + " | " + lastName + " | " + date.toString();
    }

    public LocalDate getDate(){
        return date;
    }

}
