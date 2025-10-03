package dev.playerblair.kuro.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    public void testToString() {
        User user = new User();
        user.setId(1L);
        assertEquals("User (ID:1)", user.toString());
    }

    @Test
    public void testCreate() {
        User user = User.create("user", "pass");
        assertEquals("user", user.getUsername());
        assertEquals("pass", user.getPassword());
    }

    @Test
    public void testCreateException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.create("user", null)
        );
    }
}
