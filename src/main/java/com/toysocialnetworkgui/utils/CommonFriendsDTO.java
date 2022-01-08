package com.toysocialnetworkgui.utils;

import com.toysocialnetworkgui.domain.User;

public class CommonFriendsDTO {
    private User user1, user2;
    private Integer nrOfCommonFriends;

    public CommonFriendsDTO(User user1, User user2, Integer nrOfCommonFriends) {
        this.user1 = user1;
        this.user2 = user2;
        this.nrOfCommonFriends = nrOfCommonFriends;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Integer getNrOfCommonFriends() {
        return nrOfCommonFriends;
    }

    public void setNrOfCommonFriends(Integer nrOfCommonFriends) {
        this.nrOfCommonFriends = nrOfCommonFriends;
    }
}
