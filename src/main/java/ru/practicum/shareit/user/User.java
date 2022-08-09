package ru.practicum.shareit.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class User {
    private long id;

    private String email;

    private String name;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
