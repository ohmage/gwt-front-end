package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONObject;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import edu.ucla.cens.mobilize.client.common.UserRole;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.UserInfoAwData;
import edu.ucla.cens.mobilize.client.model.UserInfo;

public class AwDataJsonTranslators {
  private static Logger _logger = Logger.getLogger(AwDataJsonTranslators.class.getName());

  public static List<UserInfo> userReadQueryToUserInfoList(String json) {
    List<UserInfo> users = new ArrayList<UserInfo>();
    JSONValue jsonValue = JSONParser.parse(json);
    JSONObject userNameToDataHash = jsonValue.isObject();
    if (userNameToDataHash != null) {
      Set<String> userNames = userNameToDataHash.keySet();
      for (String userName : userNames) {
        try {
          jsonValue = userNameToDataHash.get(userName);
          JSONObject userJSONObject = jsonValue.isObject();
          UserInfoAwData userDataJSObject = (UserInfoAwData)userJSONObject.getJavaScriptObject();
          boolean canCreateFlag = userDataJSObject.getCanCreateFlag();
          List<String> classes = userDataJSObject.getClasses();
          List<String> rolesAsStrings = userDataJSObject.getRoles();
          List<UserRole> roles = new ArrayList<UserRole>();
          for (String roleString : rolesAsStrings) {
            roles.add(UserRole.valueOf(roleString));
            // FIXME: make sure string is valid
          }
          UserInfo userInfo = new UserInfo(userName, canCreateFlag, classes, roles);
          users.add(userInfo);
        } catch (Exception e) { // FIXME: which exceptions?
          String msg = "Could not parse json for user: " + userName + ". Skipping record. " +
            "Json was: \n" + jsonValue.toString() + ".\nException msg was: \n" + e.getMessage();
          _logger.warning(msg);
        }
      }
    }
    return users;
  }
  
}
