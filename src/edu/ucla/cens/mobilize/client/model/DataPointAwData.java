package edu.ucla.cens.mobilize.client.model;

import com.google.gwt.core.client.JavaScriptObject;

import edu.ucla.cens.mobilize.client.common.Privacy;

/**
 * A single data point returned from the AndWellness data_point query API.
 * 
 * @author jhicks
 *
 */
public class DataPointAwData extends JavaScriptObject {
    protected DataPointAwData() {};
    
    // FIXME: use real data
    private static int counter=0;
    public final String getSurveyId() {
      // NOTE: surveyIds was static class member but caused exception when running compiled version
      String[] surveyIds = {"diet", "excerciseAndActivity", "foodButton"};
      int num = counter++ % 3;
      return surveyIds[num];
    }
    
    // TODO: verify these are correct json string values
    public final Privacy getPrivacyState() {
      String privacyString = this.getPrivacyStateString();
      // FIXME: deleteme
      privacyString = "private";
      Privacy privacy = Privacy.UNDEFINED;
      if ("public".equals(privacyString)) {
        privacy = Privacy.PUBLIC;
      } else if ("private".equals(privacyString)) {
        privacy = Privacy.PRIVATE;
      } else if ("invisible".equals(privacyString)) {
        privacy = Privacy.INVISIBLE;
      } else {
        // TODO: log error
      }
      return privacy;
    }
    
    public final String getPromptId() { return getLabel(); } // label == promptId
    
    // TODO: make sure privacy was added to server json 
    public final native String getPrivacyStateString() /*-{ return this.privacy; }-*/; 
    
    public final native String getLabel() /*-{ return this.label; }-*/;
    public final native String getValue() /*-{ return this.value.toString(); }-*/;
    public final native String getUnit() /*-{ return this.unit; }-*/;
    public final native String getTimeStamp() /*-{ return this.timestamp; }-*/;
    public final native String getTz() /*-{ return this.tz; }-*/;
    public final native LocationAwData getLocation() /*-{ return this.location; }-*/;
    public final native String getText() /*-{ return this.text; }-*/;
    public final native String getImage() /*-{ return this.image; }-*/;
    public final native String getType() /*-{ return this.type; }-*/;
}
