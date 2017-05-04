package by.cs.web.bean;


import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name="cpuTest" , eager = true)
@RequestScoped
public class CpuTest implements Serializable {

	public CpuTest() {

	}

	public String test() {

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Test!!!", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		return "Test";
	}

}
