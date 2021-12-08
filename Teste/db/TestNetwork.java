package db;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.domain.network.MostFriendlyCommunity;
import com.toysocialnetworkgui.domain.network.Network;
import com.toysocialnetworkgui.repository.db.FriendshipDbRepo;
import com.toysocialnetworkgui.repository.db.UserDbRepo;
import com.toysocialnetworkgui.validator.FriendshipValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNetwork {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final User us1 = new User("adi", "popa", "adi.popa@yahoo.com");
    private final User us2 = new User("alex", "popescu", "popescu.alex@gmail.com");
    private final User us3 = new User("maria", "lazar", "l.maria@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "a.gabi@gmail.com");
    private final User us5 = new User("gabriel", "andrei", "ab.gabi@gmail.com");
    private final FriendshipDbRepo fRepo = new FriendshipDbRepo(url, username, password, new FriendshipValidator(), "friendships");
    private final Friendship f1 = new Friendship(us1, us2);
    private final Friendship f2 = new Friendship(us2, us3);
    private final Friendship f3 = new Friendship(us4, us5);
    private final Network ntw = new Network(uRepo, fRepo);

    @BeforeEach
    public void setUp() throws Exception {
        uRepo.save(us1);
        uRepo.save(us2);
        uRepo.save(us3);
        uRepo.save(us4);
        uRepo.save(us5);
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
    public void testCommunities() {
        ntw.reload();
        Map<Integer, List<String>> comms = ntw.getCommunities();
        assertEquals(2, comms.size());
    }

    @Test
    public void testMostFriendlyCommunity() {
        ntw.reload();
        MostFriendlyCommunity mfc = ntw.getMfCom();
        assertEquals(3, mfc.getNrUsers());
        List<User> users = ntw.getUsersMostFrCom();
        assertEquals(3, users.size());
    }
}
