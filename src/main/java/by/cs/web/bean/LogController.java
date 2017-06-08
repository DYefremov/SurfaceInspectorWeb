package by.cs.web.bean;

import by.cs.Constants;
import org.apache.log4j.WriterAppender;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * @author Dmitriy V.Yefremov
 */
@ManagedBean(eager = true)
@SessionScoped
public class LogController extends WriterAppender implements Serializable {

    private static EventBus eventBus;
    private static final StringBuilder builder = new StringBuilder();

    public LogController() {

    }

    @PostConstruct
    public void init() {
        //Deleting initial logs
        clearLogs();
        eventBus = EventBusFactory.getDefault().eventBus();
    }

    public void activateOptions() {
        setWriter(createWriter(new OutStream()));
        super.activateOptions();
    }

    public  String getLog() {
        return builder.toString();
    }

    public void setLog(String log) {
        builder.append(log);
    }

    public void clearLogs() {
        builder.delete(0, builder.length());
    }

    private static void updateLogs(String str) {

        if (eventBus != null) {
            builder.append(str);
            eventBus.publish(Constants.LOG_RESOURCE, "message");
        }
    }

    private static class OutStream extends OutputStream {

        public OutStream() {

        }

        public void close() {

        }

        public void flush() {

        }

        public void write(final byte[] b) throws IOException {
            updateLogs(new String(b));
        }

        public void write(final byte[] b, final int off, final int len) throws IOException {
           updateLogs(new String(b, off, len));
        }

        public void write(final int b) throws IOException {
            updateLogs(String.valueOf(b));
        }
    }

}
