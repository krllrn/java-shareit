package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemShort {
    long id;
    String name;

    public ItemShort(long id) {
        this.id = id;
    }
}
