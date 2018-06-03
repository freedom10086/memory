package com.tencent.memory.dao;

import com.tencent.memory.model.Image;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ImageMapper {

    String IMAGE_COLUNM = "image.id as image_id, " +
            "image.galleryId, " +
            "image.groupId, " +
            "image.url," +
            "image.description," +
            "image.likes," +
            "image.comments," +
            "image.created as image_created ";

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
            "inner join user_gallery on user_gallery.galleryId = image.galleryId and user_gallery.uid = #{uid} " +
            //"where image.galleryId in (select galleryId from user_gallery where user_gallery.uid = #{uid}) " +
            "where image.created < user_gallery.exited " +
            "order by id ${order} " +
            "limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getAll(@Param("uid") long uid,
                       @Param("start") int start, @Param("size") int size,
                       @Param("order") String order);


    // 查询某个相册里面所有的图片
    @Select("SELECT image.*," + UserMapper.USER_COLUNM + " FROM image " +
            "inner join user on user.id = image.creater " +
            "inner join user_gallery on user_gallery.galleryId = #{galleryId} and user_gallery.uid = #{uid} " +
            "where image.galleryId = #{galleryId} " +
            "and image.created < user_gallery.exited " +
            "order by image.id ${order} limit #{size} offset #{start}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getAllByGallery(@Param("galleryId") long galleryId,
                                @Param("uid") long uid,
                                @Param("start") int start,
                                @Param("size") int size,
                                @Param("order") String order);

    // 查询所有相册里面所有的图片组
    @Select("select v.*," + UserMapper.USER_COLUNM + " from image as v  " +
            "inner join user on v.creater = user.id  " +
            "INNER JOIN (" +
            "select image_group.id from image_group " +
            "inner join user_gallery on user_gallery.galleryId = image_group.galleryId and user_gallery.uid = #{uid} " +
            "where image_group.galleryId in " +
            "(select galleryId from user_gallery where user_gallery.uid = #{uid}) " +
            "and image_group.created < user_gallery.exited " +
            "order by id DESC  limit #{size} offset #{start}" +
            ") as v2 ON v.groupId = v2.id " +
            "order by v.groupId ${order}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getGroupsAll(@Param("uid") long uid,
                             @Param("start") long start, @Param("size") int size,
                             @Param("order") String order);


    // 查询某个相册里面所有的图片组
    // 要分页
    @Select("select v.*," + UserMapper.USER_COLUNM + " from image as v  " +
            "inner join user on v.creater = user.id  " +
            "INNER JOIN (" +
            "select image_group.id from image_group " +
            "inner join user_gallery on user_gallery.galleryId = #{galleryId} and user_gallery.uid = #{uid} " +
            "where image_group.galleryId = #{galleryId} " +
            "and image_group.created < user_gallery.exited " +
            "order by id DESC  limit #{size} offset #{start}" +
            ") as v2 " +
            "ON v.groupId = v2.id " +
            "order by v.groupId ${order}")
    @ResultMap("com.tencent.memory.dao.ImageMapper.imageMap")
    List<Image> getGroupsByGallery(@Param("galleryId") long galleryId,
                                   @Param("uid") long uid,
                                   @Param("start") long start, @Param("size") int size,
                                   @Param("order") String order);


    @Update("UPDATE image SET description = #{description}, updated = now() WHERE id = #{id}")
    int update(Image image);

    @Update("UPDATE image SET likes = likes + 1 where id = #{imageId}")
    int addLike(@Param("imageId") long imageId);

    @Delete("delete from image where id = #{id}")
    int delete(@Param("id") long id);
}
