package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserId;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BookingDto {

    private long id;

    @FutureOrPresent(message = "Start date in past - WRONG!")
    private LocalDateTime start;

    @FutureOrPresent(message = "End date in past - WRONG!")
    private LocalDateTime end;

    private BookingState status;

    private UserId booker;

    @JsonAlias({"itemId"})
    private ItemShort item;
}
