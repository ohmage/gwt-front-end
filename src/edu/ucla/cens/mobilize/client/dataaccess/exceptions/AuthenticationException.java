package edu.ucla.cens.mobilize.client.dataaccess.exceptions;

/**
 * A specific ServerException to indicate a a failure of credentials.  The
 * passed user and/or password was incorrect.
 * 
 * @author jhicks
 *
 */
@SuppressWarnings("serial")
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
