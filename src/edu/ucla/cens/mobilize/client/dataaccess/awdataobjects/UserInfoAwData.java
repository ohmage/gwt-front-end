package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  
  public final Map<String, String> getClasses() {
    Map<String, String> classNameToIdMap = new HashMap<String, String>();
    JsArrayString classKeys = getClassKeys();
    for (int i = 0; i < classKeys.length(); i++) {
      String classKey = classKeys.get(i);
      classNameToIdMap.put(classKey, getClassByKey(classKey));
    }
    return classNameToIdMap;
  }
  
  public final native JsArrayString getClassKeys() /*-{
    var keys = [];
    for (var key in this.classes) keys.push(key);
    return keys;
  }-*/;
  
  public final native String getClassByKey(String classKey) /*-{
    return this.classes[classKey];
  }-*/;
  
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