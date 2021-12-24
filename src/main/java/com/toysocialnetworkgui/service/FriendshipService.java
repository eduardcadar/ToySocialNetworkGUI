package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.FriendshipRequest;
import com.toysocialnetworkgui.domain.REQUESTSTATE;
import com.toysocialnetworkgui.repository.FriendshipRepository;
import com.toysocialnetworkgui.repository.FriendshipRequestRepository;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.FriendshipDbRepo;
import com.toysocialnetworkgui.repository.db.FriendshipRequestDbRepo;

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
        if (requestRepository.getRequest(email1, email2) != null)
            removeRequest(email1,email2);
        if (requestRepository.getRequest(email2, email1) != null)
            removeRequest(email2, email1);
        friendshipRepository.removeFriendship(new Friendship(email1, email2));
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
                throw new RepoException("Friend request already APPROVED");
            }
            if (request.getState() == REQUESTSTATE.PENDING) {
                request.setState(REQUESTSTATE.APPROVED);
                requestRepository.update(request);
                friendshipRepository.addFriendship(new Friendship(email1, email2, LocalDate.now()));
            }
        }
    }

    public void rejectFriendship(String email1, String email2){
        FriendshipRequest request = requestRepository.getRequest(email1, email2);
        if( request == null){
            throw new RepoException("There is no pending request between theses 2 users");
        }
        else {
            if (request.getState() == REQUESTSTATE.REJECTED) {
                throw new RepoException("Friend request already rejected");
                // i will never reach this
                // beacuse if the other user rejected
                // it means that i have already a row in db
                // with email1,email2 and get
            }
            if (request.getState() == REQUESTSTATE.APPROVED) {
                throw new RepoException("Friend request already APPROVED");
            }
            if (request.getState() == REQUESTSTATE.PENDING) {
                request.setState(REQUESTSTATE.REJECTED);
                requestRepository.update(request);
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

    public List<String> getUserFriendsPage(String email, int firstrow, int rowcount) {
        return friendshipRepository.getUserFriendsPage(email, firstrow, rowcount);
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

}
