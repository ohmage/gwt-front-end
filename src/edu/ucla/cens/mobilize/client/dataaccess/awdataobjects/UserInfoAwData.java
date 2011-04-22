package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class UserInfoAwData extends JavaScriptObject {
  protected UserInfoAwData() {};
  
  public static native UserInfoAwData fromJsonString(String jsonString) /*-{
      return eval('(' + jsonString + ')'); 
  }-*/;

  private final native JsArrayString getClassesAsJsArray() /*-{ return this.classes; }-*/;
  
  private final native JsArrayString getRolesAsJsArray() /*-{ return this.roles;}-*/;
    
  public final native boolean getCanCreateFlag() /*-{ 
    return (this.permissions != undefined) ? this.permissions.cancreate : false; 
  }-*/;
  
  public final List<String> getClasses() {
    JsArrayString toTranslate = getClassesAsJsArray();
    List<String> toReturn = new ArrayList<String>();
    
    // Translate the array one by one
    for (int i = 0; i < toTranslate.length(); ++i) {
        String classId = toTranslate.get(i);
        toReturn.add(classId);
    }
    
    return toReturn;
  }
  
  public final List<String> getRoles() {
    JsArrayString toTranslate = getRolesAsJsArray();
    List<String> toReturn = new ArrayList<String>();
    
    // Translate the array one by one
    for (int i = 0; i < toTranslate.length(); ++i) {
        String role = toTranslate.get(i);
        toReturn.add(role);
    }
    
    return toReturn;
  }
}