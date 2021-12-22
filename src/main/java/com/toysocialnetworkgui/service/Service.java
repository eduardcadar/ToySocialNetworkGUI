package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.*;
import com.toysocialnetworkgui.domain.network.Network;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import com.toysocialnetworkgui.utils.UserRequestDTO;
import com.toysocialnetworkgui.validator.ValidatorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Service {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final Network network;

    public Service(UserService userService, FriendshipService friendshipService, MessageService messageService, ConversationService conversationService, Network network) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.conversationService = conversationService;
        this.network = network;
    }

    /**
     * @return no of communities - int
     */
    public int nrCommunities() {
        return network.getNrCommunities();
    }

    /**
     * @return the users of the longest path in the friendships network - List[User]
     */
    public List<User> getUsersMostFrCom() {
        return network.getUsersMostFrCom();
    }

    /**
     * Adds a user
     * @param firstname - the first name of the user
     * @param lastname - the last name of the user
     * @param email - the email of the user
     * @param password - the password of the user
     * @throws ValidatorException - if the user is not valid
     * @throws RepoException - if the email is already saved
     */
    public void addUser(String firstname, String lastname, String email, String password) throws ValidatorException, RepoException {
        userService.save(firstname, lastname, email, password);
    }

    /**
     * Removes a user
     * @param email - String the email of the user to be removed
     * @throws RepoException - if there's no user with the given email
     */
    public void removeUser(String email) {
        userService.remove(email);
        friendshipService.removeUserFships(email);
    }

    /**
     * Returns an user
     * @param email - String the email of the user
     * @return the user with the given email
     */
    public User getUser(String email) {
        return userService.getUser(email);
    }

    /**
     * Adds a friendship
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     * @throws RepoException - if the friendship is already saved
     */
    public void addFriendship(String email1, String email2) {
        friendshipService.addFriendshipRequest(email1, email2);
    }

    /**
     * Removes a friendship
     * @param email1 - String - the email of a user
     * @param email2 - String - the email of the other user
     */
    public void removeFriendship(String email1, String email2) {
        friendshipService.removeFriendship(email1, email2);
    }

    /**
     * Returns the friendship of two users
     * @param email1 - the email of the first user
     * @param email2 - the email of the second user
     * @return the friendship of the two users
     */
    public Friendship getFriendship(String email1, String email2) {
        return friendshipService.getFriendship(email1, email2);
    }

    /**
     * Updates a user
     * @param firstname - the new first name of the user
     * @param lastname - the new last name of the user
     * @param email - the email of the user to be updated
     * @param password - the new password of the user
     */
    public void updateUser(String firstname, String lastname, String email, String password) {
        userService.updateUser(firstname, lastname, email, password);
    }

    /**
     * @return dictionary with the users of the communites - Map[Integer, List[String]]
     */
    public Map<Integer, List<String>> getCommunities() {
        return network.getCommunities();
    }

    /**
     * @return saved users - List[User]
     */
    public List<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * @return true if there are no users saved, false otherwise
     */
    public boolean usersIsEmpty() {
        return userService.isEmpty();
    }

    /**
     * @return saved friendships - List[Friendship]
     */
    public List<Friendship> getFriendships() {
        return friendshipService.getFriendships();
    }

    /**
     * @return no of users - int
     */
    public int usersSize() {
        return userService.size();
    }

    /**
     * @return no of friendships - int
     */
    public int friendshipsSize() {
        return friendshipService.size();
    }

    /**
     * @return true if there are no friendships saved, false otherwise
     */
    public boolean friendshipsIsEmpty() {
        return friendshipService.isEmpty();
    }

    /**
     * Accepts the friendship setting its status to approved and setting the date
     * @param email1 - String
     * @param email2 - String
     * @throws RepoException - if something goes bad with the request :(
     */
    public void acceptFriendship(String email1, String email2)  {
        friendshipService.acceptFriendship(email1, email2);
    }

    /**
     * @param email - String the email of the user
     * @return the friends of the user
     */
    public List<User> getUserFriends(String email) {
        List<User> friends = new ArrayList<>();
        List<String> friendsEmails = friendshipService.getUserFriends(email);
        for (String friendEmail : friendsEmails) {
            friends.add(userService.getUser(friendEmail));
        }
        return friends;
    }

    /**
     * Returns a list of DTOs with a user's friends
     * A dto contains the first name and last name of a friend and the date
     * when the two users became friends
     * @param email - String - the email of the user
     * @return List<UserFriendDTO>
     */
    public List<UserFriendDTO> getFriendshipsDTO(String email){
        List<UserFriendDTO> userFriendDTOS  = new ArrayList<>();
        List<String> friendsEmail = friendshipService.getUserFriends(email);
        for(String friendEmail : friendsEmail){
            Friendship friendship = friendshipService.getFriendship(email, friendEmail);
            User friend;
            if(email.equals(friendship.getFirst())) {
                 friend = userService.getUser(friendship.getSecond());
            }
            else{
                 friend = userService.getUser(friendship.getFirst());
            }
            UserFriendDTO userFriendDTO = new UserFriendDTO(friend.getFirstName(), friend.getLastName(), friend.getEmail(), friendship.getDate());
            userFriendDTOS.add(userFriendDTO);
        }
        return userFriendDTOS;
    }

    public List<UserFriendDTO> getFriendshipsDTOPage(String email, int pageNumber, int pageSize){
        List<UserFriendDTO> userFriendDTOS  = new ArrayList<>();
        List<String> friendsEmail = friendshipService.getUserFriendsPage(email, (pageNumber - 1) * pageSize, pageSize);
        for (String friendEmail : friendsEmail){
            Friendship friendship = friendshipService.getFriendship(email, friendEmail);
            User friend;
            if (email.equals(friendship.getFirst())) {
                friend = userService.getUser(friendship.getSecond());
            }
            else {
                friend = userService.getUser(friendship.getFirst());
            }
            UserFriendDTO userFriendDTO = new UserFriendDTO(friend.getFirstName(), friend.getLastName(), friend.getEmail(), friendship.getDate());
            userFriendDTOS.add(userFriendDTO);
        }
        return userFriendDTOS;
    }

    /**
     * @param email - String the email of the user
     * @return the users that are not friends with the given user
     */
    public List<User> getNotFriends(String email) {
        List<String> friends = friendshipService.getUserFriends(email);
        List<User> notFriends = new ArrayList<>();
        for (User u : userService.getUsers())
            if (!friends.contains(u.getEmail()) && u.getEmail().compareTo(email) != 0)
                notFriends.add(u);
        return notFriends;
    }

    /**
     * Returns a list of a pending friend requests for the user with email
     * @param email - string
     * @return - List
     */
    public List<User> getUserFriendRequests(String email) {
        ArrayList<User> users = new ArrayList<>();
        for(String friendEmail: friendshipService.getUserFriendRequests(email)){
            users.add(userService.getUser(friendEmail));
        }
        return users;
    }

    public List<Conversation> getUserConversations(String email) {
        List<Conversation> conversations = new ArrayList<>();
        List<Integer> conversationIds = conversationService.getUserConversations(email);
        conversationIds.forEach(c -> {
            Conversation conv = conversationService.getConversation(c);
            List<Message> messages = messageService.getConversationMessages(c);
            conv.setMessages(messages);
            conversations.add(conv);
        });

        return conversations;
    }

    /**
     * Return a list of UserRequestDTPS where the sender was user with email
     */
    public List<UserRequestDTO> getUserSentRequests(String email) {
        List<FriendshipRequest> friendshipRequests = friendshipService.getAllFriendshipRequests();
        ArrayList<UserRequestDTO> sendRequestsDto = new ArrayList<>();
        for(FriendshipRequest friendshipRequest: friendshipRequests){
            String sender = friendshipRequest.getFirst();
            String receiver = friendshipRequest.getSecond();
            REQUESTSTATE state = friendshipRequest.getState();
            LocalDate sendDate = friendshipRequest.getSendDate();
            if (sender.equals(email)) {
                User userReceiver = getUser(receiver);
                UserRequestDTO dto = new UserRequestDTO(userReceiver.getFirstName(), userReceiver.getLastName(), state, sendDate,userReceiver.getEmail());
                sendRequestsDto.add(dto);
            }
        }
        return sendRequestsDto;

    }

    /**
     * Return a list of UserRequest dtos where the receiver was user with email
     */
    public List<UserRequestDTO> getUserReceivedRequests(String email) {
        List<FriendshipRequest> friendshipRequests = friendshipService.getAllFriendshipRequests();
        ArrayList<UserRequestDTO> sendRequestsDto = new ArrayList<>();
        for(FriendshipRequest friendshipRequest: friendshipRequests){
            String sender = friendshipRequest.getFirst();
            String receiver = friendshipRequest.getSecond();
            REQUESTSTATE state = friendshipRequest.getState();
            LocalDate sendDate = friendshipRequest.getSendDate();
            if (receiver.equals(email)) {
                User userSender = getUser(sender);
                UserRequestDTO dto = new UserRequestDTO(userSender.getFirstName(), userSender.getLastName(), state, sendDate, userSender.getEmail());
                sendRequestsDto.add(dto);
            }
        }
        return sendRequestsDto;

    }

    /**
     * Checks if the owner is friends with every other participant, then creates the conversation
     * @param participants list of participants; the first user in the list is the one who creates the conversation
     * @return the created conversation
     */
    public Conversation getConversation(List<String> participants) {
        String owner = participants.get(0);
        participants.forEach(p -> {
            if (!p.equals(owner))
                if (friendshipService.getFriendship(owner, p) == null)
                    throw new RepoException("Not friends");
        });
        return conversationService.getConversation(participants);
    }

    /**
     * Returns the conversation with the given id
     * @param idConversation id of the conversation
     * @return the conversation
     */
    public Conversation getConversation(int idConversation) {
        Conversation conversation = conversationService.getConversation(idConversation);
        conversation.setMessages(messageService.getConversationMessages(idConversation));
        return conversation;
    }

    public Conversation getConversationPage(int idConversation, int pageNumber, int pageSize) {
        Conversation conversation = conversationService.getConversation(idConversation);
        conversation.setMessages(messageService.getConversationMessagesPage(idConversation, (pageNumber - 1) * pageSize, pageSize));
        return conversation;
    }

    /**
     * Sends a message to a conversation
     * @param idConversation the id of the conversation where the message is sent
     * @param sender the email of the sender
     * @param message the text of the message
     * @return the saved message
     */
    public Message sendMessage(int idConversation, String sender, String message) {
        return messageService.save(idConversation, sender, message);
    }

    /**
     * Returns a message
     * @param id the id of the message
     * @return Message
     */
    public Message getMessage(int id) {
        return messageService.getMessage(id);
    }

    /**
     * Rejects the friendship between email1 si email2
     * @param email1 - String
     * @param email2 - String
     */
    public void rejectFriendship(String email1, String email2) {
        friendshipService.rejectFriendship(email1, email2);
    }

    /**
     * Cancel a pending requests between user with email1 and email2
     * @param email1 email of a user
     * @param email2 email of the other user
     * @throws RepoException - if there is no pending request between them
     */
    public void cancelPendingRequest(String email1, String email2) {
        List<String> senders = friendshipService.getUserFriendRequests(email2);
        boolean hasSent = false;
        for(String sender: senders){
            if(sender.equals(email1)){
                hasSent = true;
                friendshipService.removeRequest(email1, email2);
            }
        }
        if(!hasSent)
            throw new RepoException("There is no pending request available. You can't cancel it!");
    }
}