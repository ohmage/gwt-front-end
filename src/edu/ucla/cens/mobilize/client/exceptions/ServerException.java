package edu.ucla.cens.mobilize.client.exceptions;

/**
 * Simple wrapper for RuntimeExceptions that are thrown from the rpcservice.
 * 
 * @author jhicks
 *
 */
@SuppressWarnings("serial")
public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }
    
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServerException(Throwable cause) {
        super(cause);
    }
}
