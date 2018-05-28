package com.tencent.memory.service;

import com.tencent.memory.dao.CommentMapper;
import com.tencent.memory.model.Comment;
import com.tencent.memory.model.Order;
import com.tencent.memory.util.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public int addComment(Comment comment) {
        return commentMapper.insert(comment);
    }

    @Override
    public int delComment(long uid, long commentId) {
        return commentMapper.delete(uid, commentId);
    }

    @Override
    public Comment findById(long commentId) {
        return commentMapper.findById(commentId);
    }

    @Override
    public boolean isExist(long commentId) {
        return commentMapper.findById(commentId) != null;
    }

    @Override
    public List<Comment> getComments(long imageId, Paging paging, Order order) {
        return commentMapper.getComments(imageId, paging.start, paging.size, order);
    }
}
