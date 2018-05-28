package com.tencent.memory.dao;

import com.tencent.memory.model.Comment;
import com.tencent.memory.model.Order;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CommentMapper {

    @Insert("INSERT INTO comment(imageId,creater,content) VALUES " +
            "(#{imageId},#{creater.id},#{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Select("SELECT comment.*," + UserMapper.USER_COLUNM + " from comment " +
            "inner join user on user.id = comment.creater " +
            "WHERE comment.id = #{id}")
    @ResultMap("com.tencent.memory.dao.CommentMapper.commentMap")
    Comment findById(@Param("id") long id);


    // 查询某个图片的所有评论
    @Select("SELECT comment.*," + UserMapper.USER_COLUNM + " FROM comment " +
            "inner join user on user.id = comment.creater " +
            "where comment.imageId = #{imageId} " +
            "order by comment.created ${order} " +
            "limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.CommentMapper.commentMap")
    List<Comment> getComments(@Param("imageId") long imageId,
                              @Param("start") int start, @Param("size") int size,
                              @Param("order") Order order);


    @Update("UPDATE comment SET content = #{content} WHERE id = #{id}")
    int update(Comment comment);


    @Delete("delete from comment where id = #{id} and creater = #{uid}")
    int delete(@Param("id") long id, @Param("uid") long uid);
}
