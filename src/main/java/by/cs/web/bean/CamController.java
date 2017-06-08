package by.cs.web.bean;

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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
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
@SessionScoped
public class CamController implements Serializable {

    private volatile BufferedImage mainImage;
    private volatile int updateTime = 1;
    private volatile boolean isRunning;
    private ImageProcessor imageProcessor;
    private CamService<BufferedImage> camService;
    private EventBus eventBus;
    private ScheduledExecutorService executorService;

    @ManagedProperty(value = "#{chartController}")
    private ChartController chartController;

    private static final Logger logger = LoggerFactory.getLogger(CamController.class);

    public CamController() {

    }

    @PostConstruct
    public void init() {
        camService = DefaultCamService.getInstance();
        imageProcessor = new ImageProcessor();
        eventBus = EventBusFactory.getDefault().eventBus();
    }

    public ChartController getChartController() {
        return chartController;
    }

    public void setChartController(ChartController chartController) {
        this.chartController = chartController;
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

        if (isRunning) {
            return;
        }

        camService.start();
        isRunning = true;

        Thread thread = new Thread(() -> {
            while (isRunning) {
                mainImage =  camService.getImage();

                if ((mainImage) != null) {
                    mainImage.flush();
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
        //Execute every [updateTime].
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if (isRunning) {
                setData(mainImage);
            }
        }, 0, updateTime, TimeUnit.SECONDS);
    }

    /**
     * Stops camera
     */
    public void stop() {

        isRunning = false;

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        camService.stop();
    }

    /**
     * @param image
     */
    private void setData(BufferedImage image) {

        if (image != null) {
            chartController.setData(imageProcessor.getPreparedData(mainImage));
        }
    }

    /**
     * @return image bytes
     */
    private byte[] getBytes() {
       return imageProcessor.getImageBytes(mainImage);
    }

}
