package com.tencent.memory.dao;

import com.tencent.memory.model.Gallery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface GalleryMapper {


    @Insert("INSERT INTO gallery(name,description,type,creater) VALUES " +
            "(#{name},#{description},#{type},#{creater.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Gallery gallery);

    @Select("SELECT gallery.*, " + UserMapper.USER_COLUNM +
            "FROM gallery " +
            "inner join user on user.id = gallery.creater " +
            "WHERE gallery.id = #{id}")
    @ResultMap("com.tencent.memory.dao.GalleryMapper.galleryMap")
        // 引用XML里配置的映射器
    Gallery findById(@Param("id") long id);

    // 搜索我的所有相册
    @Select("SELECT gallery.*," + UserMapper.USER_COLUNM + " FROM gallery " +
            "inner join user on user.id = gallery.creater " +
            "inner join user_gallery on user_gallery.galleryId = gallery.id " +
            "where gallery.id in (select user_gallery.galleryId from user_gallery where user_gallery.uid = #{uid}) " +
            "and (gallery.name like concat('%',#{name},'%')) " +
            "order by user_gallery.created ${order} " +
            "limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.GalleryMapper.galleryMap")
    List<Gallery> searchByName(@Param("uid") long uid, @Param("name") String name,
                               @Param("start") int start, @Param("size") int size,
                               @Param("order") String order);


    // 获取我的所有相册
    @Select("SELECT gallery.*, " + UserMapper.USER_COLUNM + " FROM gallery " +
            "inner join user on user.id = gallery.creater " +
            "inner join user_gallery on user_gallery.galleryId = gallery.id " +
            "where gallery.id in (select user_gallery.galleryId from user_gallery where user_gallery.uid = #{uid}) " +
            "order by user_gallery.created ${order} " +
            "limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.GalleryMapper.galleryMap")
    List<Gallery> getAllByCreated(@Param("uid") long uid,
                                  @Param("start") int start, @Param("size") int size,
                                  @Param("order") String order);


    @Update("UPDATE gallery SET name = #{name}, " +
            "description = #{description}, " +
            "type = #{type}, " +
            "updated = now() " +
            "WHERE id = #{id}")
    int update(Gallery gallery);


    // 真正的删除要小心
    @Delete("delete from gallery where id = #{id}")
    int delete(@Param("id") long id);
}
