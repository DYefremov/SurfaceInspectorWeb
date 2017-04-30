package by.cs.web.bean;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name="cpuTest")
@ViewScoped
public class CpuTest implements Serializable {

	public CpuTest() {

	}

	public String test() {
		System.out.println("TestTest");
		return "Test";
	}

}
