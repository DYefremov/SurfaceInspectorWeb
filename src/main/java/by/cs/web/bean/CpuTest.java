package by.cs.web.bean;


import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "cpuTest")
public class CpuTest implements Serializable {

	private volatile boolean connect;
	private volatile String value;

	public CpuTest() {
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
