package by.cs.cam;

import java.util.List;

/**
 * @author Dmitriy V.Yefremov
 */
public interface CamService<T> {

    boolean isAvaliable();

    void start();

    void stop();

    List<?> getCams();

    T getImage();
}
