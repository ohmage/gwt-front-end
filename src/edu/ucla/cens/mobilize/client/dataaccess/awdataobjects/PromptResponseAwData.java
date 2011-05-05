package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;

import com.google.gwt.core.client.JavaScriptObject;

public class PromptResponseAwData extends JavaScriptObject {
  protected PromptResponseAwData() {};

  public final native int getSurveyResponseKey() /*-{ return this.survey_key; }-*/;
  
  public final native String getDisplayType() /*-{ return this.prompt_display_type; }-*/;
  public final native String getPromptId() /*-{ return this.prompt_id; }-*/;
  
  // response can be text, number, or uuid of an image. use prompt type to determine which
  public final native String getPromptResponse() /*-{ 
    return this.prompt_response + ""; // make sure it's a string 
  }-*/;
  public final native String getPromptType() /*-{ return this.prompt_type; }-*/;
  
  public final native String getSurveyDescription() /*-{ return this.survey_description; }-*/;
  public final native String getSurveyId() /*-{ return this.survey_id; }-*/;
  public final native String getSurveyTitle() /*-{ return this.survey_title; }-*/;
  
  // NOTE(2011/05/04): privacy state is included with each prompt_response but is
  // actually the same for all prompt responses within a single survey_response
  public final native String getPrivacy() /*-{ return this.privacy_state; }-*/;
  
  // both timestamp and timezone are needed to determine real time
  public final native String getTimestamp() /*-{ return this.timestamp; }-*/;
  public final native String getTimezone() /*-{ return this.timezone; }-*/;
  
  public final native String getUser() /*-{ return this.user; }-*/;
  
}
