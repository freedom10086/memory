package com.tencent.memory;

import com.tencent.memory.service.LikeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private LikeService likeService;

    @Test
    public void testLike() {
        System.out.println(likeService.dolike(1, 55));
        System.out.println(likeService.dolike(1, 66));
        System.out.println(likeService.dolike(1, 77));
    }
}
