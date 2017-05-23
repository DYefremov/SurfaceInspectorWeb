package by.cs.web.bean;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * @author Dmitriy V.Yefremov
 */
@ManagedBean
@SessionScoped
public class ChartController implements Serializable{

    private BarChartModel model;

    @PostConstruct
    public void init() {
       initModel();
    }

    public BarChartModel getModel() {
        return model;
    }

    public void setModel(BarChartModel model) {
        this.model = model;
    }

    private void initModel() {
        model = new BarChartModel();
        setData(new int[] {0});
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
