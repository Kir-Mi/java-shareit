package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public List<UserDto> findAll() {
        List<User> users = userStorage.findAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (isEmailExist(userDto.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует", HttpStatus.CONFLICT);
        }
        User user = userStorage.create(UserMapper.toUser(userDto));
        itemStorage.addNewUserToMap(user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(int userId, UserDto userDto) {
        if (!userStorage.isUserExist(userId)) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
        UserDto updatedUserDto = getUserById(userId);

        if (validateUserEmail(userDto)) {
            if (!userDto.getEmail().equals(updatedUserDto.getEmail())) {
                if (isEmailExist(userDto.getEmail())) {
                    throw new ValidationException("Пользователь с таким email уже существует", HttpStatus.CONFLICT);
                }
            }
        }

        String email = userDto.getEmail();
        if (!(email == null)) {
            updatedUserDto.setEmail(email);
        }
        if (userDto.getName() != null) {
            updatedUserDto.setName(userDto.getName());
        }
        userStorage.update(userId, UserMapper.toUser(updatedUserDto));
        return updatedUserDto;
    }

    @Override
    public void delete(int id) {
        itemStorage.deleteUserFromMap(id);
        userStorage.delete(id);
    }

    private boolean isEmailExist(String email) {
        List<UserDto> users = findAll();
        for (UserDto user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
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
