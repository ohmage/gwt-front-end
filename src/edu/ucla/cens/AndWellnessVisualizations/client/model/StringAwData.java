package edu.ucla.cens.AndWellnessVisualizations.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Used to parse out a String from a JsArray.
 * 
 * @author jhicks
 *
 */
public class StringAwData extends JavaScriptObject {
    protected StringAwData() {};
    
    public final String getString() {
        return this.toString();
    }
}
