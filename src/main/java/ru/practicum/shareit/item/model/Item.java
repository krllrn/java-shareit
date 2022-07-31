package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Item {
    private long id;
    private User owner;
    private String name;
    private String description;
    private Boolean available;
}
