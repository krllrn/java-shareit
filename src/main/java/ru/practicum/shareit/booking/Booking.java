package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.Entity;
import java.time.LocalDate;

@Data
//@Entity
public class Booking {
    Item item;
    LocalDate from;
    LocalDate to;
    String bookingFromOwner;
    String feedback;

}
