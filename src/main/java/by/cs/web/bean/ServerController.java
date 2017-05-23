package by.cs.web.bean;

import by.cs.Constants;
import by.cs.web.StandaloneServer;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;

/**
 * @author Dmitriy V.Yefremov
 */
@ManagedBean
@ApplicationScoped
public class ServerController implements Serializable {

    public ServerController() {

    }

    /**
     * Starts service from client
     */
    public void startService() {

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
}
