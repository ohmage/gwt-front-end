package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class ClassSearchAwData extends JavaScriptObject {
  protected ClassSearchAwData() {};
  public final native String getName() /*-{ return this.name; }-*/;
  public final native String getDescription() /*-{ return this.description; }-*/;
  
  public final native JsArrayString getUserNames() /*-{
    return this.usernames;
  }-*/;

  public final native JsArrayString getCampaignUrns() /*-{
    return this.campaigns;
  }-*/;
  
}
