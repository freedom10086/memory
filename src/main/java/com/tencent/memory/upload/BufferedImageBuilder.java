package com.tencent.memory.upload;

import com.tencent.memory.model.MyException;
import org.springframework.http.HttpStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BufferedImageBuilder {

    public static BufferedImage toBufferedImage(Image image) {

        if (image instanceof BufferedImage) {

            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded

        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's

        // implementation, see e661 Determining If an Image Has Transparent
        // Pixels

        // boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the
        // screen

        BufferedImage bimage = null;

        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();

        try {

            // Determine the type of transparency of the new buffered image

            int transparency = Transparency.OPAQUE;

            // Create the buffered image

            GraphicsDevice gs = ge.getDefaultScreenDevice();

            GraphicsConfiguration gc = gs.getDefaultConfiguration();

            bimage = gc.createCompatibleImage(

                    image.getWidth(null), image.getHeight(null), transparency);

        } catch (HeadlessException e) {

            // The system does not have a screen

        }

        if (bimage == null) {

            // Create a buffered image using the default color model

            int type = BufferedImage.TYPE_INT_RGB;

            // int type = BufferedImage.TYPE_3BYTE_BGR;//by wang

            if (image.getWidth(null) == -1 || image.getHeight(null) == -1) {
                throw new MyException(HttpStatus.BAD_REQUEST, "image type is not supported.");
            }
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);

        }

        // Copy image to buffered image

        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image

        g.drawImage(image, 0, 0, null);

        g.dispose();

        return bimage;

    }

}
