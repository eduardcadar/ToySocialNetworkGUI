package db;

import com.toysocialnetworkgui.domain.Conversation;
import com.toysocialnetworkgui.domain.ConversationParticipant;
import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.db.ConversationDbRepo;
import com.toysocialnetworkgui.repository.db.ConversationParticipantDbRepo;
import com.toysocialnetworkgui.repository.db.EventDbRepo;
import com.toysocialnetworkgui.repository.db.UserDbRepo;
import com.toysocialnetworkgui.validator.ConversationParticipantValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TestEventsRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";

    UserDbRepo userRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    EventDbRepo eventRepo = new EventDbRepo(url, username, password, "events");

    @BeforeEach
    void setUp() {
        userRepo.save(new User("adi", "popa", "a@a.a"));
        userRepo.save(new User("alex", "popescu", "b@b.b"));
        userRepo.save(new User("ana", "cozma", "c@c.c"));
        userRepo.save(new User("miruna", "lazar", "d@d.d"));


        eventRepo.save(new Event("Ziua izabelei", "mergem ","Constanta", LocalDateTime.of(2021, 8, 15, 20, 22), LocalDateTime.of(2021, 8, 15, 20, 22)))
    }

    @AfterEach
    void tearDown() {
        convPartRepo.clear();
        eventRepo.clear();
        userRepo.clear();
    }

}
