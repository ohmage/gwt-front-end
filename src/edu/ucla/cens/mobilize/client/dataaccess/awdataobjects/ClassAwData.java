package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/*
 * {"users":{"user.adv.pa":"restricted","user.adv.su":"privileged"},"name":"BH_HS_CS102_Spring_2011"}
 */

public class ClassAwData extends JavaScriptObject {
  protected ClassAwData() {};
  
  public final native String getName() /*-{ return this.name; }-*/;
  
  public final native JsArrayString getPrivilegedUsers() /*-{
    var users = [];
    for (var username in this.users) {
      if (this.users[username] == "privileged") users.push(username);
    }
    return users;
  }-*/;
  
  public final native JsArrayString getRestrictedUsers() /*-{
    var users = [];
    for (var username in this.users) {
      if (this.users[username] == "restricted") users.push(username);
    }
    return users;
  }-*/;
  
  // Create a ClassAwData from a JSON string
  public static native ClassAwData fromJsonString(String jsonString) /*-{
      return eval('(' + jsonString + ')'); 
  }-*/;
  
}
