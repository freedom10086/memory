package com.tencent.memory.dao;

import com.tencent.memory.model.Gallery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface GalleryMapper {

    @Insert("INSERT INTO gallery(name,description,creater) VALUES " +
            "(#{name},#{description},#{creater.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Gallery gallery);

    @Select("SELECT * FROM gallery " +
            "inner join user on user.id = gallery.creater " +
            "WHERE gallery.id = #{id}")
    @ResultMap("com.tencent.memory.dao.GalleryMapper.galleryMap")  // 引用XML里配置的映射器
    Gallery findById(@Param("id") int id);

    @Select("SELECT * FROM gallery " +
            "inner join user on user.id = gallery.creater " +
            "where name like %#{name}% limit #{size} offset #{start} order by id #{order}")
    @ResultMap("com.tencent.memory.dao.GalleryMapper.galleryMap")
    List<Gallery> searchByName(@Param("name") String name,
                               @Param("start") int start, @Param("size") int size,
                               @Param("order") String order);


    @Select("SELECT * FROM gallery " +
            "inner join user on user.id = gallery.creater " +
            "limit #{size} offset #{start} order by id #{order}")
    @ResultMap("com.tencent.memory.dao.GalleryMapper.galleryMap")
    List<Gallery> getAllByCreated(@Param("start") int start, @Param("size") int size,
                                  @Param("order") String order);

    @Select("SELECT * FROM gallery inner " +
            "join user on user.id = gallery.creater " +
            "limit #{size} offset #{start} order by updated #{order}")
    @ResultMap("com.tencent.memory.dao.GalleryMapper.galleryMap")
    List<Gallery> getAllByUpdated(@Param("start") int start, @Param("size") int size,
                                  @Param("order") String order);


    @Update("UPDATE gallery SET name = #{name}, " +
            "description = #{description}, " +
            "updated = now() " +
            "WHERE id = #{id}")
    int update(Gallery gallery);


    @Delete("delete from gallery where id = #{id}")
    int delete(@Param("id") int id);
}
