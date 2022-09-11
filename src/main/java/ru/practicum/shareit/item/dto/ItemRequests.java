package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemRequests {
    long id;
    String name;
    String description;
    Boolean available;
    long requestId;
}
