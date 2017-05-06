package by.cs.web.bean;

import by.cs.Constants;
import com.github.sarxos.webcam.Webcam;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Dmitriy V.Yefremov
 */
@ManagedBean
@ApplicationScoped
public class CamController implements Serializable {

    private volatile BufferedImage mainImage;
    private volatile boolean isRunning;
    private List<Webcam> webcams;
    private Webcam webcam;
    private ServerController serverController;

    private static final Logger logger = LoggerFactory.getLogger(CamController.class);

    public CamController() {

    }

    @PostConstruct
    public void init() {
        //Init web cams
        if (!Webcam.getWebcams().isEmpty()) {
            webcams = Webcam.getWebcams();
            webcam = Webcam.getDefault();
        }
        serverController = new ServerController();
    }

    public StreamedContent getContent() {

        FacesContext context = FacesContext.getCurrentInstance();
        return context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE || !isRunning ?
                new DefaultStreamedContent() :
                new DefaultStreamedContent(new ByteArrayInputStream(getBytes()), "image/png", "image");
    }

    public List<Webcam> getWebcams() {
        return webcams;
    }

    public void setWebcams(List<Webcam> webcams) {
        this.webcams = webcams;
    }

    /**
     * Starts camera
     */
    public void start() {

        if (webcam == null) {
            logger.error("No camera!");
            return;
        }

        if (webcam.isOpen()) {
            logger.info("Camera is already running!");
            return;
        }

        webcam.open();

        Thread thread = new Thread(() -> {
            while (isRunning = true) {
                mainImage = webcam.getImage();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if ((mainImage) != null) {
                    mainImage.flush();
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
        //Execute every 2s.
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if (isRunning) {
                updateImg();
//                setData(mainImage);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * Stops camera
     */
    public void stop() {

        isRunning = false;

        if (webcam != null) {
            webcam.close();
            Webcam.shutdown();
            logger.info("Camera stopped!");
        }
    }

    /**
     * @param image
     */
    private void setData(BufferedImage image) {

        if (image == null) {
            return;
        }

        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = grayImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        int pixels[] = image.getRGB(0, 0, 1, image.getHeight(), null, 0, 1);

        if (serverController != null) {
            serverController.setData(pixels);
        }
    }

    /**
     * @return image bytes
     */
    private byte[] getBytes() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (mainImage == null) {
            return bos.toByteArray();
        }

        int w = mainImage.getWidth(null);
        int h = mainImage.getHeight(null);
        int scale = 2;

        BufferedImage image = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.drawImage(mainImage, 10, 10, w * scale, h * scale, null);

        try {
            ImageIO.write(image, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    /**
     * Updating cam form with Pimefaces Push
     */
    public void updateImg() {

        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish(Constants.CAM_RESOURCE, "");
    }
}
