package com.toysocialnetworkgui.repository;

import com.toysocialnetworkgui.domain.Friendship;

import java.util.List;

public interface FriendshipRepository {

    void addFriendship(Friendship f);
    void removeFriendship(Friendship f);
    int size();
    void clear();
    boolean isEmpty();
    List<Friendship> getAll();

    List<String> getUserFriends(String email);

    void removeUserFships(String email);

    Friendship getFriendship(String email1, String email2);

}
