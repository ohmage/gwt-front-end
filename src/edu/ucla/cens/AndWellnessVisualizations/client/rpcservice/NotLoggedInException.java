package edu.ucla.cens.AndWellnessVisualizations.client.rpcservice;

/**
 * A specific ServerException to indicate a a failure of credentials.  Either the
 * user was automatically logged out, or the login attempt failed (invalid user or password).
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
