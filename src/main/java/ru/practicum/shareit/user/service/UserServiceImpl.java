package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(int id) {
        User user = findByIdOrThrow(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        try {
            User saved = userRepository.save(userMapper.toUser(userDto));
            return userMapper.toUserDto(saved);
        } catch (DataIntegrityViolationException ex) {
            String msg = "Email уже существует";
            throw new ValidationException(msg, HttpStatus.CONFLICT);
        }
    }

    @Override
    public UserDto update(int userId, UserDto userDto) {
        User toUpdate = findByIdOrThrow(userId);
        try {
            if (userDto.getName() != null) {
                toUpdate.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                toUpdate.setEmail(userDto.getEmail());
            }
            userRepository.saveAndFlush(toUpdate);
            return userMapper.toUserDto(toUpdate);
        } catch (DataIntegrityViolationException ex) {
            String msg = "Email уже существует";
            throw new ValidationException(msg, HttpStatus.CONFLICT);
        }
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    private User findByIdOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String msg = String.format("Пользователь ID=%d не найден", userId);
                    return new NotFoundException(msg, HttpStatus.NOT_FOUND);
                });
    }

    private boolean validateUserEmail(UserDto userDto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        if (!violations.isEmpty()) {
            return false;
        }
        return true;
    }
}
