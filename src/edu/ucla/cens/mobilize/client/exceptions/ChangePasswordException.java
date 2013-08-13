package edu.ucla.cens.mobilize.client.exceptions;

@SuppressWarnings("serial")
public class ChangePasswordException extends RuntimeException {
    private String _errorCode = "";
    
    public ChangePasswordException(String message) {
        super(message);
    }
    
    public ChangePasswordException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ChangePasswordException(Throwable cause) {
        super(cause);
    }
    
    public ChangePasswordException(String errorCode, String message) {
      super(message);
      this._errorCode = errorCode;
    }
    
    public String getErrorCode() {
      return this._errorCode != null && !this._errorCode.isEmpty() ? this._errorCode : "0203";
    }
}
