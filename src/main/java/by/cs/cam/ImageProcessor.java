package by.cs.cam;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Dmitriy V. Yefremov
 */
public class ImageProcessor {

    public ImageProcessor() {

    }

    /**
     * @return prepared data
     */
    public int[] getPreparedData(BufferedImage image) {

        if (image == null) {
            return new int[0];
        }

        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = grayImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

       return image.getRGB(0, 0, 1, image.getHeight(), null, 0, 1);
    }

    /**
     * @param image
     * @return image bytes
     */
    public byte[] getImageBytes(BufferedImage image) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (image == null) {
            return bos.toByteArray();
        }

        int w = image.getWidth(null);
        int h = image.getHeight(null);
        int scale = 2;

        BufferedImage bufferedImage = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        g.drawImage(image, 10, 10, w * scale, h * scale, null);

        try {
            ImageIO.write(bufferedImage, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

}
