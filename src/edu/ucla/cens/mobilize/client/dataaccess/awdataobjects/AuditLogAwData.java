package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

public class AuditLogAwData extends JavaScriptObject {
  protected AuditLogAwData() {}
  
  public final native String getTimestamp() /*-{ return this.timestamp; }-*/;
  public final native String getResponseStatus() /*-{ return this.response.result;}-*/;
  public final native String getClient() /*-{ return this.client;}-*/;
  public final native String getRequestType() /*-{ return this.request_type; }-*/;
  public final native double getRespondedMillis() /*-{ return this.responded_millis; }-*/;
  public final native double getReceivedMillis() /*-{ return this.received_millis;}-*/;
  public final native String getUri() /*-{ return this.uri; }-*/;
  
  // FIXME: these two ok?
  public final native JavaScriptObject getRequestParams() /*-{ return this.request_parameters;}-*/;
  public final native JavaScriptObject getExtraData() /*-{ return this.extra_data;}-*/;
  
}

