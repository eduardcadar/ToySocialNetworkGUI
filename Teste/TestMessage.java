import com.toysocialnetworkgui.domain.Message;
import com.toysocialnetworkgui.domain.MessageReceiver;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class TestMessage {
    private final Message m1 = new Message("eu","mesaj1");
    private final Message m2 = new Message("tu","mesaj2") {{
        setReceivers(Arrays.asList("eu", "el"));
    }};
    private final MessageReceiver mr1 = new MessageReceiver(1, "tu");

    @Test
    public void testGetters() {
        assertEquals("eu", m1.getSender());
        assertEquals("tu", m2.getSender());
        assertEquals("mesaj1", m1.getMessage());
        assertEquals("mesaj2", m2.getMessage());
        assertEquals(mr1.getIdMessage(), 1);
        assertEquals(mr1.getReceiver(), "tu");
    }
}
