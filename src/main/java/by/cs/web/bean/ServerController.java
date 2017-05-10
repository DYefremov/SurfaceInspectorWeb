package by.cs.web.bean;

import by.cs.Constants;
import by.cs.web.StandaloneServer;
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;

/**
 * @author Dmitriy V.Yefremov
 */
@ManagedBean(name = "serverController")
@ApplicationScoped
public class ServerController implements Serializable {

    private BarChartModel model;
    private String log = "Log";

    public ServerController() {

    }

    @PostConstruct
    public void init() {
        model = new BarChartModel();
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
     * Set data for chart
     * @param values
     */
    public void setData(int[] values) {

        ChartSeries series = new ChartSeries();
        series.setLabel("Values");

        for (int i = 0; i < values.length; i++) {
            int value = values[i]/10000 * -1;
            series.set(i, value);
        }

        model.clear();
        model.addSeries(series);
    }
}
