package com.tencent.memory;

import com.tencent.memory.dao.GalleryMapper;
import com.tencent.memory.dao.UserMapper;
import com.tencent.memory.model.Gallery;
import com.tencent.memory.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GalleryServiceTest {

    @Autowired
    private GalleryMapper galleryMapper;

    @Test
    public void testInsert() {
        Gallery gallery = new Gallery();
        gallery.name = "测试相册";
        gallery.description = "测试相册描述";
        gallery.creater = new User();
        gallery.creater.id = 1;

        try {
            galleryMapper.insert(gallery);
        } catch (DuplicateKeyException e) {
            System.out.println("already have");
        }

    }


    @Test
    public void testFind() {
        Gallery gallery = galleryMapper.findById(1);
        assert gallery != null;
        assert gallery.creater != null;
        assert gallery.creater.id == 1;

        gallery = galleryMapper.findById(2);
        assert gallery == null;
    }
}
