package edu.ucla.cens.mobilize.client.dataaccess.awdataobjects;


import com.google.gwt.core.client.JsArray;

/**
 * Class for accessing the results of a user info query.
 * 
 * Expects json like:
 * [_users={"temp.user":{"classes":["andwellness"],"roles":["supervisor"],"permissions":{"cancreate":true}}}]
 * 
 * @author vhajdik
 *
 */
public class UserInfoQueryAwData extends QueryAwData {
  protected UserInfoQueryAwData() {};
  
  // FIXME: TEST
  public final native JsArray<UserInfoAwData> getUserInfoAwDataArray()  /*-{
    var arrayToReturn = [];
    var keys = this.data.keys();
    for (var key in keys) {
      this.data[key].username = key;
      arrayToReturn.push(this.data[key]);
    }
    return arrayToReturn; 
  }-*/;
  
  public static native UserInfoQueryAwData fromJsonString(String jsonString) /*-{
      return eval('(' + jsonString + ')'); 
  }-*/;

  
  
}
