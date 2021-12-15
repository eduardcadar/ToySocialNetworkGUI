import com.toysocialnetworkgui.domain.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class TestMessage {
    private final Message m1 = new Message(1,"eu","mesaj1");
    private final Message m2 = new Message(1,"tu","mesaj2");

    @Test
    public void testGetters() {
        assertEquals("eu", m1.getSender());
        assertEquals("tu", m2.getSender());
        assertEquals("mesaj1", m1.getMessage());
        assertEquals("mesaj2", m2.getMessage());
    }
}
