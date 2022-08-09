package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ItemDto {
    private long id;

    @NotEmpty
    @NotBlank
    @NotNull
    private String name;

    @NotEmpty
    @NotBlank
    @NotNull
    private String description;

    @NotNull
    private Boolean available;

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
