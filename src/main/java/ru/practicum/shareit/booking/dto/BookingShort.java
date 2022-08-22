package ru.practicum.shareit.booking.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BookingShort {
    private long id;
    private long bookerId;
}
