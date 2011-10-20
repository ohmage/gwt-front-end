package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

// {"first_name":"firstName","organization":"organization","json_data":{},"last_name":"lastName","personal_id":"123456789","email_address":"test@example.com"}

/**
 * Personal information about a user, used by teachers to keep track of 
 * names, contact info, etc for their class members.
 * 
 * @author shlurbee
 *
 */
public class UserAwData extends JavaScriptObject {
  protected UserAwData() {}

  public final native String getFirstName() /*-{ return this.first_name; }-*/;
  public final native String getLastName() /*-{ return this.last_name; }-*/;
  public final native String getPersonalId() /*-{ return this.personal_id; }-*/;
  public final native String getOrganization() /*-{ return this.organization; }-*/;
  public final native String getEmail() /*-{ return this.email_address; }-*/;
  
  //public static native JavaScriptObject getJsonData() /*-{ return this.json_data }-*/;
  
}
