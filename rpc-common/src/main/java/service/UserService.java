package service;

import pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User username);
}
