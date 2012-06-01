package org.ohmage.mobilize.client.dataaccess.ohdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A single error from a list of errors returned from the server.
 * 
 * @author jhicks
 *
 */
public class ErrorOhData extends JavaScriptObject {
    protected ErrorOhData() {};
    
    public final native String getCode() /*-{ return this.code; }-*/;
    public final native String getText() /*-{ return this.text; }-*/;
}
