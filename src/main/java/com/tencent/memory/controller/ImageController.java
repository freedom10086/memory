package com.tencent.memory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.memory.config.Attrs;
import com.tencent.memory.model.*;
import com.tencent.memory.service.ImageService;
import com.tencent.memory.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 处理相册里面的
 * 图片的增删改查
 */
@RestController
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // 查询某张图片
    @GetMapping("/images/{imageId}")
    public ApiResult<Image> getImage(@PathVariable("imageId") long imageId) {
        Image image = imageService.findById(imageId);
        if (image == null) {
            return new ApiResultBuilder<Image>()
                    .error(HttpStatus.NOT_MODIFIED.value(), "图片不存在:" + imageId).build();
        } else {
            return new ApiResultBuilder<Image>()
                    .success(image).build();
        }
    }

    // 给相册添加图片
    // images Image数组
    @PostMapping("/images/")
    public ApiResult<Integer> addImageToGallery(HttpServletRequest req,
                                                @RequestParam("galleryId") long galleryId,
                                                @RequestParam("images") String images,
                                                @RequestParam("description") String description) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Image[] imageList;
        try {
            imageList = new ObjectMapper().readValue(images, Image[].class);
        } catch (IOException e) {
            throw new MyException(HttpStatus.BAD_REQUEST, "images解析错误");
        }

        if (imageList.length == 0) {
            throw new MyException(HttpStatus.BAD_REQUEST, "需要上传的图片不能为空");
        }
        int affets = imageService.addImagesToGallery(galleryId, uid, imageList, description);

        return new ApiResultBuilder<Integer>().success(affets).build();
    }

    // 从相册删除图片
    @DeleteMapping("/images/{imageId}")
    public ApiResult<Integer> delImageFromGallery(HttpServletRequest req,
                                                  @RequestParam("galleryId") long galleryId,
                                                  @PathVariable("imageId") long imageId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        int affets = imageService.delImageFromGallery(galleryId, uid, imageId);
        return new ApiResultBuilder<Integer>().success(affets).build();
    }

    // 从相册删除图片
    @DeleteMapping("/image-groups/{groupId}")
    public ApiResult<Integer> delImageGroup(HttpServletRequest req,
                                            @RequestParam("galleryId") long galleryId,
                                            @PathVariable("groupId") long groupId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        int affets = imageService.delImageGroup(galleryId, uid, groupId);
        return new ApiResultBuilder<Integer>().success(affets).build();
    }
}
