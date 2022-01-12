package com.toysocialnetworkgui.utils;

import com.toysocialnetworkgui.domain.User;

public class CommonFriendsDTO {
    private User user;
    private String email;
    private String firstName;
    private String lastName;
    private Integer nrOfCommonFriends;

    public CommonFriendsDTO(User user, Integer nrOfCommonFriends) {
        this.user = user;
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.nrOfCommonFriends = nrOfCommonFriends;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getNrOfCommonFriends() {
        return nrOfCommonFriends;
    }

    public void setNrOfCommonFriends(Integer nrOfCommonFriends) {
        this.nrOfCommonFriends = nrOfCommonFriends;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
