package by.cs.web.bean;

import by.cs.Constants;
import by.cs.web.StandaloneServer;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CaptureEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Dmitriy V.Yefremov
 */
@ManagedBean
public class ServerController implements Serializable {

    private BarChartModel model;
    private String log = "";

    public ServerController() {

    }

    @PostConstruct
    public void init() {
        initModel();
        //add values for test
        setData(new int[] {100, 200, 300, 400 ,500});
    }

    public BarChartModel getModel() {
        return model;
    }

    public void setModel(BarChartModel model) {
        this.model = model;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    /**
     * Stops service from client
     */
    public void stopService() {

        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        StandaloneServer server = (StandaloneServer) ctx.getAttribute(Constants.SERVER_REFERENCE);
        server.stopServer();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Server shutdown!", "OK!");
        RequestContext.getCurrentInstance().showMessageInDialog(message);
    }

    /**
     * Capturing image from client side
     *
     * @param captureEvent
     */
    public void onCapture(CaptureEvent captureEvent) {

        byte[] data = captureEvent.getData();

        String fileName = "tmp/output.jpg";

        FileImageOutputStream imageOutputStream;
        try {
            imageOutputStream = new FileImageOutputStream(new File(fileName));
            imageOutputStream.write(data, 0, data.length);
            imageOutputStream.close();
        }
        catch(IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    }

    /**
     * Set data for chart
     * @param values
     */
    public void setData(int[] values) {

        ChartSeries series = new ChartSeries();
        series.setLabel("Values");
        for (int i = 0; i < values.length; i++) {
            series.set(i, values[i]);
        }

        model.addSeries(series);
    }

    /**
     *
     */
    private void initModel() {

        model = new BarChartModel();
        model.setTitle("Bar Chart");
        model.setLegendPosition("ne");

        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("");

        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Values");
        yAxis.setMin(0);
        yAxis.setMax(1000);
    }

}
