package edu.ucla.cens.mobilize.client.exceptions;

/**
 * Indicates a problem with the parameters passed to the server API.
 * 
 * @author jhicks
 *
 */
@SuppressWarnings("serial")
public class ApiException extends RuntimeException {
    private String _errorCode = "";
    
    public ApiException(String errorCode, String message) {
        super(message);
        _errorCode = errorCode;
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ApiException(Throwable cause) {
        super(cause);
    }
    
    public String getErrorCode() {
      return _errorCode;
    }
    
}
