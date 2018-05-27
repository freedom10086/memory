package com.tencent.memory.dao;

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

    @Select("SELECT * FROM image" +
            "inner join user on user.id = image.creater " +
            "WHERE image.id = #{id}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    Image findById(@Param("id") long id);

    // 根据关键字查找用户所有相册里面的图片
    @Select("SELECT * FROM image " +
            "inner join user on user.id = image.creater " +
            "inner join user_gallery on user_gallery.galleryId = gallery.id " +
            "where user_gallery.uid = #{uid} " +
            "and description like %#{description}% limit #{size} offset #{start} order by id #{order}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> searchByDescription(@Param("uid") long uid,
                                    @Param("description") String description,
                                    @Param("start") int start, @Param("size") int size,
                                    @Param("order") String order);

    // 查询用户所有的相册里面的相片
    // 要分页
    @Select("SELECT * FROM image " +
            "inner join user on user.id = image.creater " +
            "inner join user_gallery on user_gallery.galleryId = gallery.id " +
            "where user_gallery.uid = #{uid} " +
            "order by id ${order} " +
            "limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getAllByCreated(@Param("uid") long uid,
                                @Param("start") int start, @Param("size") int size,
                                @Param("order") String order);


    // 查询某个相册里面所有的图片
    // 要分页
    @Select("SELECT * FROM image " +
            "inner join user on user.id = image.creater " +
            "where image.galleryId = #{galleryId} " +
            "order by image.id ${order} " +
            "limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getPagedByGallery(@Param("galleryId") long galleryId,
                                  @Param("start") long start, @Param("size") int size,
                                  @Param("order") String order);

    // 查询某个相册里面所有的图片
    @Select("SELECT * FROM image " +
            "inner join user on user.id = image.creater " +
            "where image.galleryId = #{galleryId} " +
            "order by image.id ${order}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getAllByGallery(@Param("galleryId") long galleryId,
                                @Param("order") String order);

    @Update("UPDATE image SET description = #{description}, updated = now() WHERE id = #{id}")
    int update(Image image);


    @Delete("delete from image where id = #{id}")
    int delete(@Param("id") long id);
}
