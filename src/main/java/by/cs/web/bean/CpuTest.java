package by.cs.web.bean;


import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name="cpuTest")
@ViewScoped
public class CpuTest implements Serializable {

	public CpuTest() {

	}

	public String test() {

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Test!!!", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		return "Test";
	}

}
