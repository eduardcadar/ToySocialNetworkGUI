import com.toysocialnetworkgui.domain.Conversation;
import com.toysocialnetworkgui.domain.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TestConversation {
    private final Conversation c1 = new Conversation(1, List.of("eu", "tu"));
    private final Conversation c2 = new Conversation(2, List.of("el", "eu")) {{
        setMessages(List.of(
                new Message(2,"eu", "mesaaaaj"),
                new Message(2, "el", "tot mesaj")));
    }};

    @Test
    void testGetters() {
        assertEquals(1, c1.getID());
        assertEquals(2, c2.getID());
    }
}
