package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

public abstract class RequestParams {
  public String toString() {
    throw new RuntimeException("toString() not overridden in subclass of RequestParams.");
  }
}
