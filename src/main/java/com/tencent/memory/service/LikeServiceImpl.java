package com.tencent.memory.service;

import com.tencent.memory.dao.ImageMapper;
import com.tencent.memory.model.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class LikeServiceImpl implements LikeService {

    private static final Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

    private StringRedisTemplate redisTemplate;
    private ImageMapper imageMapper;

    private ValueOperations valueOperations;

    @PostConstruct
    private void init() {
        valueOperations = redisTemplate.opsForValue();
    }

    @Autowired
    public LikeServiceImpl(StringRedisTemplate redisTemplate, ImageMapper imageMapper) {
        this.redisTemplate = redisTemplate;
        this.imageMapper = imageMapper;
    }

    @Override
    public long dolike(long imageId, long uid) {

        long count = redisTemplate.execute((RedisCallback<Long>) connection -> {
            boolean before = connection.setBit(("likes-" + imageId).getBytes(), uid, true);
            if (before) {
                throw new MyException(HttpStatus.BAD_REQUEST, "你已经点赞,不要重复点赞");
            }

            return connection.bitCount(("likes-" + imageId).getBytes());
        });

        //db update
        imageMapper.addLike(imageId);

        logger.info("do like uid:{} imageId:{} likecount:{}", uid, imageId, count);
        return count;
    }

    @Override
    public long likesCount(long imageId) {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.bitCount(("likes-" + imageId).getBytes()));
    }
}
