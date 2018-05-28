package com.tencent.memory.service;

import com.tencent.memory.model.Comment;
import com.tencent.memory.model.Order;
import com.tencent.memory.util.Paging;

import java.util.List;

public interface CommentService {

    int addComment(Comment comment);

    int delComment(long uid, long commentId);

    Comment findById(long commentId);

    boolean isExist(long commentId);

    List<Comment> getComments(long imageId, Paging paging, Order order);
}
