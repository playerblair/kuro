package dev.playerblair.kuro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    public static User create(String username, String password) {
        User user = new User();
        if (username == null) throw new IllegalArgumentException("Username cannot be null");
        user.setUsername(username);
        if (password == null) throw new IllegalArgumentException("Password cannot be null");
        user.setPassword(password);
        return user;
    }

    @Override
    public String toString() {
        return "User (ID:" + id + ")";
    }
}
