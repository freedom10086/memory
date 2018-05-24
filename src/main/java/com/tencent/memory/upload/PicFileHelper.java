package com.tencent.memory.upload;

import com.tencent.memory.model.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;


public class PicFileHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PicFileHelper.class);

    public static Map<String, Object> doUpload(byte[] src, int uploadType)
            throws IOException {

        // 如果上传的原图不是JPG格式(例如PNG、BMP、GIF)，则转换成PNG格式图片
        // 这里如果默认转成JPG格式，存在某些图片会红图的现象，而转换成PNG则不会
        byte[] data = src;
        if (!ImageChecker.isJpeg(src)) {
            LOGGER.debug("Image not jpeg format");
            data = ImageConverter.toJpg(src);
            LOGGER.debug("Image src size={}, dst size={}", src.length, data.length);
        }

        BufferedImage image = getImage(data);
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] dest = src;
        if (width > 1366) {
            dest = scaleImage(data, 1366, height * 1366 / width);
        }


        // TODO real upload stuff

        return null;
    }

    /**
     * 缩放图片.
     *
     * @param data        原始图片
     * @param finalWidth  目标宽度
     * @param finalHeight 目标高度
     */
    public static byte[] scaleImage(byte[] data, int finalWidth,
                                    int finalHeight) throws IOException {
        BufferedImage image = getImage(data);
        int width = image.getWidth();
        int height = image.getHeight();
        if (width <= 0 || height <= 0) {
            throw new MyException(HttpStatus.BAD_REQUEST, "invalid src image width or height");
        }

        BufferedImage newImage = new BufferedImage(finalWidth, finalHeight, image.getType());
        Graphics g = newImage.createGraphics();
        scale(g, image, finalWidth, finalHeight);
        g.dispose();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(newImage, "jpg", output);
        return output.toByteArray();
    }

    /**
     * 根据原图的宽高的比例缩放成目标宽高.
     */
    protected static void scale(Graphics g, BufferedImage image,
                                int finalWidth, int finalHeight) {
        int x = 0;
        int y = 0;
        int width = finalWidth;
        int height = finalHeight;
        double xScale = image.getWidth() * 1.0 / finalWidth;
        double yScale = image.getHeight() * 1.0 / finalHeight;
        if (xScale <= yScale) {
            width = image.getWidth();
            height = (int) (finalHeight * xScale);
            x = 0;
            y = (image.getHeight() - height) / 2;
        } else {
            width = (int) (finalWidth * yScale);
            height = image.getHeight();
            x = (image.getWidth() - width) / 2;
            y = 0;
        }
        g.drawImage(image, 0, 0, finalWidth, finalHeight, x, y, width + x,
                height + y, null);
    }

    /**
     * Toolkit加载图片，不会红图，但可能CPU负载会过高
     */
    public static BufferedImage getImage(byte[] data) throws IOException {
        Image imageTookit = Toolkit.getDefaultToolkit().createImage(data);
        return BufferedImageBuilder.toBufferedImage(imageTookit);
    }

    public static String getFileNmae(long uin) {
        return System.currentTimeMillis() + "_" + uin;
    }

}