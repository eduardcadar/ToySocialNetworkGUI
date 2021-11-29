import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFriendship {
    private final User us1 = new User("Ion", "Pop", "pop.ion@yahoo.com");
    private final User us2 = new User("Adi", "Radu", "radu.adi@gmail.com");
    private final Friendship f1 = new Friendship(us1, us2);
    private final Friendship f2 = new Friendship(us2, us1);

    @Test
    public void testGetters() {
        assertEquals(f1.getFirst(), us1.getEmail());
        assertEquals(f1.getSecond(), us2.getEmail());
    }

    @Test
    public void testToString() {
        assertEquals(0, f1.toString().compareTo(us1.getEmail() + " --- " + us2.getEmail()));
    }

}
