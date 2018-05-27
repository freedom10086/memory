package com.tencent.memory.dao;

import com.tencent.memory.model.Gallery;
import com.tencent.memory.model.Image;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ImageMapper {

    @Insert("INSERT INTO image(galleryId,groupId,url,creater,description) VALUES " +
            "(#{galleryId},#{groupId},#{url},#{creater.id},#{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Image image);

    @Select("SELECT * FROM image WHERE id = #{id}")
    Gallery findById(@Param("id") int id);

    // 根据关键字查找用户所有相册里面的图片
    @Select("SELECT * FROM image " +
            "inner join user on user.id = image.creater " +
            "inner join user_gallery on user_gallery.galleryId = gallery.id " +
            "where user_gallery.uid = #{uid} " +
            "and description like %#{description}% limit #{size} offset #{start} order by id #{order}")
    List<Image> searchByDescription(@Param("uid") long uid,
                                    @Param("description") String description,
                                    @Param("start") int start, @Param("size") int size,
                                    @Param("order") String order);

    // 查询用户所有的相册里面的相片
    @Select("SELECT * FROM image " +
            "inner join user on user.id = image.creater " +
            "inner join user_gallery on user_gallery.galleryId = gallery.id " +
            "where user_gallery.uid = #{uid} " +
            "limit #{size} offset #{start} order by id #{order}")
    List<Image> getAllByCreated(@Param("uid") long uid,
                                @Param("start") int start, @Param("size") int size,
                                @Param("order") String order);

    // 查询用户所有的相册里面的相片
    @Select("SELECT * FROM image " +
            "inner join user on user.id = image.creater " +
            "inner join user_gallery on user_gallery.galleryId = gallery.id " +
            "where user_gallery.uid = #{uid} " +
            "limit #{size} offset #{start} order by updated #{order}")
    List<Image> getAllByUpdated(@Param("galleryId") long galleryId,
                                @Param("start") int start, @Param("size") int size,
                                @Param("order") String order);

    // 查询某个相册里面所有的图片
    @Select("SELECT * FROM image inner join user on user.id = image.creater " +
            "where image.galleryId = #{galleryId} " +
            "limit #{size} offset #{start} order by id #{order}")
    List<Image> getGalleryAllByCreated(@Param("galleryId") long galleryId,
                                @Param("start") int start, @Param("size") int size,
                                @Param("order") String order);

    // 查询某个相册里面所有的图片
    @Select("SELECT * FROM image inner join user on user.id = image.creater " +
            "where image.galleryId = #{galleryId} " +
            "limit #{size} offset #{start} order by updated #{order}")
    List<Image> getGalleryAllByUpdated(@Param("galleryId") long galleryId,
                                @Param("start") int start, @Param("size") int size,
                                @Param("order") String order);

    @Update("UPDATE image SET description = #{description}, updated = now() WHERE id = #{id}")
    int update(Image image);


    @Delete("delete from image where id = #{id}")
    int delete(@Param("id") int id);
}
