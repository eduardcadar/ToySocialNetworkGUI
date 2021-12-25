package db;

import com.toysocialnetworkgui.domain.Conversation;
import com.toysocialnetworkgui.domain.ConversationParticipant;
import com.toysocialnetworkgui.domain.Event;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.RepoException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TestEventsRepoDb {
    private final String url = "jdbc:postgresql://localhost:5432/TestToySocialNetwork";
    private final String username = "postgres";
    private final String password = "postgres";

    private LocalDate startDate = LocalDate.of(2021, 5, 5);
    private LocalDate endDate = LocalDate.of(2021, 7, 8);
    UserDbRepo userRepo = new UserDbRepo(url, username, password, new UserValidator(), "users");
    EventDbRepo eventRepo = new EventDbRepo(url, username, password, "events");

    @BeforeEach
    void setUp() {
        userRepo.save(new User("adi", "popa", "a@a.a"));
        userRepo.save(new User("alex", "popescu", "b@b.b"));
        userRepo.save(new User("ana", "cozma", "c@c.c"));
        userRepo.save(new User("miruna", "lazar", "d@d.d"));


        eventRepo.save(new Event("Ziua izabelei", "mergem ","Zi de nastere","Constanta", LocalDate.of(2021, 8, 15), LocalDate.of(2021, 8, 15)));
        eventRepo.save(new Event("Antold", "Clujao ","Festival","MD cu apa", LocalDate.of(2021, 8, 15), LocalDate.of(2021, 8, 25)));

    }

    @Test
    void testSize(){
        assertEquals(2, eventRepo.size());
        eventRepo.clear();
        assertEquals(0, eventRepo.size());
    }
    @Test
    void testSave(){
        eventRepo.save(new Event("ev1", "loc1", "meetup", "desc1", startDate, endDate));
        assertEquals(3, eventRepo.size());
        try{
            eventRepo.save(new Event("ev1", "loc1", "meetup", "desc1", startDate, endDate));
            assertTrue(false);
        }
        catch (RepoException e){
            assertTrue(true);
        }
    }

    @Test
    void testGetEvent(){

    }

    @Test
    void testRemove(){


    }

    @Test
    void testUpdate(){


    }


    @AfterEach
    void tearDown() {
        eventRepo.clear();
        userRepo.clear();
    }

}
