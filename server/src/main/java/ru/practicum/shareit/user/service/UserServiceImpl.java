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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(int id) {
        User user = findByIdOrThrow(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        try {
            User saved = userRepository.save(UserMapper.toUser(userDto));
            return UserMapper.toUserDto(saved);
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
            return UserMapper.toUserDto(toUpdate);
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
}
