package com.tencent.memory.upload;

import com.tencent.memory.model.MyException;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageConverter {

    /**
     * 将非JPG格式图片(例如PNG、BMP、GIF)转换成JPG
     *
     * @param data
     * @return byte[]
     */
    public static byte[] toJpg(byte[] data) {
        BufferedImage image = null;
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(data);
            image = ImageIO.read(input);
            if (image == null) {
                throw new MyException(HttpStatus.BAD_REQUEST,
                        "image type is not supported.");
            }

            BufferedImage newImage = new BufferedImage(image.getWidth(),
                    image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            Graphics g = newImage.createGraphics();
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 0, 0,
                    image.getWidth(), image.getHeight(), null);
            g.dispose();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(newImage, "jpg", output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    /**
     * 将非PNG格式图片(例如JPG、BMP、GIF)转换成PNG
     *
     * @param data
     * @return byte[]
     */
    public static byte[] toPng(byte[] data) {
        BufferedImage image = null;
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(data);
            image = ImageIO.read(input);
            if (image == null) {
                throw new MyException(HttpStatus.BAD_REQUEST, "image type is not supported.");
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }


    public static byte[] toGif(byte[] data) {
        BufferedImage image = null;
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(data);
            image = ImageIO.read(input);
            if (image == null) {
                throw new MyException(HttpStatus.BAD_REQUEST, "image type is not supported.");
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "gif", output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
