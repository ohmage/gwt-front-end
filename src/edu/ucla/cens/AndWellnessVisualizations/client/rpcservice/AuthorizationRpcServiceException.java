package edu.ucla.cens.AndWellnessVisualizations.client.rpcservice;

/**
 * A specific RpcServiceException to indicate a a failure of credentials.  Either the
 * user was automatically logged out, or the login attempt failed (invalid user or password).
 * 
 * @author jhicks
 *
 */
@SuppressWarnings("serial")
public class AuthorizationRpcServiceException extends RpcServiceException {
    public AuthorizationRpcServiceException(String message) {
        super(message);
    }
    
    public AuthorizationRpcServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AuthorizationRpcServiceException(Throwable cause) {
        super(cause);
    }
}
