package by.cs.web.bean;

import by.cs.Constants;
import by.cs.web.StandaloneServer;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;

/**
 * @author Dmitriy V.Yefremov
 */
@ManagedBean
@ViewScoped
public class ServerController implements Serializable {

    public ServerController() {

    }

    public void stopService() {

        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        StandaloneServer server = (StandaloneServer) ctx.getAttribute(Constants.SERVER_REFERENCE);
        server.stopServer();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Server shutdown!", "Test OK!");
        RequestContext.getCurrentInstance().showMessageInDialog(message);
    }
}
