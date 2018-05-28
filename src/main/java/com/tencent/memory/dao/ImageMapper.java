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

    @Select("select count(0) from image where id = #{imageId}")
    int isExist(@Param("imageId") long imageId);

    @Select("SELECT image.*," + UserMapper.USER_COLUNM + " FROM image" +
            "inner join user on user.id = image.creater " +
            "WHERE image.id = #{id}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    Image findById(@Param("id") long id);

    // 查询用户所有的相册里面的相片
    @Select("SELECT image.*," + UserMapper.USER_COLUNM + " FROM image " +
            "inner join user on user.id = image.creater " +
            "where image.galleryId in (select galleryId from user_gallery where user_gallery.uid = #{uid}) " +
            "order by id ${order} " +
            "limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getAll(@Param("uid") long uid,
                       @Param("start") int start, @Param("size") int size,
                       @Param("order") String order);

    // 根据关键字查找用户所有相册里面的图片
    @Select("SELECT image.*," + UserMapper.USER_COLUNM + " FROM image " +
            "inner join user on user.id = image.creater " +
            "where image.galleryId in (select galleryId from user_gallery where user_gallery.uid = #{uid}) " +
            "and description like %#{description}% limit #{size} offset #{start} order by id #{order}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> searchByDescription(@Param("uid") long uid,
                                    @Param("description") String description,
                                    @Param("start") int start, @Param("size") int size,
                                    @Param("order") String order);


    // 查询某个相册里面所有的图片
    @Select("SELECT image.*," + UserMapper.USER_COLUNM + " FROM image " +
            "inner join user on user.id = image.creater " +
            "where image.galleryId = #{galleryId} " +
            "order by image.id ${order} limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getAllByGallery(@Param("galleryId") long galleryId,
                                @Param("start") int start,
                                @Param("size") int size,
                                @Param("order") String order);

    // 查询所有相册里面所有的图片组
    @Select("select image.*," + UserMapper.USER_COLUNM + " from image as v  " +
            "INNER JOIN (select image_group.id from image_group where image_group.galleryId in " +
            "(select galleryId from user_gallery where user_gallery.uid = #{uid}) " +
            "order by id DESC  limit #{size} offset #{start}) as v2 ON v.groupId = v2.id " +
            "inner join user on v.creater = user.id  " +
            "order by v.groupId ${order}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getGroupsAll(@Param("uid") long uid,
                             @Param("start") long start, @Param("size") int size,
                             @Param("order") String order);


    // 查询某个相册里面所有的图片组
    // 要分页
    @Select("select image.*," + UserMapper.USER_COLUNM + " from image as v  " +
            "INNER JOIN (select image_group.id from image_group where image_group.galleryId = #{galleryId} " +
            "order by id DESC  limit #{size} offset #{start}) as v2 ON v.groupId = v2.id " +
            "inner join user on v.creater = user.id  " +
            "order by v.groupId ${order}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getGroupsByGallery(@Param("galleryId") long galleryId,
                                   @Param("start") long start, @Param("size") int size,
                                   @Param("order") String order);


    @Update("UPDATE image SET description = #{description}, updated = now() WHERE id = #{id}")
    int update(Image image);

    @Update("UPDATE image SET likes = likes + 1 where id = #{imageId}")
    int addLike(@Param("imageId") long imageId);

    @Delete("delete from image where id = #{id}")
    int delete(@Param("id") long id);
}
