package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;


import com.google.gwt.core.client.JavaScriptObject;

public class WhoAmIAwData extends JavaScriptObject {
	protected WhoAmIAwData() {}

	public final native String getUsername() /*-{
		return this.username;
	}-*/;

	public static native WhoAmIAwData fromJsonString(String jsonString) /*-{
		return eval('(' + jsonString + ')'); 
	}-*/;
}
