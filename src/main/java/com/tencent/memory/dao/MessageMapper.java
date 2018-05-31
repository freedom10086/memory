package com.tencent.memory.dao;

import com.tencent.memory.model.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface MessageMapper {

    @Insert("INSERT INTO message(uid,creater,imageId,type,content) VALUES " +
            "( (select creater from image where image.id = #{imageId} limit 1)," +
            "#{creater.id},#{imageId},#{type},#{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addMessage(Message message);


    @Select("select message.*," + UserMapper.USER_COLUNM + "," + ImageMapper.IMAGE_COLUNM + " from message " +
            "inner join user on message.creater = user.id " +
            "inner join image on message.imageId = image.id " +
            "where message.uid = #{uid} " +
            "order by message.created desc " +
            "limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.MessageMapper.messageMap")
    List<Message> getMessages(@Param("uid") long uid, @Param("start") int start, @Param("size") int size);


    @Select("select count(0) from message where message.uid = #{uid} " +
            "and message.id > #{startId}")
    int getUnreadCount(@Param("uid") long uid, @Param("startId") long startId);

}
