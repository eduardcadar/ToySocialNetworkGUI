import com.toysocialnetworkgui.domain.FriendshipRequest;
import com.toysocialnetworkgui.domain.REQUESTSTATE;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestFriendshipRequest {
    private final FriendshipRequest friendshipRequest = new FriendshipRequest("1", "2");


    @Test
    public void testConstructor() {
        assertEquals(friendshipRequest.getFirst(), "1");
        assertEquals(friendshipRequest.getSecond(), "2");
        assertEquals(friendshipRequest.getState(), REQUESTSTATE.PENDING);
    }

    @Test
    public void testChangeState() {
        friendshipRequest.setState(REQUESTSTATE.APPROVED);
        assertEquals(friendshipRequest.getState(), REQUESTSTATE.APPROVED);

        friendshipRequest.setState(REQUESTSTATE.REJECTED);
        assertEquals(friendshipRequest.getState(), REQUESTSTATE.REJECTED);
    }
}
