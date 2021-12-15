package db;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.FriendshipDbRepo;
import com.toysocialnetworkgui.repository.db.UserDbRepo;
import com.toysocialnetworkgui.validator.FriendshipValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFriendshipRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final User us1 = new User("adi", "popa", "adi.popa@yahoo.com");
    private final User us2 = new User("alex", "popescu", "popescu.alex@gmail.com");
    private final User us3 = new User("maria", "lazar", "l.maria@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "a.gabi@gmail.com");
    private final FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator(), "friendships");
    private final Friendship f1 = new Friendship(us1, us2, LocalDate.now());
    private final Friendship f2 = new Friendship(us1, us3, LocalDate.now());
    private final Friendship f3 = new Friendship(us2, us4, LocalDate.now());

    @BeforeEach
    void setUp() {
        uRepo.save(us1);
        uRepo.save(us2);
        uRepo.save(us3);
        uRepo.save(us4);
        fRepo.addFriendship(f1);
        fRepo.addFriendship(f2);
        fRepo.addFriendship(f3);

    }

    @AfterEach
    public void tearDown() throws Exception {
        fRepo.clear();
        uRepo.clear();
    }

    @Test
    public void TestConstructorDb() {
        assertEquals(3, fRepo.size());
        List<Friendship> fships = fRepo.getAll();
        assertTrue(fships.contains(f1));
        assertTrue(fships.contains(f2));
        assertTrue(fships.contains(f3));
    }

    @Test
    public void testAddRemoveFshipDb() {
        fRepo.addFriendship(new Friendship(us1, us4));
        assertEquals(4, fRepo.size());
        assertNotNull(fRepo.getFriendship(us1.getEmail(), us4.getEmail()));
        try {
            fRepo.addFriendship(new Friendship(us1, us4));
            fail();
        } catch (RepoException e) {
            assertTrue(true);
        }
        fRepo.removeFriendship(new Friendship(us1, us4));
        assertNull(fRepo.getFriendship(us1.getEmail(), us4.getEmail()));
        assertEquals(3, fRepo.size());
    }

    @Test
    public void testClearFriendshipsDb() {
        fRepo.clear();
        assertTrue(fRepo.isEmpty());
    }

    @Test
    public void testGetUserFriendsDb() {
        List<String> friends = fRepo.getUserFriends(us1.getEmail());
        assertEquals(2,friends.size());
        assertTrue(friends.contains(us2.getEmail()));
        assertTrue(friends.contains(us3.getEmail()));
    }

    @Test
    public void testRemoveUserFriendshipsDb() {
        fRepo.removeUserFships(us1.getEmail());
        assertEquals(1, fRepo.size());
        fRepo.removeUserFships(us1.getEmail());
        assertEquals(1, fRepo.size());
        fRepo.addFriendship(f1);
        fRepo.addFriendship(f2);
    }
}
