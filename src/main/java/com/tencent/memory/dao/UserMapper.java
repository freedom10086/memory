package com.tencent.memory.dao;

import com.tencent.memory.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper
@Component
public interface UserMapper {

    String USER_COLUNM = "user.id as user_id, " +
            "user.name as user_name, " +
            "user.created as user_created, " +
            "user.avatar," +
            "user.gender," +
            "user.openId ";

    @Insert("INSERT INTO user(openId,name,avatar,gender) VALUES " +
            "(#{openId},#{name},#{avatar},#{gender})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT * FROM user WHERE id = #{uid}")
    @ResultMap("com.tencent.memory.dao.UserMapper.userMap")
        // 引用XML里配置的映射器
    User findById(@Param("uid") int uid);

    @Select("SELECT * FROM user WHERE openId = #{openId}")
    User findByOpenId(@Param("openId") String openId);


    @Select("SELECT * FROM user limit #{size} offset #{start} order by id #{order}")
    List<User> getAll(@Param("start") int start, @Param("size") int size, @Param("order") String order);

    // 修改名字
    @Update("UPDATE user SET  name = #{name} WHERE id = #{id}")
    int update(@Param("id") int uid, @Param("name") String name);


    // 删除用户
    @Delete("delete from user where id = #{uid}")
    int delete(@Param("uid") int uid);

    // 删除用户
    @Delete("delete from user where openId = #{openId}")
    int deleteByOpenId(@Param("openId") String openId);
}
