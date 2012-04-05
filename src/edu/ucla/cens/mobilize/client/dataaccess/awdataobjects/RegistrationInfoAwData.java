package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class RegistrationInfoAwData extends JavaScriptObject {
	protected RegistrationInfoAwData() {}

	public final native String getRecaptchaKey() /*-{
		return this.data['recaptcha_public_key'];
	}-*/;

	public final native String getTermsOfService() /*-{
		return this.data['terms_of_service'];
	}-*/;

	public static native RegistrationInfoAwData fromJsonString(String jsonString) /*-{
		return eval('(' + jsonString + ')'); 
	}-*/;
}
