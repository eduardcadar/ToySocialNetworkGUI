package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.*;
import com.toysocialnetworkgui.domain.network.Network;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.UserRepository;
import com.toysocialnetworkgui.repository.db.*;
import com.toysocialnetworkgui.utils.*;
import com.toysocialnetworkgui.validator.ValidatorException;

import java.time.LocalDate;

import java.util.*;

public class Service {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final Network network;
    private final EventService eventService;

    public Service(UserService userService, FriendshipService friendshipService, MessageService messageService, ConversationService conversationService, Network network, EventService eventService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.conversationService = conversationService;
        this.network = network;
        this.eventService = eventService;
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
    public User updateUser(String firstname, String lastname, String email, String password) {
        return userService.updateUser(firstname, lastname, email, password);
    }
    public User updateUser(String firstname, String lastname, String email, String password, String path) {
        return userService.updateUser(firstname, lastname, email, password, path);
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
        return userService.getAllUsers();
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

    public List<UserFriendDTO> getFriendshipsDTOMonthFilteredPage(String email, int pageNumber, int pageSize, String pattern, int month) {
        List<UserFriendDTO> userFriendDTOS = new ArrayList<>();
        List<String> friendsEmails;
        if (month == 0)
            friendsEmails = friendshipService.getUserFriendsFilteredPage(email, (pageNumber - 1) * pageSize, pageSize, pattern);
        else
            friendsEmails = friendshipService.getUserFriendsMonthFilteredPage(email, (pageNumber - 1) * pageSize, pageSize, pattern, month);
        for (String friendEmail : friendsEmails) {
            Friendship friendship = friendshipService.getFriendship(email, friendEmail);
            User friend;
            if (email.equals(friendship.getFirst()))
                friend = userService.getUser(friendship.getSecond());
            else
                friend = userService.getUser(friendship.getFirst());
            UserFriendDTO userFriendDTO = new UserFriendDTO(friend.getFirstName(), friend.getLastName(), friend.getEmail(), friendship.getDate());
            userFriendDTOS.add(userFriendDTO);
        }
        return userFriendDTOS;
    }

    public int getUserFriendsMonthFilteredSize(String email, String pattern, int month) {
        if (month == 0) return friendshipService.getUserFriendsFilteredSize(email, pattern);
        return friendshipService.getUserFriendsMonthFilteredSize(email, pattern, month);
    }

    public int getUserFriendsFilteredSize(String email, String pattern) {
        return friendshipService.getUserFriendsFilteredSize(email, pattern);
    }

    public List<User> getUserFriendsFilteredPage(String email, int pageNumber, int pageSize, String pattern) {
        List<User> friends = new ArrayList<>();

        List<String> friendsEmails = friendshipService.getUserFriendsFilteredPage(email, (pageNumber - 1) * pageSize, pageSize, pattern);
        friendsEmails.forEach(f -> friends.add(userService.getUser(f)));

        return friends;
    }

    /**
     * @param email - String the email of the user
     * @return the users that are not friends with the given user
     */
    public List<User> getNotFriends(String email) {
        List<String> friends = friendshipService.getUserFriends(email);

        return userService.getAllUsers()
                .stream()
                .filter(u -> !friends.contains(u.getEmail()) && u.getEmail().compareTo(email) != 0)
                .toList();
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

    public List<ConversationDTO> getUserConversationDTOs(String email) {
        List<ConversationDTO> conversationDTOs = new ArrayList<>();

        List<Integer> conversations = conversationService.getUserConversations(email);
        conversations.forEach(c -> {
            List<String> participantsEmails = conversationService.getConversationParticipants(c);
            List<User> participants = new ArrayList<>();
            participantsEmails.forEach(e -> participants.add(userService.getUser(e)));
            conversationDTOs.add(new ConversationDTO(c, participants));
        });

        return conversationDTOs;
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
     * @param idConversation id of the conversation
     * @return number of messages in the conversation
     */
    public int getConversationSize(int idConversation) {
        return messageService.getConversationSize(idConversation);
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

    /**
     * Returns a list with UserMessageDTOs from a conversation's messages
     * @param emails the emails of the participants of the conversation
     * @return list of UserMessageDTO
     */
    public List<UserMessageDTO> getConversationUserMessageDTOs(List<String> emails) {
        List<UserMessageDTO> messageDTOs = new ArrayList<>();
        Map<String, User> usersMap = new HashMap<>();
        emails.forEach(e -> usersMap.put(e, userService.getUser(e)));

        int convID = conversationService.getConversation(emails).getID();
        List<Message> messages = messageService.getConversationMessages(convID);
        messages.forEach(m ->
                messageDTOs.add(new UserMessageDTO(usersMap.get(m.getSender()), m.getMessage(), m.getDate())));

        return messageDTOs;
    }

    /**
     * Returns a list with UserMessageDTOs for all messages received by a user
     * @param email the email of the user
     * @return list of UserMessageDTO
     */
    public List<UserMessageDTO> getUserMessageDTOs(String email) {
        List<UserMessageDTO> messageDTOs = new ArrayList<>();
        List<Integer> conversations = conversationService.getUserConversations(email);
        List<Message> messages = messageService.getAllMessages()
                .stream()
                .filter(m -> conversations.contains(m.getIdConversation()))
                .filter(m -> !m.getSender().equals(email))
                .toList();

        List<User> users = userService.getAllUsers();
        Map<String, User> usersMap = new HashMap<>();
        users.forEach(u -> usersMap.put(u.getEmail(), u));

        messages.forEach(m ->
                messageDTOs.add(new UserMessageDTO(usersMap.get(m.getSender()), m.getMessage(), m.getDate())));

        return messageDTOs;
    }

    public ConversationDbRepo getConversationRepo() { return conversationService.getConvRepo(); }
    public ConversationParticipantDbRepo getConversationParticipantsRepo() { return conversationService.getParticipantsRepo(); }
    public UserRepository getUserRepo() { return userService.getRepo(); }
    public FriendshipDbRepo getFriendshipRepo() { return friendshipService.getFriendshipRepository(); }
    public FriendshipRequestDbRepo getRequestRepo() { return friendshipService.getRequestRepository(); }
    public MessageDbRepo getMessageRepo() { return messageService.getRepo(); }
    public EventDbRepo getEventRepo() { return eventService.getEventRepo(); }
    public EventsSubscriptionDbRepo getEventsSubscriptionRepo() { return eventService.getEventsSubscriptionRepo(); }
    public ConversationService getConversationService() { return conversationService; }

    /**
     * Adds a new event into Social Network
     * @param name - String
     * @param location - String
     * @param description - String
     * @param startDate - LocalDate
     * @param endDate -LocalDate
     */
    public void addEvent(String creator, String name, String category, String  location, String description, LocalDate startDate, LocalDate endDate, String eventPhotoPath){
        eventService.saveEvent(name,creator,location,category, description, startDate, endDate, eventPhotoPath);
    }

    public List<Event> getAllEvents(){
        return eventService.getAllEvents();
    }

    /**
     * Removes the event
     * @param name - String
     * @param location - String
     * @param description - String
     * @param startDate - LocalDate
     * @param endDate - LocalDate
     */
    public void removeEvent(String name, String location, String description, LocalDate startDate, LocalDate endDate) {
        eventService.removeEvent(name,location,description,startDate,endDate);
    }

    public void updateEvent(String name, String location, String description, LocalDate startDate, LocalDate endDate) {
        eventService.updateEvent(name,location,description,startDate,endDate);
    }

    public void subscribeUserToEvent(Integer eventId, String userEmail){
        // if user does not exist ->
        // if event does not exits ->
        // add Subscription
       Event ev =  eventService.getEvent(eventId);
       User u = userService.getUser(userEmail);
       if(ev != null && u != null)
            eventService.subscribeUserToEvent(eventId, userEmail);
    }

    public void unsubscribeUserFromEvent(Integer eventId, String userEmail){
        eventService.unsubscribeUserFromEvent(eventId,userEmail);
    }

    public List<Event> getEventsForUser(String userEmail) {
        return eventService.getEventsForUser(userEmail);
    }

    public List<Event> getUserUpcomingEvents(String email) {
        return eventService.getEventsForUser(email)
                .stream()
                .filter(ev -> ev.getEnd().isAfter(LocalDate.now()))
                .toList();
    }

    /**
     * Returns a list of CommonFriendsDTO objects with users that are not friends with the specified user
     * @param email email of the user
     * @return list of CommonFriendsDTOs
     */
    public List<CommonFriendsDTO> getUserCommonFriendsDTO(String email) {
        List<CommonFriendsDTO> commonFriendsDTOs = new ArrayList<>();

        List<User> notFriends = getNotFriends(email);
        List<User> friends = getUserFriends(email);
        List<String> friendsEmails = new ArrayList<>();
        friends.forEach(f -> friendsEmails.add(f.getEmail()));

        notFriends.forEach(n -> {
            int size;
            size = friendshipService.getUserFriends(n.getEmail())
                    .stream()
                    .filter(friendsEmails::contains)
                    .toList()
                    .size();
            commonFriendsDTOs.add(new CommonFriendsDTO(n, size));
        });

        return commonFriendsDTOs
                .stream()
                .sorted((a, b) -> b.getNrOfCommonFriends().compareTo(a.getNrOfCommonFriends()))
                .toList();
    }

}
