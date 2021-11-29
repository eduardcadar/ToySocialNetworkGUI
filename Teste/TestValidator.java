import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.validator.FriendshipValidator;
import com.toysocialnetworkgui.validator.UserValidator;
import com.toysocialnetworkgui.validator.Validator;
import com.toysocialnetworkgui.validator.ValidatorException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestValidator {
    private final Validator<User> val1 = new UserValidator();
    private final Validator<Friendship> val2 = new FriendshipValidator();
    private final User u1 = new User("Ion", "Pop", "pop.ion@yahoo.com");
    private final User u2 = new User("Alex", "Popescu", "popescu.alex@yahoo.com");

    @Test
    public void testValidateNames() {
        // corect
        try {
            val1.validate(new User("ion andrei","pop","ion.pop@yahoo.com"));
        } catch (ValidatorException e) {
            assertTrue(false);
        }

        // firstName vid
        try {
            val1.validate(new User("", "pop", "i@ya.co"));
            fail();
        } catch (ValidatorException e) {
            assertTrue(true);
        }

        // lastName vid
        try {
            val1.validate(new User("ion", "", "i@ya.co"));
            fail();
        } catch (ValidatorException e) {
            assertTrue(true);
        }

        // firstName contine si alte caractere inafara de litere si spatii
        try {
            val1.validate(new User("io2n", "pop", "i@ya.co"));
            fail();
        } catch (ValidatorException e) {
           assertTrue(true);
        }

        // lastName contine si alte caractere inafara de litere si spatii
        try {
            val1.validate(new User("ion", "p.op", "i@ya.co"));
            fail();
        } catch (ValidatorException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testValidateEmail() {
        // corect
        try {
            val1.validate(new User("ionica","popescu","ion.popescu2@yahoo.com"));
        } catch (ValidatorException e) {
            fail();
        }

        // corect
        try {
            val1.validate(new User("ionica","popescu","ion.popescu2@yahoo.co.uk"));
        } catch (ValidatorException e) {
            fail();
        }

        // email nu are '.' dupa '@'
        try {
            val1.validate(new User("ionica","popescu","ion.popescu2@yahoo"));
            fail();
        } catch (ValidatorException e) {
            assertTrue(true);
        }

        // email are 3 '.' dupa '@'
        try {
            val1.validate(new User("ionica","popescu","ion.popescu2@yahoo.co.uk.ro"));
            fail();
        } catch (ValidatorException e) {
            assertTrue(true);
        }

        // email e vid
        try {
            val1.validate(new User("ionica","popescu",""));
            fail();
        } catch (ValidatorException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testValidateFriendship() {
        val2.validate(new Friendship(u1, u2));
        val2.validate(new Friendship(u2, u1));
        try {
            val2.validate(new Friendship(u1, u1));
            fail();
        } catch (ValidatorException e) {
            assertTrue(true);
        }
        try {
            val2.validate(new Friendship(u2, u2));
            fail();
        } catch (ValidatorException e) {
            assertTrue(true);
        }
    }
}
