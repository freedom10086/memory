package com.tencent.memory.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ImageGroupMapper {

    @Delete("delete from image_group where id = #{id}")
    int delete(@Param("id") long id);
}
