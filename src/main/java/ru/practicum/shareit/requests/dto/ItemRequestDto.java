package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemRequests;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemRequestDto {

    private long id;

    @NotEmpty
    @NotNull
    @NotBlank
    private String description;

    @FutureOrPresent()
    private LocalDateTime created;

    private List<ItemRequests> items;

    public ItemRequestDto(String description) {
        this.description = description;
    }
}
