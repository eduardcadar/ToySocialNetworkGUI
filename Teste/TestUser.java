import com.toysocialnetworkgui.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUser {
    private final User us1 = new User("Ion", "Pop", "pop.ion@yahoo.com");
    private final User us2 = new User("Adi", "Radu", "radu.adi@gmail.com");

    @Test
    public void testGetters() {
        assertEquals(0, us1.getFirstName().compareTo("Ion"));
        assertEquals(0, us1.getLastName().compareTo("Pop"));
        assertEquals(0, us1.getEmail().compareTo("pop.ion@yahoo.com"));
    }

    @Test
    public void testUpdate() {
        us1.update("gigel","g","parola");
        assertEquals(us1.getLastName(),"g");
        assertEquals(us1.getFirstName(),"gigel");
        us1.update("Ion","Pop","000000");
    }

    @Test
    public void testToString() {
        assertEquals(0, us1.toString().compareTo("Pop Ion"));
    }
}
