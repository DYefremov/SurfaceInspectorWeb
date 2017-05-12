package by.cs.web.bean;


import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "cpuController")
@SessionScoped
public class CpuController implements Serializable {

	private volatile boolean connect;
	private volatile String ip = "127.0.0.1";
	private volatile String variable = "VAR1";

	public CpuController() {
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public void apply() {
		System.out.println("Apply!");
	}

	public String test() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Test!!!", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		return "Test";
	}

	public void connectTest() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, !connect ? "Connecting..." : "Disconnecting..", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String getStatus() {
		return connect ? "Connected" : "Disconnected";
	}
}
