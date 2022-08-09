package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;

@Data
public class Booking {
    Item item;
    LocalDate from;
    LocalDate to;
    String bookingFromOwner;
    String feedback;

}
