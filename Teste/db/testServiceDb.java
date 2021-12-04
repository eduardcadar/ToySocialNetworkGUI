package db;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.domain.network.Network;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.*;
import com.toysocialnetworkgui.service.*;
import com.toysocialnetworkgui.utils.UserFriendDTO;
import com.toysocialnetworkgui.validator.FriendshipValidator;
import com.toysocialnetworkgui.validator.MessageReceiverValidator;
import com.toysocialnetworkgui.validator.MessageValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class testServiceDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final UserService uSrv = new UserService(uRepo);
    private final User us1 = new User("adi", "popa", "adi.popa@yahoo.com");
    private final User us2 = new User("alex", "popescu", "popescu.alex@gmail.com");
    private final User us3 = new User("maria", "lazar", "l.maria@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "a.gabi@gmail.com");
    private final FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator(), "friendships");
    private final FriendshipRequestDbRepo requestsRepo = new FriendshipRequestDbRepo(url, username, password, "requests");

    private final FriendshipService fSrv = new FriendshipService(fRepo,requestsRepo);
    private final MessageDbRepo mRepo = new MessageDbRepo(url, username, password, new MessageValidator(), "messages");
    private final MessageService mSrv = new MessageService(mRepo);
    private final MessageReceiverDbRepo mrRepo = new MessageReceiverDbRepo(url, username, password, new MessageReceiverValidator(), "receivers");
    private final MessageReceiverService mrSrv = new MessageReceiverService(mrRepo);
    private final Friendship f1 = new Friendship(us2, us1);
    private final Friendship f2 = new Friendship(us3, us1);
    private final Friendship f3 = new Friendship(us2, us4);
    private final Network ntw = new Network(uRepo, fRepo);
    private final Service service = new Service(uSrv, fSrv, mSrv, mrSrv, ntw);

    @BeforeEach
    public void setUp() throws Exception {
        service.addUser(us1.getFirstName(), us1.getLastName(), us1.getEmail(), us1.getPassword());
        service.addUser(us2.getFirstName(), us2.getLastName(), us2.getEmail(), us2.getPassword());
        service.addUser(us3.getFirstName(), us3.getLastName(), us3.getEmail(), us3.getPassword());
        service.addUser(us4.getFirstName(), us4.getLastName(), us4.getEmail(), us4.getPassword());
    }

    @AfterEach
    public void tearDown() throws Exception {
        mrRepo.clear();
        mRepo.clear();
        fRepo.clear();
        uRepo.clear();
    }

    @Test
    public void testUsersSv() {
        assertEquals(4, service.usersSize());
        service.removeUser(us1.getEmail());
        assertEquals(3, service.usersSize());
        List<User> users = service.getUsers();
        assertTrue(users.contains(us3));
        assertTrue(users.contains(us2));
        assertTrue(users.contains(us4));
        assertFalse(service.usersIsEmpty());
        service.updateUser("andrei", "popescu", "popescu.alex@gmail.com", "parolaa");
        User us = service.getUser("popescu.alex@gmail.com");
        assertEquals(us.getFirstName(), "andrei");
        assertEquals(us.getLastName(), "popescu");
    }

    @Test
    public void testGetUserFriends() {
        assertTrue(service.friendshipsIsEmpty());
        service.addFriendship(f1.getFirst(), f1.getSecond());
        service.addFriendship(f2.getFirst(), f2.getSecond());
        service.addFriendship(f3.getFirst(), f3.getSecond());
        service.acceptFriendship(f1.getFirst(), f1.getSecond());
        service.acceptFriendship(f2.getFirst(), f2.getSecond());
        service.acceptFriendship(f3.getFirst(), f3.getSecond());

        List<UserFriendDTO> friendsDTOs = service.getFriendshipsDTO(us1.getEmail());
        assertEquals(2, friendsDTOs.size());

        List<User> friends = service.getUserFriends(us1.getEmail());
        assertEquals(2, friends.size());
        assertTrue(friends.contains(us2));
        assertTrue(friends.contains(us3));
        friends = service.getUserFriends(us2.getEmail());
        assertEquals(2, friends.size());
        assertTrue(friends.contains(us1));
        assertTrue(friends.contains(us4));
        friends = service.getUserFriends(us3.getEmail());
        assertEquals(1, friends.size());
        assertTrue(friends.contains(us1));
        friends = service.getUserFriends(us4.getEmail());
        assertEquals(1, friends.size());
        assertTrue(friends.contains(us2));
        List<User> notFriends = service.getNotFriends(us1.getEmail());
        assertEquals(1, notFriends.size());
        assertTrue(notFriends.contains(us4));
        notFriends = service.getNotFriends(us3.getEmail());
        assertEquals(2, notFriends.size());
        assertTrue(notFriends.contains(us2));
        assertTrue(notFriends.contains(us4));
    }

    @Test
    public void testFriendshipsSv() {
        assertEquals(0, service.friendshipsSize());

        service.addFriendship(f1.getFirst(), f1.getSecond());
        service.acceptFriendship(f1.getFirst(), f1.getSecond());

        service.addFriendship(f2.getFirst(), f2.getSecond());
        service.acceptFriendship(f2.getFirst(), f2.getSecond());

        assertEquals(2, service.friendshipsSize());
        Friendship f = service.getFriendship(us1.getEmail(), us2.getEmail());
        assertNotNull(f);
        f = service.getFriendship(us1.getEmail(), us4.getEmail());
        assertNull(f);
        List<Friendship> friendships = service.getFriendships();
        assertTrue(friendships.contains(f1));
        assertTrue(friendships.contains(f2));
        assertFalse(friendships.contains(f3));

        service.removeFriendship(f1.getFirst(), f1.getSecond());
        assertEquals(1, service.friendshipsSize());
    }

    @Test
    public void testFriendRequest() throws Exception {
        service.addFriendship(us1.getEmail(), us2.getEmail());
        assertEquals(1, service.getUserFriendRequests(us2.getEmail()).size());

        service.rejectFriendship(us1.getEmail(), us2.getEmail());
        try{
            service.addFriendship(us1.getEmail(), us2.getEmail());
            fail();
        }
        catch (RepoException e){
            assertEquals("There is already a request sent by user", e.getMessage());
        }

        service.addFriendship(us2.getEmail(), us1.getEmail());
        service.acceptFriendship(us2.getEmail(), us1.getEmail());
        assertEquals(service.getUserFriends(us2.getEmail()).get(0).getEmail(), us1.getEmail());
    }

    @Test
    public void testNetwork() {
        service.addFriendship(f1.getFirst(), f1.getSecond());
        service.acceptFriendship(f1.getFirst(),f1.getSecond() );
        service.addFriendship(f2.getFirst(), f2.getSecond());
        service.acceptFriendship(f2.getFirst(), f2.getSecond());
        service.addFriendship(f3.getFirst(), f3.getSecond());
        service.acceptFriendship(f3.getFirst(), f3.getSecond());
        assertEquals(1, service.getCommunities().size());
        assertEquals(1, service.nrCommunities());
        assertEquals(4, service.getUsersMostFrCom().size());
    }

    @Test
    public void testMessages() {
        service.addFriendship(us1.getEmail(), us2.getEmail());
        service.acceptFriendship(us1.getEmail(), us2.getEmail());


        Message m1 = new Message(us1.getEmail(),"mesaj1");
        Message m2 = new Message(us2.getEmail(),"mesaj2");
        m1 = service.save(m1.getSender(), List.of(us2.getEmail(), us3.getEmail()), m1.getMessage());
        m2 = service.save(m2.getSender(), List.of(us3.getEmail()), m2.getMessage());
        Message r1 = new Message(us2.getEmail(), "reply", m1.getID());
        r1 = service.save(r1.getSender(), List.of(us1.getEmail()), r1.getMessage(), r1.getIdMsgRepliedTo());
        assertEquals(m1.getID(), service.getMessage(r1.getID()).getIdMsgRepliedTo());
        List<Message> conv = service.getConversation(us1.getEmail(), us2.getEmail());
        assertEquals(2, conv.size());
        assertEquals(conv.get(0).getSender(), m1.getSender());
        assertEquals(conv.get(1).getSender(), r1.getSender());
        conv = service.getConversation(us2.getEmail(), us3.getEmail());
        assertEquals(0, conv.size());
        service.removeUser(us1.getEmail());
        conv = service.getConversation(us1.getEmail(), us2.getEmail());
        assertEquals(0, conv.size());
    }
}