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
import com.toysocialnetworkgui.service.EventService;
import com.toysocialnetworkgui.validator.ConversationParticipantValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
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
    void setUp() throws SQLException {
        userRepo.save(new User("adi", "popa", "a@a.a"));
        userRepo.save(new User("alex", "popescu", "b@b.b"));
        userRepo.save(new User("ana", "cozma", "c@c.c"));
        userRepo.save(new User("miruna", "lazar", "d@d.d"));


        eventRepo.save(new Event(1, "Ziua izabelei", "Izabela", "Acasa", "Zi de nastere ","cel mai mare PArty", LocalDate.of(2021, 8, 15), LocalDate.of(2021, 8, 15), "images/error.png"));
        eventRepo.save(new Event(2, "Antold", "BOC ","CLuj napoca", "Festival","MD cu apa", LocalDate.of(2021, 8, 15), LocalDate.of(2021, 8, 25), "images/error.png"));


    }

    @Test
    void testSize(){
        assertEquals(2, eventRepo.size());
        eventRepo.clear();
        assertEquals(0, eventRepo.size());
    }
    @Test
    void testSave() throws SQLException {
        eventRepo.save(new Event(3,"ev1", "tester","loc1", "meetup", "desc1", startDate, endDate, "images/error.png"));
        assertEquals(3, eventRepo.size());
        try{
            eventRepo.save(new Event(3,"ev1", "tester","loc1", "meetup", "desc1", startDate, endDate, "images/error.png"));
            fail();
        }
        catch (RepoException e){
            assertTrue(true);
        }
    }

    @Test
    void testGetEvent() throws SQLException {
        assertNull(eventRepo.getEvent("basl"));
        assertEquals(eventRepo.getEvent("Antold").getName(), "Antold");
    }

    @Test
    void testRemove() throws SQLException {
        eventRepo.remove("Antold");
        assertEquals(eventRepo.getAll().size(),1 );

    }

    @Test
    void testUpdate() throws SQLException {
//        eventRepo.update(new Event(2, "Untold", "X","CLuj napoca", "Festival","MD cu apa", LocalDate.of(2021, 8, 15), LocalDate.of(2021, 8, 25)));
//        assertEquals(eventRepo.getEvent("Untold").getName(), "Untold");
//        assertEquals(eventRepo.getEvent("Untold").getOrganizer(), "X");
    }

    @Test
    void testGetAll() throws SQLException {
        List<Event> events = eventRepo.getAll();
        assertEquals(events.size(), 2);
        assertEquals(events.get(0).getName(), "Ziua izabelei");
        assertEquals(events.get(1).getName(), "Antold");

    }

    @AfterEach
    void tearDown() {
        eventRepo.clear();
        userRepo.clear();
    }


}
