package ru.practicum.shareit.requests;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "request_owner_id")
    private User reqOwnerId;

    public ItemRequest(String description, LocalDateTime created, User reqOwnerId) {
        this.description = description;
        this.created = created;
        this.reqOwnerId = reqOwnerId;
    }
}
