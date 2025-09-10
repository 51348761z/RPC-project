package RpcCommon.service;

import RpcCommon.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User username);
}
