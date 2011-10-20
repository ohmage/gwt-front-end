package edu.ucla.cens.mobilize.client;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;


public class MockResponse extends Response {
  int statusCode = 200;
  String text = "";
  
  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  /************ METHODS BEING TESTED ***********/
  
  @Override
  public int getStatusCode() {
    return this.statusCode;
  }

  @Override
  public String getText() {
    return this.text;
  }

  /************ CONVENIENCE METHODS ***********/
  // Get examples of good responses by running curl examples at:
  // http://www.lecs.cs.ucla.edu/wikis/andwellness/index.php/Curl_Examples_2.2
  
  public void setGoodLoginResponse() {
    // curl -d "user=temp.user&password=temp.user&client=josh" http://dev1.andwellness.org/app/user/auth_token
    this.text = "{\"result\":\"success\",\"token\":\"f11f0490-04b1-4ab4-b2f2-5094b4675a4b\"}";
  }
  
  public void setMalformedLoginResponse() {
    this.text = "not even json";
  }
  
  public void setLoginResponseWithUnknownResult() {
    this.text = "{\"result\":\"not_success_or_failure\",\"token\":\"f11f0490-04b1-4ab4-b2f2-5094b4675a4b\"}";
  }
  
  public void setGoodStatusCode() {
    this.statusCode = 200;
  }
  
  public void setUnknownStatusCode() {
    this.statusCode = 123;
  }
  
  public void set404NotFoundStatusCode() {
    this.statusCode = 404;
  }
  
  /************************** UNUSED *********************/
  @Override
  public String getStatusText() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getHeader(String header) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Header[] getHeaders() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getHeadersAsString() {
    // TODO Auto-generated method stub
    return null;
  }

  
}
