package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(1, "name", "email");
        user2 = new User(2, "name2", "email2");
    }

    @Test
    public void findAll_returnAllUsers() {
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.findAll();

        assertEquals(users.size(), userDtos.size());
    }

    @Test
    void getUserById_whenUserExists_thenReturnUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        User actualUser = UserMapper.toUser(userService.getUserById(1));

        assertEquals(user1, actualUser);
    }

    @Test
    void getUserById_whenUserNotExists_thenReturnNotFoundExceptionThrow() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void createUser_Successfully() {
        when(userRepository.save(user1)).thenReturn(user1);

        UserDto createdUser = userService.create(UserMapper.toUserDto(user1));

        assertNotNull(createdUser);
        assertEquals(user1.getId(), createdUser.getId());
        assertEquals(user1.getName(), createdUser.getName());
        assertEquals(user1.getEmail(), createdUser.getEmail());
    }

    @Test
    void createUser_EmailAlreadyExists() {
        when(userRepository.save(user1)).thenThrow(new DataIntegrityViolationException("Duplicate key"));

        assertThrows(ValidationException.class, () -> userService.create(UserMapper.toUserDto(user1)));
    }


    @Test
    void updateUser_Successfully() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.saveAndFlush(user1)).thenReturn(user1);

        UserDto updatedUser = userService.update(user1.getId(), UserMapper.toUserDto(user1));

        assertNotNull(updatedUser);
        assertEquals(user1.getId(), updatedUser.getId());
        assertEquals(user1.getName(), updatedUser.getName());
        assertEquals(user1.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).findById(user1.getId());
        verify(userRepository, times(1)).saveAndFlush(user1);
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(user1.getId(), UserMapper.toUserDto(user1)));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void updateUser_EmailAlreadyExists() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.saveAndFlush(user1)).thenThrow(new DataIntegrityViolationException("Duplicate key"));

        assertThrows(ValidationException.class, () -> userService.update(user1.getId(), UserMapper.toUserDto(user1)));

        verify(userRepository, times(1)).findById(user1.getId());
        verify(userRepository, times(1)).saveAndFlush(user1);
    }

    @Test
    void deleteUser_Successfully() {
        assertDoesNotThrow(() -> userService.delete(user1.getId()));

        verify(userRepository, times(1)).deleteById(user1.getId());
    }
}