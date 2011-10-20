package edu.ucla.cens.mobilize.client.exceptions;

public class ServerUnavailableException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public ServerUnavailableException(String serverUrl) {
    super("Could not contact server at: " + serverUrl);
  }
  
  
}
