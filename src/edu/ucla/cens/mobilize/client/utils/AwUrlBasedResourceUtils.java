package edu.ucla.cens.mobilize.client.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Cookies;

import edu.ucla.cens.mobilize.client.AwConstants;

public class AwUrlBasedResourceUtils {  
  public static String getImageUrl(String imageId, String campaignUrn) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("id", imageId);
    params.put("campaign_urn", campaignUrn);
    params.put("auth_token", Cookies.getCookie(AwConstants.cookieAuthToken));
    params.put("user", Cookies.getCookie(AwConstants.cookieUserName));
    params.put("client", AwConstants.apiClientString);
    String paramString = MapUtils.translateToParameters(params);
    return AwConstants.getImageReadUrl() + "?" + paramString;
  }
}
