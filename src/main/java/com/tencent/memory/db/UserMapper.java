package com.tencent.memory.db;

import com.tencent.memory.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper
@Component
public interface UserMapper {

    @Insert("INSERT INTO user(openId,name,avatar,gender) VALUES " +
            "(#{openId},#{name},#{avatar},#{gender})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT * FROM user WHERE id = #{uid}")
    User findById(@Param("uid") int uid);

    @Select("SELECT * FROM user WHERE openId = #{openId}")
    User findByOpenId(@Param("openId") String openId);


    @Select("SELECT * FROM user limit #{size} offset #{start} order by id {order}")
    List<User> getAll(@Param("start") int start, @Param("size") int size, @Param("order") String order);

    // 修改名字
    @Update("UPDATE user SET  name = #{name} WHERE id = #{uid}")
    int updatePassword(@Param("uid") int uid, @Param("name") String name);


    // 删除用户
    @Delete("delete from user where id = #{uid}")
    int delete(@Param("uid") int uid);
}
