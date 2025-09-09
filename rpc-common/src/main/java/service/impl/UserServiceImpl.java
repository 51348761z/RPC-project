package service.impl;

import pojo.User;
import service.UserService;

import java.util.Random;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Integer id) {
        System.out.println("Client called getUserById with ID: " + id);
        // mimic the action of getting user from datastore
        Random random = new Random();
        // UUID.randomUUID() generates a random UUID which is globally unique
        // User.builder() creates a new User object with the Builder pattern of Lombok
        User user = User.builder()
                .username(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean())
                .build();
        return user;
    }
    @Override
    public Integer insertUserId(User user) {
        System.out.println("Client called insertUserId with user: " + user.getUsername());
        return user.getId();
    }
}
