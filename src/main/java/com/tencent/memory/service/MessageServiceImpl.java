package com.tencent.memory.service;

import com.tencent.memory.dao.MessageMapper;
import com.tencent.memory.model.Message;
import com.tencent.memory.util.Paging;
import com.tencent.memory.util.TextUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public int addMessage(Message message) {
        if (TextUtils.isEmpty(message.content)) {
            message.content = "";
        }

        return messageMapper.addMessage(message);
    }

    @Override
    public List<Message> getMyMessages(long uid, Paging paging) {
        return messageMapper.getMessages(uid, paging.start, paging.size);
    }

    @Override
    public int getUnreadCount(long uid, int startId) {
        return messageMapper.getUnreadCount(uid, startId);
    }
}
