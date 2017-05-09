package by.cs.cam;

import java.awt.image.RenderedImage;
import java.util.List;

/**
 * @author Dmitriy V.Yefremov
 */
public interface CamService {

    boolean isAvaliable();

    void start();

    void stop();

    List<?> getCams();

     RenderedImage  getImage();
}
