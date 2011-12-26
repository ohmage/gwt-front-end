package edu.ucla.cens.mobilize.client.exceptions;

/**
 * A specific ServerException to indicate a a failure of credentials.  The
 * passed user and/or password was incorrect.
 * 
 * @author jhicks
 *
 */
@SuppressWarnings("serial")
public class AuthenticationException extends RuntimeException {
    private String _errorCode = "";
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AuthenticationException(Throwable cause) {
        super(cause);
    }
    
    public AuthenticationException(String errorCode, String message) {
      super(message);
      this._errorCode = errorCode;
    }
    
    public String getErrorCode() {
      return this._errorCode != null && !this._errorCode.isEmpty() ? this._errorCode : "0200";
    }
}
