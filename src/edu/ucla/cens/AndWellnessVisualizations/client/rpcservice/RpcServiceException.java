package edu.ucla.cens.AndWellnessVisualizations.client.rpcservice;

/**
 * Simple wrapper for RuntimeExceptions that are thrown from the rpcservice.
 * 
 * @author jhicks
 *
 */
@SuppressWarnings("serial")
public class RpcServiceException extends RuntimeException {
    public RpcServiceException(String message) {
        super(message);
    }
    
    public RpcServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RpcServiceException(Throwable cause) {
        super(cause);
    }
}
