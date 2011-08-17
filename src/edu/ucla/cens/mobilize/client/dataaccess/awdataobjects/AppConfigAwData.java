package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

// {"result":"success","data":{"application_build":"55e80e9","application_name":"ohmage","application_version":"2.5","default_survey_response_sharing_state":"private"}}
public class AppConfigAwData extends JavaScriptObject {
  protected AppConfigAwData() {}
  
  public final native String getApplicationName() /*-{
    return this.data['application_name'];
  }-*/;
  
  /*
  private final native JsArrayString getPrivacyStateStrings() /*-{
    return this.data.privacy_states;
  }-;
  */
  
  public static native AppConfigAwData fromJsonString(String jsonString) /*-{
    return eval('(' + jsonString + ')'); 
  }-*/;
}
