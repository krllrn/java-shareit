package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(mapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        return mapper.userToDto(userRepository.findByIdIs(id));
    }

    @Override
    public UserDto create(Long id, UserDto userDto) {
        return mapper.userToDto(userRepository.save(mapper.userToEntity(id, userDto)));
    }

    @Override
    public UserDto updateValues(Long id, UserDto userDto) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        return mapper.userToDto(userRepository.save(mapper.userToEntity(id, userDto)));
    }

    @Override
    public void delete(Long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ID must be positive");
        }
        userRepository.delete(userRepository.findByIdIs(id));
    }
}
