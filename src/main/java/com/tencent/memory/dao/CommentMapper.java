package com.tencent.memory.dao;

import com.tencent.memory.model.Comment;
import com.tencent.memory.model.Image;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CommentMapper {

    @Insert("INSERT INTO comment(galleryId,creater,content) VALUES " +
            "(#{galleryId},#{creater.id},#{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment image);

    @Select("SELECT * FROM comment " +
            "inner join user on user.id = comment.creater " +
            "WHERE id = #{id}")
    Comment findById(@Param("id") int id);


    // 查询某个图片的所有评论
    @Select("SELECT * FROM comment " +
            "inner join user on user.id = comment.creater " +
            "where image.galleryId = #{galleryId} " +
            "limit #{size} offset #{start} order by id #{order}")
    List<Image> getImageAllByCreated(@Param("galleryId") long galleryId,
                                     @Param("start") int start, @Param("size") int size,
                                     @Param("order") String order);


    @Update("UPDATE comment SET content = #{content}, created = now() WHERE id = #{id}")
    int update(Comment comment);


    @Delete("delete from comment where id = #{id}")
    int delete(@Param("id") int id);
}
