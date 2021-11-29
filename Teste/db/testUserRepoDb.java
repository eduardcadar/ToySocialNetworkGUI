package db;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
import com.toysocialnetworkgui.repository.db.UserDbRepo;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class testUserRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final UserDbRepo repo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final User us1 = new User("adi", "popa", "adi.popa@yahoo.com");
    private final User us2 = new User("alex", "popescu", "popescu.alex@gmail.com");
    private final User us3 = new User("maria", "lazar", "l.maria@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "a.gabi@gmail.com");

    @BeforeEach
    public void setUp() throws Exception {
        repo.save(us1);
        repo.save(us2);
        repo.save(us3);
        repo.save(us4);
    }

    @AfterEach
    public void tearDown() throws Exception {
        repo.clear();
    }

    @Test
    public void testConstructorDb() {
        assertEquals(4, repo.size());
        List<User> users = repo.getAll();
        assertTrue(users.contains(us1));
        assertTrue(users.contains(us2));
        assertTrue(users.contains(us3));
        assertTrue(users.contains(us4));
    }

    @Test
    public void testAddRemoveUserDb() {
        repo.save(new User("cosmin","harja","cosmin.h@yahoo.com"));
        assertEquals(5, repo.size());
        assertNotNull(repo.getUser("cosmin.h@yahoo.com"));
        try {
            repo.save(new User("cosmin","harja","cosmin.h@yahoo.com"));
            fail();
        } catch (RepoException e) {
            assertTrue(true);
        }
        assertNull(repo.getUser("email.em@y.c"));
        repo.remove("cosmin.h@yahoo.com");
        assertEquals(4, repo.size());
    }

    @Test
    public void testUpdateUserDb() {
        repo.update(new User("toma","furdui","adi.popa@yahoo.com"));
        User u = repo.getUser("adi.popa@yahoo.com");
        assertEquals("toma", u.getFirstName());
        assertEquals("furdui", u.getLastName());
        repo.update(us1);
    }

    @Test
    public void testClearUsersDb() {
        repo.clear();
        assertTrue(repo.isEmpty());
        repo.save(us1);
        repo.save(us2);
        repo.save(us3);
        repo.save(us4);
    }
}
