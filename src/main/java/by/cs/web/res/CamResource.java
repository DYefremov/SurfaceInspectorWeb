package by.cs.web.res;

import by.cs.Constants;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.impl.JSONEncoder;

/**
 * @author Dmitriy V.Yefremov
 */
@PushEndpoint(Constants.CAM_RESOURCE)
public class CamResource {

    @OnMessage(encoders = {JSONEncoder.class})
    public String onMessage(String message) {
        return message;
    }
}
