package com.tencent.memory.controller;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.ApiResult;
import com.tencent.memory.model.ApiResultBuilder;
import com.tencent.memory.model.Message;
import com.tencent.memory.service.MessageService;
import com.tencent.memory.util.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import java.util.List;

@RestController
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/messages/")
    public ApiResult<List<Message>> getMyMessages(ServletRequest req) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Paging paging = (Paging) req.getAttribute(Attrs.paging);

        return new ApiResultBuilder<List<Message>>()
                .success(messageService.getMyMessages(uid, paging))
                .build();
    }

    @GetMapping("/messages/unread")
    public ApiResult<Integer> getUnReadCount(
            ServletRequest req,
            @RequestParam(value = "startId", defaultValue = "0") int startId) {

        Long uid = (Long) req.getAttribute(Attrs.uid);
        Integer count = messageService.getUnreadCount(uid,startId);

        return new ApiResultBuilder<Integer>()
                .success(count)
                .build();
    }
}
