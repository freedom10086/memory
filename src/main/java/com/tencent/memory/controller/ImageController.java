package com.tencent.memory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.memory.config.Attrs;
import com.tencent.memory.config.Config;
import com.tencent.memory.model.ApiResult;
import com.tencent.memory.model.ApiResultBuilder;
import com.tencent.memory.model.Image;
import com.tencent.memory.model.ImageGroup;
import com.tencent.memory.service.ImageService;
import com.tencent.memory.util.Paging;
import com.tencent.memory.util.TextUtils;
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
        Paging paging = (Paging) req.getAttribute(Attrs.paging);
        List<ImageGroup> res = imageService.getImageGroupsFromGallery(galleryId, paging);
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

    // 给相册添加图片
    // images Image数组
    @PostMapping("/images/")
    public ApiResult<Integer> addImageToGallery(HttpServletRequest req,
                                                @RequestParam("galleryId") long galleryId,
                                                @RequestParam("images") String images,
                                                @RequestParam("description") String description) {

        Long uid = (Long) req.getAttribute(Attrs.uid);
        Image[] imageArray;
        try {
            imageArray = new ObjectMapper().readValue(images, Image[].class);
        } catch (IOException e) {
            //e.printStackTrace();
            return new ApiResultBuilder<Integer>()
                    .error(HttpStatus.BAD_REQUEST.value(), "images解析错误:"+e.getMessage()).build();
        }

        if (imageArray.length == 0) {
            return new ApiResultBuilder<Integer>()
                    .error(HttpStatus.BAD_REQUEST.value(), "需要上传的图片不能为空").build();
        }

        for (Image image : imageArray) {
            if (TextUtils.isEmpty(image.url) || !image.url.startsWith(Config.bucketPathCdnPrefix)) {
                return new ApiResultBuilder<Integer>()
                        .error(HttpStatus.BAD_REQUEST.value(), "图片地址不合法：" + image.url).build();
            }

            image.url = image.url.substring(Config.bucketPathCdnPrefix.length());
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
