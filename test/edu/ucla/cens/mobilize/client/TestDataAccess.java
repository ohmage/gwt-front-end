package edu.ucla.cens.mobilize.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import edu.ucla.cens.mobilize.client.dataaccess.AndWellnessDataService;
import edu.ucla.cens.mobilize.client.exceptions.ServerException;

public class TestDataAccess extends GWTTestCase {

  // Wrap data obj so protected methods can be tested.  
  class TestableDataService extends AndWellnessDataService {
    private String testGetResponseText(RequestBuilder request, Response response) throws Exception {
      return super.getResponseTextOrThrowException(request, response);
    }
  }

  @Override
  public String getModuleName() {
    return "edu.ucla.cens.mobilize.MobilizeWebDebug";
  }
  
  public void testGetResponseText() {
    TestableDataService dataService = new TestableDataService();
    RequestBuilder request = new MockRequest();
    MockResponse response = new MockResponse();
    response.setGoodStatusCode();
    String goodJSON = "{\"result\":\"success\",\"token\":\"f11f0490-04b1-4ab4-b2f2-5094b4675a4b\"}";
    response.setText(goodJSON);
    
    // test text successfully extracted from good response
    try {
      String text = dataService.testGetResponseText(request, (Response)response);
      assertEquals("Didn't extract text from good response", goodJSON, text); 
    } catch (Exception e) {
      fail("exception thrown for good response");
    }

    // test ServerException is thrown for 404 not found response
    response.set404NotFoundStatusCode();
    response.setText("<html>404 not found, etc</html>");
    try {
      dataService.testGetResponseText(request, (Response)response);
      fail("No exception thrown for 404 not found response. Should have thrown ServerException.");
    } catch (ServerException e) {
      // ServerException thrown, as expected. Test passed.
    } catch (Exception e) {
      String wrongException = e.getClass().getName();
      fail("Wrong exception thrown for 404 not found. Was: " + wrongException + ". Should have been ServerException.");
    }

    // test ServerException thrown if status field in JSON is set to 
    // something other than "success" or "failure"
    response.setUnknownStatusCode();
    response.setGoodLoginResponse();
    try {
      dataService.testGetResponseText(request, (Response)response);
      fail("No exception thrown for unknown status code");
    } catch (ServerException e) {
      // yay!
    } catch (Exception e) {
      String wrongException = e.getClass().getName();
      fail("Wrong exception thrown for unknown status code. Was: " + wrongException + ". Should have been ServerException.");
    }

    // test JavaScriptException thrown when server returns malformed json
    response.setGoodStatusCode();
    response.setText("this should have been a json object but it's not");
    try {
      dataService.testGetResponseText(request, (Response)response);
      fail("No exception thrown for malformed json response");
    } catch (JavaScriptException e) {
      // good 
    } catch (Exception e) {
      String wrongException = e.getClass().getName();
      fail("Wrong exception thrown for malformed json response. Was: " + wrongException + ". Should have been JavaScriptException.");
    }
    
  }


}
