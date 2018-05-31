package com.tencent.memory.service;

import com.tencent.memory.model.Message;
import com.tencent.memory.util.Paging;

import java.util.List;

public interface MessageService {

    // 添加一条消息
    int addMessage(Message message);

    // 查询我的消息列表
    List<Message> getMyMessages(long uid, Paging paging);

    // 获得未读消息计数
    int getUnreadCount(long uid,int startId);
}
