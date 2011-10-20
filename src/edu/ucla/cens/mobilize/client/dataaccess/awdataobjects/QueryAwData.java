package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * All AndWellness query calls will return JSON with a "result" key in the
 * base object.  
 * 
 * @author jhicks
 *
 */
public abstract class QueryAwData extends JavaScriptObject {
    // Overlay types always have protected, zero-arg ctors
    protected QueryAwData() {};
    
    // All responses have a result, grab it here
    public final native String getResult() /*-{ return this.result; }-*/;
    
    public final boolean wasSuccess() {
      return "success".equals(getResult());
    }
    
    public final boolean wasError() {
      return "failure".equals(getResult());
    }
    
    public static native QueryAwData fromJsonString(String jsonString) /*-{
        return eval('(' + jsonString + ')'); 
    }-*/;
    
}
