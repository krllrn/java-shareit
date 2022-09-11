package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String name;
    private String description;
    private Boolean available;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "request_id")
    private Long requestId;

    public Item(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Item(User owner, String name, String description, Boolean available, Comment comment, Long requestId) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.available = available;
        this.comment = comment;
        this.requestId = requestId;
    }
}
