package com.toysocialnetworkgui.repository;

import com.toysocialnetworkgui.domain.FriendshipRequest;

import java.util.List;

public interface FriendshipRequestRepository {
    public void addRequest(FriendshipRequest request);

    public void clear();

    public int size();

    public List<FriendshipRequest> getAll();

    public FriendshipRequest getRequest(String email1, String email2);

    public void removeRequest(FriendshipRequest friendshipRequest);

    public boolean isEmpty();

    void update(FriendshipRequest request);

    public List<String> getUserFriendRequests(String email);
}

