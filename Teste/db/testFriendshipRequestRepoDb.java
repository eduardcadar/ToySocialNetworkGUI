package db;

import com.toysocialnetworkgui.domain.FriendshipRequest;
import com.toysocialnetworkgui.domain.REQUESTSTATE;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.FriendshipRequestDbRepo;
import com.toysocialnetworkgui.repository.db.UserDbRepo;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class testFriendshipRequestRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final User us1 = new User("adi", "popa", "one@gmail.com");
    private final User us2 = new User("alex", "popescu", "two@gmail.com");
    private final User us3 = new User("maria", "lazar", "three@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "for@gmail.com");
    private final FriendshipRequestDbRepo friendshipRequestDbRepo = new FriendshipRequestDbRepo(url, username, password, "requests");
    private final FriendshipRequest f1 = new FriendshipRequest(us1, us2);
    private final FriendshipRequest f2 = new FriendshipRequest(us1, us3);
    private final FriendshipRequest f3 = new FriendshipRequest(us2, us4);

    @BeforeEach
    public void setUp() throws Exception {
        uRepo.save(us1);
        uRepo.save(us2);
        uRepo.save(us3);
        uRepo.save(us4);
        friendshipRequestDbRepo.addRequest(f1);
        friendshipRequestDbRepo.addRequest(f2);
        friendshipRequestDbRepo.addRequest(f3);

    }

    @AfterEach
    public void tearDown() throws Exception {
        friendshipRequestDbRepo.clear();
        uRepo.clear();
    }

    @Test
    public void TestConstructorDb() {
        assertEquals(3, friendshipRequestDbRepo.size());
        List<FriendshipRequest> requests = friendshipRequestDbRepo.getAll();
        assertTrue(requests.contains(f1));
        assertTrue(requests.contains(f2));
        assertTrue(requests.contains(f3));
    }

    @Test
    public void testRejectRequest() {
        f1.setState(REQUESTSTATE.REJECTED);
        friendshipRequestDbRepo.update(f1);
        assertEquals(REQUESTSTATE.REJECTED, friendshipRequestDbRepo.getRequest(f1.getFirst(), f1.getSecond()).getState());
    }

    @Test
    public void testAddRequestsDb() {
        friendshipRequestDbRepo.addRequest(new FriendshipRequest(us1, us4));
        assertEquals(4, friendshipRequestDbRepo.size());
//        Assert.assertNotNull(friendshipRequestDbRepo.getRequest(us1.getEmail(), us4.getEmail()));
        try {
            friendshipRequestDbRepo.addRequest(new FriendshipRequest(us1, us4));
            fail();
        } catch (RepoException e) {
            assertTrue(true);
        }
    }

    @Test
    public void RemoveRequestDb(){
        friendshipRequestDbRepo.removeRequest(new FriendshipRequest(us1, us2));
        assertNull(friendshipRequestDbRepo.getRequest(us1.getEmail(), us2.getEmail()));
        assertEquals(2, friendshipRequestDbRepo.size());
        try{
            friendshipRequestDbRepo.removeRequest(new FriendshipRequest(us1, us2));
            fail();
        }
        catch (RepoException e){
            // Friend request does not exits
        }
    }

    @Test
    public void testClearFriendshipsDb() {
        friendshipRequestDbRepo.clear();
        assertTrue(friendshipRequestDbRepo.isEmpty());
    }


}
