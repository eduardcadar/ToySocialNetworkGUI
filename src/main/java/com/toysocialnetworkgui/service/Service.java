package com.toysocialnetworkgui.service;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.domain.network.Network;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.utils.MessageDTO;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import com.toysocialnetworkgui.validator.ValidatorException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Service {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final MessageReceiverService messageReceiverService;
    private final Network network;

    public Service(UserService userService, FriendshipService friendshipService, MessageService messageService, MessageReceiverService messageReceiverService, Network network) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.messageReceiverService = messageReceiverService;
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
        friendshipService.addFriendship(email1, email2);
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
     *
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


    /**
     * @param email - String
     * @param month - int
     * @return - Stream of USerFriend DTOS
     */
    public Stream<UserFriendDTO> getFriendshsByMonth(String email, int month){
        List<UserFriendDTO> dtos = getFriendshipsDTO(email);
        System.out.println("----FRIENDS----");
        return
            dtos.stream()
                .filter(x -> x.getDate().getMonth().getValue() == month);
   }

    /**
     * @param email - String the email of the user
     * @return the users that are not friends with the given user
     */
    public List<User> getNotFriends(String email) {
        List<String> friends = friendshipService.getUserFriendsAll(email);
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

    public List<MessageDTO> getMessageDTOsReceivedBy(String receiver, String sender) {
        List<MessageDTO> messages = new ArrayList<>();
        List<Integer> messageIds = messageReceiverService.getMessageIdsReceivedBy(receiver);
        messageIds.forEach(x -> {
            Message msg = messageService.getMessage(x);
            String textMsgRepliedTo;
            if (msg.getIdMsgRepliedTo() != null) {
                textMsgRepliedTo = messageService.getMessage(msg.getIdMsgRepliedTo()).getMessage();
                if (textMsgRepliedTo.length() >= 20)
                    textMsgRepliedTo = textMsgRepliedTo.substring(0, 20) + " ...";
            } else
                textMsgRepliedTo = ".";
            MessageDTO msgDTO = new MessageDTO(msg.getSender(), msg.getMessage(), textMsgRepliedTo);
            msgDTO.setID(x);
            msgDTO.setReceivers(messageReceiverService.getMessageReceivers(x));
            msgDTO.setDate(msg.getDate());
            messages.add(msgDTO);
        });
        return messages.stream()
                .filter(x -> x.getSender().compareTo(sender) == 0)
                .sorted(Comparator.comparing(MessageDTO::getDate))
                .toList();
    }

    public List<MessageDTO> getConversationDTOs(String email1, String email2) {
        List<MessageDTO> messagesReceived1 = getMessageDTOsReceivedBy(email1, email2);
        List<MessageDTO> messagesReceived2 = getMessageDTOsReceivedBy(email2, email1);
        List<MessageDTO> conversation = new ArrayList<>();
        conversation.addAll(messagesReceived1);
        conversation.addAll(messagesReceived2);
        return conversation.stream()
                .sorted(Comparator.comparing(MessageDTO::getDate))
                .toList();
    }

    /**
     * Returns a list with the messages received by a user from a specific user
     * @param receiver the email of the receiver
     * @param sender the email of the sender
     * @return list with messages
     */
    public List<Message> getMessagesReceivedBy(String receiver, String sender) {
        List<Message> messages = new ArrayList<>();
        List<Integer> messageIds = messageReceiverService.getMessageIdsReceivedBy(receiver);
        messageIds.forEach(x -> messages.add(messageService.getMessage(x)));
        messages.forEach(x -> {
            List<String> receivers = new ArrayList<>(messageReceiverService.getMessageReceivers(x.getID()));
            x.setReceivers(receivers);
        });
        return messages.stream()
                .filter(x -> x.getSender().compareTo(sender) == 0)
                .sorted(Comparator.comparing(Message::getDate))
                .toList();
    }

    /**
     * Returns a list with all the messages between two users
     * @param email1 email of the first user
     * @param email2 email of the second user
     * @return List of Message
     */
    public List<Message> getConversation(String email1, String email2) {
        List<Message> messagesReceived1 = getMessagesReceivedBy(email1, email2);
        List<Message> messagesReceived2 = getMessagesReceivedBy(email2, email1);
        List<Message> conversation = new ArrayList<>();
        conversation.addAll(messagesReceived1);
        conversation.addAll(messagesReceived2);
        return conversation.stream()
                .sorted(Comparator.comparing(Message::getDate))
                .toList();
    }

    /**
     * Saves a reply message
     * @param sender email of the sender
     * @param receivers list with emails of the receivers
     * @param message text of the message
     * @param idMsgRepliedTo id of the message replied to
     */
    public Message save(String sender, List<String> receivers, String message, int idMsgRepliedTo) {
        Message msg = messageService.save(sender, message, idMsgRepliedTo);
        receivers.forEach(x -> {
            // verific daca cei doi sunt prieteni
            if (friendshipService.getFriendship(sender, x) != null)
                messageReceiverService.save(msg.getID(), x);
        });
        return msg;
    }

    /**
     * Saves a message
     * @param sender email of the sender
     * @param receivers list with emails of the receivers
     * @param message text of the message
     */
    public Message save(String sender, List<String> receivers, String message) {
        Message msg = messageService.save(sender, message);
        receivers.forEach(x -> {
            // verific daca cei doi sunt prieteni
            if (friendshipService.getFriendship(sender, x) != null)
                messageReceiverService.save(msg.getID(), x);
        });
        return msg;
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
     * @param email1
     * @param email2
     */
    public void rejectFriendship(String email1, String email2) {
        friendshipService.rejectFriendship(email1, email2);
    }
}