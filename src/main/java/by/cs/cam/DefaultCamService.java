package by.cs.cam;

import com.github.sarxos.webcam.Webcam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitriy V.Yefremov
 */
public class DefaultCamService  implements CamService<BufferedImage> {

    private boolean isRunning;
    private List<Webcam> webcams;
    private Webcam webcam;

    private static final Logger logger = LoggerFactory.getLogger(DefaultCamService.class);

    private static volatile DefaultCamService instance;

    private DefaultCamService() {
        init();
    }

    public static DefaultCamService getInstance() {
        if (instance == null) {
            synchronized (DefaultCamService.class) {
                if(instance == null) {
                    instance = new DefaultCamService();
                }
            }
        }

        return instance;
    }

    /**
     * Initialize web cams
     */
    private void init() {
        //Init web cams
        if (!Webcam.getWebcams().isEmpty()) {
            webcams = Webcam.getWebcams();
            webcam = Webcam.getDefault();
        }
    }

    @Override
    public boolean isAvaliable() {
        return webcam == null;
    }

    @Override
    public void start() {
        if (webcam == null) {
            logger.error("DefaultCamService error [start]: No camera!");
            return;
        }

        if (webcam.isOpen()) {
            logger.info("DefaultCamService info Camera is already running!");
            return;
        }

        webcam.open();
    }

    @Override
    public void stop() {

        if (webcam == null) {
            logger.error("DefaultCamService error [stop]: No camera!");
            return;
        }

        webcam.close();
        Webcam.shutdown();
        logger.info("Camera stopped!");
    }

    @Override
    public List<?> getCams() {
        return webcams != null ? webcams : new ArrayList<>();
    }

    @Override
    public BufferedImage getImage() {
        return webcam != null ? webcam.getImage() : null;
    }


}
