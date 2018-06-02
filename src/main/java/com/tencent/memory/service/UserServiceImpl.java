package com.tencent.memory.service;


import com.tencent.memory.dao.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public int updateUserInfo(long uid, String name) {
        return userMapper.update(uid,name);
    }
}
