package edu.ucla.cens.mobilize.client.dataaccess.exceptions;

/**
 * Indicates a problem with the parameters passed to the server API.
 * 
 * @author jhicks
 *
 */
@SuppressWarnings("serial")
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ApiException(Throwable cause) {
        super(cause);
    }
}
