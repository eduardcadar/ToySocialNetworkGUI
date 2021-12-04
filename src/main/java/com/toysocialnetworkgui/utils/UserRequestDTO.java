package com.toysocialnetworkgui.utils;

import com.toysocialnetworkgui.domain.REQUESTSTATE;

import java.time.LocalDate;

/**
 *
 */
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private REQUESTSTATE state;
    private LocalDate sentDate;
    private String email;

    /**
     * Creates an object DTO to encapsulate the email of the user that received the request, the state and the date of sending
     * @param firstName String
     * @param lastName String
     * @param state - ENUM Requeststate
     * @param date - localDate
     * @param email - String the email for the other user in friendRequest
     */
    public UserRequestDTO(String firstName,String lastName, REQUESTSTATE state, LocalDate date, String email){
        this.firstName =  firstName;
        this.lastName =  lastName;
        this.state = state;
        this.sentDate = date;
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserRequestDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", state=" + state +
                ", sentDate=" + sentDate +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Gets the first name
     * @return String
     */
    public String getFirstName(){
        return firstName;
    }

    /**
     * Gets the first name
     * @return String
     */
    public String getLastName(){
        return lastName;
    }
    /**
     * Gets the state of the request
     * @return String
     */

    public REQUESTSTATE getState(){
        return state;
    }

    /**
     * Gets the local date of the time when request was sent
     * @return LocalDate
     */
    public LocalDate getSendDate(){
        return sentDate;
    }

    public String getEmail() {
        return email;
    }
}
