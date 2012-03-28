package edu.ucla.cens.mobilize.client.utils;

/**
 * Various String input validators.
 * 
 * @author ewang9
 */
public class InputValidationUtils {
	public native static boolean isValidEmail(String email) /*-{
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		return re.test(email);
	}-*/;
}
