package edu.ucla.cens.mobilize.client;
import com.google.gwt.http.client.RequestBuilder;

public class MockRequest extends RequestBuilder {
  public MockRequest() {
    super("POST", "fakeurl.com");
  }
  
  protected MockRequest(String httpMethod, String url) {
    super(httpMethod, url);
  }
}
