package com.toysocialnetworkgui.repository;

import com.toysocialnetworkgui.domain.FriendshipRequest;

import java.util.List;

public interface FriendshipRequestRepository {
    void addRequest(FriendshipRequest request);

    void clear();

    int size();

    List<FriendshipRequest> getAll();

    FriendshipRequest getRequest(String email1, String email2);

    void removeRequest(FriendshipRequest friendshipRequest);

    boolean isEmpty();

    void update(FriendshipRequest request);

    List<String> getPendingFriendRequestsReceived(String email);
}

