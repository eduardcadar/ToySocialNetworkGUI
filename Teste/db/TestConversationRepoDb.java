package db;

import com.toysocialnetworkgui.domain.Conversation;
import com.toysocialnetworkgui.domain.ConversationParticipant;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.ConversationDbRepo;
import com.toysocialnetworkgui.repository.db.ConversationParticipantDbRepo;
import com.toysocialnetworkgui.repository.db.UserDbRepo;
import com.toysocialnetworkgui.validator.ConversationParticipantValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TestConversationRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";

    UserDbRepo userRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    ConversationDbRepo convRepo = new ConversationDbRepo(url, username, password, "conversations");
    ConversationParticipantDbRepo convPartRepo = new ConversationParticipantDbRepo(url, username, password, new ConversationParticipantValidator(), "participants");

    @BeforeEach
    void setUp() {
        userRepo.save(new User("adi", "popa", "a@a.a"));
        userRepo.save(new User("alex", "popescu", "b@b.b"));
        userRepo.save(new User("ana", "cozma", "c@c.c"));
        userRepo.save(new User("miruna", "lazar", "d@d.d"));
    }

    @AfterEach
    void tearDown() {
        convPartRepo.clear();
        convRepo.clear();
        userRepo.clear();
    }

    @Test
    void testAddConversation() {
        Conversation c1 = convRepo.save(new Conversation());
        c1.setParticipants(List.of("a@a.a", "b@b.b", "c@c.c"));
        c1.getParticipants().forEach(p -> convPartRepo.save(new ConversationParticipant(c1.getID(), p)));
        Conversation c2 = convRepo.save(new Conversation());
        c2.setParticipants(List.of("d@d.d", "a@a.a", "b@b.b", "c@c.c"));
        c2.getParticipants().forEach(p -> convPartRepo.save(new ConversationParticipant(c2.getID(), p)));
        Conversation c = convRepo.getConversation(c1.getID());
        c.setParticipants(convPartRepo.getConversationParticipants(c1.getID()));
        c1.getParticipants().forEach(p -> assertTrue(c.getParticipants().contains(p)));
        c.setParticipants(convPartRepo.getConversationParticipants(c2.getID()));
        c2.getParticipants().forEach(p -> assertTrue(c.getParticipants().contains(p)));
    }
}
