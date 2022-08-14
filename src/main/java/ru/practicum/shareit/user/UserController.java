package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final Mapper mapper;

    @Autowired
    public UserController(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(mapper::userToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        return mapper.userToDto(userRepository.getReferenceById(id));
    }

    @PostMapping
    public UserDto create(@PathVariable (required = false) Long id,
                          @Valid @RequestBody UserDto userDto) {
        return mapper.userToDto(userRepository.save(mapper.userToEntity(id, userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto updateValues(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        return mapper.userToDto(userRepository.save(mapper.userToEntity(id, userDto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        userRepository.delete(userRepository.getReferenceById(id));
    }

    @ExceptionHandler({EntityNotFoundException.class})
    void handleEntityNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
