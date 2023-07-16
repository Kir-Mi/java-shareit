package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class UserStorageImpl implements UserStorage {

    private HashMap<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void update(int userId, User user) {
        users.put(userId, user);
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    @Override
    public boolean isUserExist(int id) {
        if (users.containsKey(id)) {
            return true;
        }
        return false;
    }

    private int getNewId() {
        return idCounter++;
    }
}
