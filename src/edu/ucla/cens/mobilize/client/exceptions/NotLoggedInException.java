package edu.ucla.cens.mobilize.client.exceptions;

/**
 * A specific ServerException to indicate a failure of the auth token to authenticate.
 * The user was automatically timed out.
 * 
 * @author jhicks
 *
 */
@SuppressWarnings("serial")
public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException(String message) {
        super(message);
    }
    
    public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NotLoggedInException(Throwable cause) {
        super(cause);
    }
}
