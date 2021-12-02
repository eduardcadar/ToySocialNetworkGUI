package com.toysocialnetworkgui.utils;

import java.time.LocalDate;

public class UserFriendDTO {

    private String firstName, lastName, email;
    private LocalDate date;
    public  UserFriendDTO(String firstName, String lastName, String email, LocalDate date){
        this.firstName = firstName;
        this.lastName = lastName;
        this.date  = date;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String toString(){
        return firstName + " | " + lastName + " | " + date.toString();
    }

    public LocalDate getDate(){
        return date;
    }

}
