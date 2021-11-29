package com.toysocialnetworkgui.repository;

import com.toysocialnetworkgui.domain.Friendship;

import java.util.List;

public interface FriendshipRepository {

    public void addFriendship(Friendship f);
    public void removeFriendship(Friendship f);
    public int size();
    public void clear();
    public boolean isEmpty();
    public List<Friendship> getAll();

    public List<String> getUserFriends(String email);

    List<String> getUserFriendsAll(String email);

    public void removeUserFships(String email);

    public Friendship getFriendship(String email1, String email2);

}
