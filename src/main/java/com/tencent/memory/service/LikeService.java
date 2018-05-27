package com.tencent.memory.service;

public interface LikeService {

    //返回现在的like数目
    //不能取消
    long dolike(long imageId,long uid);

    long likesCount(long imageId);
}
