package wongs.tinyrpc.common.service;

import wongs.tinyrpc.common.dto.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User username);
}
