package by.cs.web.bean;

import by.cs.Constants;
import by.cs.cam.CamService;
import by.cs.cam.DefaultCamService;
import by.cs.cam.ImageProcessor;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Dmitriy V.Yefremov
 */
@ManagedBean
public class CamController implements Serializable {

    private volatile BufferedImage mainImage;
    private volatile int updateTime;
    private volatile boolean isRunning;
    private ImageProcessor imageProcessor;
    private CamService camService;

    private static final Logger logger = LoggerFactory.getLogger(CamController.class);

    public CamController() {

    }

    @PostConstruct
    public void init() {
        camService = DefaultCamService.getInstance();
        imageProcessor = new ImageProcessor(mainImage);
    }

    public StreamedContent getContent() {

        FacesContext context = FacesContext.getCurrentInstance();
        return context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE || !isRunning ?
                new DefaultStreamedContent() :
                new DefaultStreamedContent(new ByteArrayInputStream(getBytes()), "image/png", "image");
    }

    public List<Webcam> getWebcams() {
        return (List<Webcam>) camService.getCams();
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * Starts camera
     */
    public void start() {

        camService.start();

        Thread thread = new Thread(() -> {
            while (isRunning = true) {
                mainImage = (BufferedImage) camService.getImage();
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
        camService.stop();
    }

    /**
     * @param image
     */
    private void setData(BufferedImage image) {

        if (image == null) {
            return;
        }
    }

    /**
     * @return image bytes
     */
    private byte[] getBytes() {
       return imageProcessor.getImageBytes(mainImage);
    }

    /**
     * Updating cam form with Pimefaces Push
     */
    public void updateImg() {

        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish(Constants.CAM_RESOURCE, "");
    }
}
