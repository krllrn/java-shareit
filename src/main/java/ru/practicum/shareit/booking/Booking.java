package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    private User booker;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingState status;

    @Transient
    private String bookingFromOwner;

    @Column(name = "item_owner_id")
    private long itemOwnerId;

    @Transient
    private String feedback;

    public Booking(Item item, LocalDateTime start, LocalDateTime end, User booker, BookingState status, String bookingFromOwner, long itemOwnerId, String feedback) {
        this.item = item;
        this.start = start;
        this.end = end;
        this.booker = booker;
        this.status = status;
        this.bookingFromOwner = bookingFromOwner;
        this.itemOwnerId = itemOwnerId;
        this.feedback = feedback;
    }
}
