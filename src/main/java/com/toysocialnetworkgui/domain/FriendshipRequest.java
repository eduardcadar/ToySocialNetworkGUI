package com.toysocialnetworkgui.domain;

import java.util.Objects;

public class FriendshipRequest {

    private String email1;
    private String email2;
    private REQUESTSTATE state;

    public  FriendshipRequest(String email1, String email2){
        this.email1 = email1;
        this.email2 = email2;
        this.state = REQUESTSTATE.PENDING;
    }
    public  FriendshipRequest(User user1, User user2){
        this.email1 = user1.getEmail();
        this.email2 = user2.getEmail();
        this.state = REQUESTSTATE.PENDING;
    }
    public  FriendshipRequest(String email1, String email2, REQUESTSTATE state){
        this.email1 = email1;
        this.email2 = email2;
        this.state = state;
    }
    public  FriendshipRequest(User user1, User user2, REQUESTSTATE state){
        this.email1 = user1.getEmail();
        this.email2 = user2.getEmail();
        this.state = state;
    }

    /**
     * @return the email of the first user of the friend request
     */
    public String getFirst() { return email1; }

    /**
     * @return the email of the second user of the friend request
     */
    public String getSecond() { return email2; }

    /**
     *
     * @return - the state of the friend request
     */
    public REQUESTSTATE getState(){
        return state;
    }

    /**
     * Sets the state of the request
     * @param state
     */
    public void setState(REQUESTSTATE state){
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipRequest that = (FriendshipRequest) o;
        return email1.equals(that.email1) && email2.equals(that.email2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email1, email2);
    }
}
