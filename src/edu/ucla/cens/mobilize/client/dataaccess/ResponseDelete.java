package edu.ucla.cens.mobilize.client.dataaccess;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class ResponseDelete /*extends JavaScriptObject*/ { 
  protected boolean wasSuccessful;
  protected int errorCode;
  protected String msg;
  
  public String getMsg() {
    return this.msg;
  }
  
  public int getErrorCode() {
    return this.errorCode;
  }
  
  public boolean wasSuccessful() {
    return this.wasSuccessful;
  }
  
  // FIXME: overlay obj instead
  public static ResponseDelete fromJson(String json) {

    ResponseDelete retval = null;
    JSONValue value = JSONParser.parseStrict(json);
    JSONObject obj = value.isObject();
    if (obj != null) {
      retval = new ResponseDelete();
      if (obj.containsKey("result")) {
        JSONValue resultVal = obj.get("result");
        JSONString resultString = resultVal.isString();
        retval.wasSuccessful = resultString != null && resultString.toString().equals("\"success\"");
      } else {
        retval.wasSuccessful = false;
      }
      if (!retval.wasSuccessful) {
        if (obj.containsKey("error")) {
          JSONNumber errCode = obj.get("error").isNumber();
          if (errCode != null) {
            retval.errorCode = (int) errCode.getValue();
          }
        }
        if (obj.containsKey("msg")) { // FIXME: what should key be here?
          JSONString msg = obj.get("msg").isString(); 
          if (msg != null) {
            retval.msg = msg.stringValue();
          }
        }
      }
    }

    return retval;
  }
  
}
