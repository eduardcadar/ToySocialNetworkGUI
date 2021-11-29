package db;

import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.MessageReceiver;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.MessageDbRepo;
import com.toysocialnetworkgui.repository.db.MessageReceiverDbRepo;
import com.toysocialnetworkgui.repository.db.UserDbRepo;
import com.toysocialnetworkgui.validator.MessageReceiverValidator;
import com.toysocialnetworkgui.validator.MessageValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class testMessageRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";
    private final User us1 = new User("adi", "popa", "adi.popa@yahoo.com");
    private final User us2 = new User("alex", "popescu", "popescu.alex@gmail.com");
    private final User us3 = new User("maria", "lazar", "l.maria@gmail.com");
    private final User us4 = new User("gabriel", "andrei", "a.gabi@gmail.com");
    private final UserDbRepo uRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    private final MessageDbRepo mRepo = new MessageDbRepo(url, username, password, new MessageValidator(), "messages");
    private final MessageReceiverDbRepo mrRepo = new MessageReceiverDbRepo(url, username, password, new MessageReceiverValidator(), "receivers");
    private final Message m1 = new Message("adi.popa@yahoo.com","mesaj1");
    private final Message m2 = new Message("popescu.alex@gmail.com","mesaj2");
    private final MessageReceiver mr1 = new MessageReceiver(1, "popescu.alex@gmail.com");

    @BeforeEach
    public void setUp() throws Exception {
        mrRepo.clear();
        mRepo.clear();
        uRepo.clear();
        uRepo.save(us1);
        uRepo.save(us2);
        uRepo.save(us3);
        uRepo.save(us4);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mrRepo.clear();
        mRepo.clear();
        uRepo.clear();
    }

    @Test
    public void testAddMessage() {
        mRepo.save(m1);
        assertEquals(1, mRepo.size());
        mRepo.save(m2);
        assertEquals(2, mRepo.size());
    }
}
