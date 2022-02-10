package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.FriendshipRequest;
import com.toysocialnetworkgui.domain.REQUESTSTATE;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.FriendshipDbRepo;
import com.toysocialnetworkgui.repository.db.FriendshipRequestDbRepo;
import javafx.application.Platform;

import java.time.LocalDate;
import java.util.List;

public class FriendshipService {
    private final FriendshipDbRepo friendshipRepository;
    private final FriendshipRequestDbRepo requestRepository;
    public FriendshipService(FriendshipDbRepo friendshipRepository, FriendshipRequestDbRepo requestRepository) {
        this.friendshipRepository = friendshipRepository;
        this.requestRepository = requestRepository;
    }

    public FriendshipDbRepo getFriendshipRepository() { return friendshipRepository; }
    public FriendshipRequestDbRepo getRequestRepository() { return requestRepository; }

    /**
     * @param email1 - String the email of the first user
     * @param email2 - String the email of the second user
     * @return the friendship of the two users if it is saved in the repository,
     * null otherwise
     */
    public Friendship getFriendship(String email1, String email2) {
        return friendshipRepository.getFriendship(email1, email2);
    }

    /**
     * Adds a friendship request to the repository
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     */
    public void addFriendshipRequest(String email1, String email2) {
        requestRepository.addRequest(new FriendshipRequest(email1, email2, LocalDate.now()));
    }

    /**
     * Removes the request sent by email1 to email2
     * @param email1
     * @param email2
     */
    public void removeRequest(String email1, String email2){
        requestRepository.removeRequest(new FriendshipRequest(email1, email2));
    }

    /**
     * Removes a friendship and the request from the repositories
     * @param email1 - String - the email of a user
     * @param email2 - String - the email of the other user
     */
    public void removeFriendship(String email1, String email2) {
        new Thread(() -> {
            if (requestRepository.getRequest(email1, email2) != null)
                removeRequest(email1,email2);
        }).start();
        new Thread(() -> {
            if (requestRepository.getRequest(email2, email1) != null)
                removeRequest(email2, email1);
        }).start();
        friendshipRepository.removeFriendship(new Friendship(email1, email2));
    }

    /**
     * @param email the email of the user
     * @return number of requests sent by the user
     */
    public Integer getUserSentRequestsSize(String email) {
        return requestRepository.getUserSentRequestsSize(email);
    }

    /**
     * @param email the email of the user
     * @return number of requests received by the user
     */
    public Integer getUserReceivedRequestsSize(String email) {
        return requestRepository.getUserReceivedRequestsSize(email);
    }

    public Integer getUserFriendsSize(String email) {
        return friendshipRepository.getUserFriendsSize(email);
    }

    /**
     * @return all the friendships saved in the repository
     */
    public List<Friendship> getFriendships() {
        return friendshipRepository.getAll();
    }

    /**
     * @return the number of friendships saved in the repository
     */
    public int size() {
        return friendshipRepository.size();
    }

    /**
     * @return true if there are no friendships saved, false otherwise
     */
    public boolean isEmpty() {
        return friendshipRepository.isEmpty();
    }

    /**
     * Removes the friendships of a user
     * @param email - String the email of the user
     */
    public void removeUserFships(String email) {
        friendshipRepository.removeUserFships(email);
    }

    /**
     * Accepts a friend request between user1 with email1 and user2 with email2
     * @param email1 - String
     * @param email2 - String
     * @throws Exception - if there is no pending request in friendship
     */
    public void acceptFriendship(String email1, String email2) {
        FriendshipRequest request = requestRepository.getRequest(email1, email2);
        if (request == null) {
            throw new RepoException("There is no pending request between theses 2 users");
        } else {
            if (request.getState() == REQUESTSTATE.REJECTED) {
                throw new RepoException("Friend request already rejected");
            }
            if (request.getState() == REQUESTSTATE.APPROVED) {
                throw new RepoException("Friend request already approved");
            }
            if (request.getState() == REQUESTSTATE.PENDING) {
                request.setState(REQUESTSTATE.APPROVED);
                new Thread(() -> requestRepository.update(request)).start();
                new Thread(() -> friendshipRepository.addFriendship(new Friendship(email1, email2, LocalDate.now()))).start();
            }
        }
    }

    /**
     * Rejects a friend request between user1 with email1 and user2 with email2
     * @param email1 - String
     * @param email2 - String
     * @throws Exception - if there is no pending request in friendship
     */
    public void rejectFriendship(String email1, String email2) {
        FriendshipRequest request = requestRepository.getRequest(email1, email2);
        if (request == null) {
            throw new RepoException("There is no pending request between theses 2 users");
        } else {
            if (request.getState() == REQUESTSTATE.REJECTED) {
                throw new RepoException("Friend request already rejected");
                // i will never reach this
                // because if the other user rejected
                // it means that i have already a row in db
                // with email1,email2 and get
            }
            if (request.getState() == REQUESTSTATE.APPROVED) {
                throw new RepoException("Friend request already approved");
            }
            if (request.getState() == REQUESTSTATE.PENDING) {
                request.setState(REQUESTSTATE.REJECTED);
                new Thread(() -> requestRepository.update(request)).start();
            }
        }
    }

    /**
     * @param email - String the email of the user
     * @return list with the emails of a user's friends
     */
    public List<String> getUserFriends(String email) {
        return friendshipRepository.getUserFriends(email);
    }

    /**
     * Returns a list with the emails of the user's friends that contain 'pattern' in their names,
     * skipping the first 'firstrow' ones and returning the next 'rowcount' ones
     * @param email email of the user
     * @param firstrow how many results to ignore
     * @param rowcount how many results to return
     * @param pattern what should the name of a user contain
     * @return list with emails of the friends requested
     */
    public List<String> getUserFriendsFilteredPage(String email, int firstrow, int rowcount, String pattern) {
        return friendshipRepository.getUserFriendsFilteredPage(email, firstrow, rowcount, pattern);
    }

    /**
     * Returns a list with the emails of the user's friends that contain 'pattern' in their names,
     * have become friends in the month specified,
     * skipping the first 'firstrow' ones and returning the next 'rowcount' ones
     * @param email email of the user
     * @param firstrow how many results to ignore
     * @param rowcount how many results to return
     * @param pattern what should the name of a user contain
     * @param month the month in which the user became friends with the other users
     * @return list with emails of the friends requested
     */
    public List<String> getUserFriendsMonthFilteredPage(String email, int firstrow, int rowcount, String pattern, int month) {
        return friendshipRepository.getUserFriendsMonthFilteredPage(email, firstrow, rowcount, pattern, month);
    }

    /**
     * Returns a list of emails with the request status is pending for user with email
     * @param email
     * @return
     */
    public List<String> getUserFriendRequests(String email) {
        return requestRepository.getPendingFriendRequestsReceived(email);
    }

    /**
     * @return all the Requests saved in the repository
     */
    public List<FriendshipRequest> getAllFriendshipRequests() {
        return requestRepository.getAll();
    }

    /**
     * @param email the email of the user
     * @param pattern string that friends of the user should have in their names
     * @return number of friends that contain the pattern in their names
     */
    public int getUserFriendsFilteredSize(String email, String pattern) {
        return friendshipRepository.getUserFriendsFilteredSize(email, pattern);
    }

    /**
     * @param email the email of the user
     * @param pattern string that friends should have in their names
     * @param month friendships' month
     * @return number of friends the user made in the specified month, that have the pattern in their names
     */
    public int getUserFriendsMonthFilteredSize(String email, String pattern, int month) {
        return friendshipRepository.getUserFriendsMonthFilteredSize(email, pattern, month);
    }
}
