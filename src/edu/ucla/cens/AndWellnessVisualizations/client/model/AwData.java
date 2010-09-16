package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * The base AwData object that all data from the AndWellness server must match.
 * All data from the server should be parsed into JavaScriptObjects that extend
 * from this abstract class.
 * 
 * @author jhicks
 *
 */
public abstract class AwData extends JavaScriptObject {
    // Overlay types always have protected, zero-arg ctors
    protected AwData() {};
    
    // All responses have a result, grab it here
    public final native String getResult() /*-{ return this.result; }-*/;
}
