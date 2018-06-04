package com.tencent.memory.controller;

import com.tencent.memory.api.ApiResult;
import com.tencent.memory.api.ApiResultBuilder;
import com.tencent.memory.config.Attrs;
import com.tencent.memory.config.Config;
import com.tencent.memory.model.*;
import com.tencent.memory.service.GalleryService;
import com.tencent.memory.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class InviteController {

    private final GalleryService galleryService;

    @Autowired
    public InviteController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    // 生成invitecode
    @ResponseBody
    @PostMapping("/invite/")
    public ApiResult<String> genInviteCode(ServletRequest req,
                                           @RequestParam("galleryId") long galleryId) {

        Long uid = (Long) req.getAttribute(Attrs.uid);
        Gallery gallery = galleryService.loadGalleryWithoutImage(galleryId, 0);
        if (gallery == null) {
            return new ApiResultBuilder<String>()
                    .error(HttpStatus.BAD_REQUEST.value(), "相册不存在,id:" + galleryId).build();
        }

        if (gallery.creater.id != uid) {
            return new ApiResultBuilder<String>()
                    .error(HttpStatus.FORBIDDEN.value(), "只有相册创建人才能邀请").build();
        }

        //有效期三天
        InviteCode inviteCode = new InviteCode(galleryId, uid, Token.day * 3);
        String code = inviteCode.genInviteCode(Config.inviteSecreateKey);

        return new ApiResultBuilder<String>().success(code).build();
    }

    // 不要验证
    // 检查验证码的有效性
    @ResponseBody
    @RequestMapping("/invite/check")
    public ApiResult<Gallery> checkInviteCode(@RequestParam("invitecode") String code) {
        InviteCode inviteCode;
        try {
            inviteCode = InviteCode.decode(code, Config.inviteSecreateKey);
        } catch (IOException e) {
            return new ApiResultBuilder<Gallery>().error(HttpStatus.BAD_REQUEST.value(),
                    e.getMessage()).build();
        }

        if (inviteCode.isExpired()) {
            return new ApiResultBuilder<Gallery>().error(HttpStatus.UNAUTHORIZED.value(),
                    "邀请码已过期").build();
        }

        Gallery gallery = galleryService.loadGalleryWithoutImage(inviteCode.galleryId, 0);
        if (gallery == null) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.BAD_REQUEST.value(), "相册不存在,id:" + inviteCode.galleryId).build();
        }


        return new ApiResultBuilder<Gallery>().success(gallery).build();
    }

    // 需要验证
    // 接受邀请添加相册
    @ResponseBody
    @RequestMapping(value = "/invite/join", produces = "application/json")
    public ApiResult<Gallery> acceptInvite(HttpServletRequest req,
                                           @RequestParam("invitecode") String code) {
        Long uid = (Long) req.getAttribute(Attrs.uid);
        InviteCode inviteCode;
        try {
            inviteCode = InviteCode.decode(code, Config.inviteSecreateKey);
        } catch (IOException e) {
            return new ApiResultBuilder<Gallery>().error(HttpStatus.BAD_REQUEST.value(),
                    e.getMessage()).build();
        }

        if (inviteCode.isExpired()) {
            return new ApiResultBuilder<Gallery>().error(HttpStatus.UNAUTHORIZED.value(),
                    "邀请码已过期").build();
        }

        Gallery gallery = galleryService.loadGalleryWithoutImage(inviteCode.galleryId, 0);
        if (gallery == null) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.BAD_REQUEST.value(), "相册不存在,id:" + inviteCode.galleryId).build();
        }

        if (gallery.creater.id == uid) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.BAD_REQUEST.value(), "你不能邀请你自己").build();

        }

        try {
            galleryService.addGallery(uid, inviteCode.galleryId);
        } catch (DuplicateKeyException e) {
            return new ApiResultBuilder<Gallery>()
                    .error(HttpStatus.BAD_REQUEST.value(), "你已经加入此相册").build();
        }

        return new ApiResultBuilder<Gallery>().success(gallery).build();
    }


    // 浏览器点击
    @ResponseBody
    @GetMapping(value = "/invite-page.html")
    public String acceptInviteHTML(@RequestParam("invitecode") String invitecode) {
        InviteCode inviteCode;
        try {
            inviteCode = InviteCode.decode(invitecode, Config.inviteSecreateKey);
        } catch (IOException | MyException e) {
            return String.format(ERR_HTML, "链接错误 " + e.getMessage());
        }

        if (inviteCode.isExpired()) {
            return String.format(ERR_HTML, "邀请链接已过期");
        }

        Gallery gallery = galleryService.loadGalleryWithoutImage(inviteCode.galleryId, 0);
        if (gallery == null) {
            return String.format(ERR_HTML, "相册不存在,id:" + inviteCode.galleryId);
        }


        return String.format(HTML_1, gallery.creater.name, gallery.name, invitecode);
    }

    private final static String HTML = "<!DOCTYPE HTML>\n" +
            "<html>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>相册加入邀请-印迹</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<p> %s 邀请你加入相册%s 是否同意?</p>\n" +
            "<a href=\"memory://invite?invitecode=%s\">同意</a>\n" +
            "</body>\n" +
            "</html>";

    public final static String HTML_1 = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>相册邀请-印迹</title>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <style>\n" +
            "        * {\n" +
            "            margin: 0px;\n" +
            "            padding: 0px;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body style=\"overflow: hidden\">\n" +
            "    <div style=\"position: absolute; top: 30px; width: 100%%\">\n" +
            "        <div style=\"font-size: 3rem; text-align: center\">%s</div>\n" +
            "    </div>\n" +
            "    <div style=\"position: absolute; top: 21%%; width: 100%%\">\n" +
            "        <div style=\"font-size: 2rem; text-align: center\">%s 同意?</div>\n" +
            "    </div>\n" +
            "\n" +
            "    <img style=\"margin-bottom: 20px\" src=\"invite_img1.jpg\" width=\"100%%\">\n" +
            "    <div style=\"margin-left: 10%%;margin-right: 10%%\">\n" +
            "        <a href=\"memory://invite?invitecode=%s\"><img src=\"invite_img2.png\" width=\"100%%\"></a>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";

    private final static String ERR_HTML = "<!DOCTYPE HTML>\n" +
            "<html>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>错误-印迹</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<p>%s</p>\n" +
            "</body>\n" +
            "</html>";
}
