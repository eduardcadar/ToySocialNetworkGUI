package com.toysocialnetworkgui.domain;

/*
Class that abstracts the state of a friendship:
PENDING - user send friend request
APPROVED - the other user accepted the friend request
 */
public enum REQUESTSTATE {
    PENDING,
    APPROVED,
    REJECTED
}
