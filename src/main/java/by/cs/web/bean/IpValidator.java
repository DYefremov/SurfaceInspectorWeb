package by.cs.web.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author Dmitriy V.Yefremov
 */
@FacesValidator("ipValidator")
public class IpValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(IpValidator.class);

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        String ip = value.toString();

        if (!ip.matches("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}")) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid ip address.", null);
            logger.error("IpValidator error :  Invalid ip address.");
            throw new ValidatorException(message);
        }

    }

}
