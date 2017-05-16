package by.cs.web.bean;


import by.cs.cpu.CpuConnection;
import by.cs.cpu.CpuS7;
import by.cs.cpu.CpuS7Connection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "cpuController")
@SessionScoped
public class CpuController implements Serializable {

	private volatile boolean connect;
	private String variable;
	private CpuS7 cpuS7;
	private CpuConnection cpuConnection;

	public CpuController() {

	}

	@PostConstruct
	private void init() {
		cpuS7 = new CpuS7(0, 2, "127.0.0.1");
		cpuConnection = new CpuS7Connection(cpuS7);
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public CpuS7 getCpuS7() {
		return cpuS7;
	}

	public void setCpuS7(CpuS7 cpuS7) {
		this.cpuS7 = cpuS7;
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	public void apply() {
		System.out.println("Cpu info : " + cpuS7);
	}

	public String getInfo() {
		String infoMessage = "Info: \n" + cpuConnection.getInfo();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, infoMessage, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		return infoMessage;
	}

	public void connectTest() {

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, !connect ? "Connecting..." : "Disconnecting..", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		if (!connect) {
			System.out.println(cpuConnection.getCurrentCpu());
			cpuConnection.connect(cpuS7);
		} else {
			cpuConnection.disconnect();
		}
	}

	public String getStatus() {
		return connect ? "Connected" : "Disconnected";
	}
}
