package com.tencent.memory;

import com.tencent.memory.dao.UserMapper;
import com.tencent.memory.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert() {
        User user = new User();
        user.name = "test";
        user.avatar = "avatar";
        user.gender = "女";
        user.openId = "55EAE8EDA1A6762EF99EB31AF96C59AB";

        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            System.out.println("already have");
        }

    }

    @Test
    public void testFind() {
        User user = userMapper.findById(1);
        assert user != null;
        System.out.println(user.toString());

        user = userMapper.findById(2);
        assert user == null;

        user = userMapper.findByOpenId("55EAE8EDA1A6762EF99EB31AF96C59AA");
        assert user != null;
    }

    @Test
    public void  testUpdate() {
        userMapper.update(1,"测试");
        userMapper.update(2,"测试");

        User user = userMapper.findById(1);
        assert user.name.equals("测试");

        userMapper.update(1,"管理员");
        user = userMapper.findById(1);
        assert user.name.equals("管理员");
    }

    @Test
    public void testDeleteUser() {
        int deletes = userMapper.deleteByOpenId("55EAE8EDA1A6762EF99EB31AF96C59AB");
        System.out.println("delete:" + deletes);
    }
}
