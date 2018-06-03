package com.tencent.memory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.memory.config.Attrs;
import com.tencent.memory.config.Config;
import com.tencent.memory.model.*;
import com.tencent.memory.service.ImageService;
import com.tencent.memory.util.Paging;
import com.tencent.memory.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 处理相册里面的
 * 图片的增删改查
 */
@RestController
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // 获取某个相册的分组列表
    // 应用 相册详情页
    @GetMapping("/galleries/{galleryId}/image-groups/")
    public ApiResult<List<ImageGroup>> getImageGroupsFromGallery(ServletRequest req,
                                                                 @PathVariable("galleryId") long galleryId) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        List<ImageGroup> res = imageService.getImageGroupsFromGallery(galleryId, uid, paging);
        return new ApiResultBuilder<List<ImageGroup>>().success(res).build();
    }

    // 获取最新的图片组
    // 应用 最新页面
    @GetMapping("/galleries/new-image-groups/")
    public ApiResult<List<ImageGroup>> getNewsImageGroup(ServletRequest req) {
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        Long uid = (Long) req.getAttribute(Attrs.uid);

        List<ImageGroup> res = imageService.getNewsImageGroups(uid, paging);
        return new ApiResultBuilder<List<ImageGroup>>().success(res).build();
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

    // 按列表查询某个相册里的所有图片
    // 列表视图
    @GetMapping("/galleries/{galleryId}/images/")
    public ApiResult<List<Image>> getAllByGallery(ServletRequest req,
                                                  @PathVariable("galleryId") long galleryId) {
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        Long uid = (Long) req.getAttribute(Attrs.uid);

        return new ApiResultBuilder<List<Image>>()
                .success(imageService.getAllByGallery(galleryId, uid, paging)).build();
    }

    // 给相册添加图片
    // images Image数组
    @PostMapping("/galleries/{galleryId}/images/")
    public ApiResult<Integer> addImageToGallery(HttpServletRequest req,
                                                @PathVariable("galleryId") long galleryId,
                                                @RequestParam("images") String images,
                                                @RequestParam("description") String description) {
        logger.info("request params -- galleryId:{}, images:{}, des:{}", galleryId, images, description);
        Long uid = (Long) req.getAttribute(Attrs.uid);
        UploadRequest[] imageArray;
        try {
            imageArray = new ObjectMapper().readValue(images, UploadRequest[].class);
        } catch (IOException e) {
            logger.error("addImageToGallery failed: {}", e.getMessage());
            e.printStackTrace();
            return new ApiResultBuilder<Integer>()
                    .error(HttpStatus.BAD_REQUEST.value(), "images解析错误:" + e.getMessage()).build();
        }

        if (imageArray.length == 0) {
            return new ApiResultBuilder<Integer>()
                    .error(HttpStatus.BAD_REQUEST.value(), "需要上传的图片不能为空").build();
        }

        for (UploadRequest image : imageArray) {
            if (TextUtils.isEmpty(image.url) || !image.url.startsWith(Config.bucketPathCdnPrefix)) {
                return new ApiResultBuilder<Integer>()
                        .error(HttpStatus.BAD_REQUEST.value(), "图片地址不合法：" + image.url).build();
            }

            image.url = image.url.substring(Config.bucketPathCdnPrefix.length());
            image.description = description;
        }

        int affets = imageService.addImagesToGallery(galleryId, uid, imageArray, description);

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
