package com.tencent.memory.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserGalleryMapper {

    //2038-01-19 03:14:07 最大的timestamp
    @Insert("replace into user_gallery(uid,galleryId,exited) values (#{uid},#{galleryId},'2038-01-19 03:14:07')")
    int addGallery(@Param("uid") long uid, @Param("galleryId") long galleryId);

    //并不是真正的删除
    @Update("update user_gallery set exited = now() where uid = #{uid} and galleryId = #{galleryId}")
    int setGalleryDel(@Param("uid") long uid, @Param("galleryId") long galleryId);

    //真正的删除
    @Delete("delete from gallery where uid = #{uid} and galleryId = #{galleryId}")
    int removeGallery(@Param("uid") long uid, @Param("galleryId") long galleryId);
}
