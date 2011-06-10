package edu.ucla.cens.mobilize.client.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Cookies;

import edu.ucla.cens.mobilize.client.AwConstants;

public class AwUrlBasedResourceUtils {  
  /**
   * @param imageId String image UUID
   * @param imageOwnerId String userName of the user that uploaded the image
   * @param campaignUrn String Urn of the campaign the image was uploaded to
   * @return Image url
   */
  public static String getImageUrl(String imageId, String imageOwnerId, String campaignUrn) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("id", imageId);
    params.put("campaign_urn", campaignUrn);
    params.put("auth_token", Cookies.getCookie(AwConstants.cookieAuthToken));
    params.put("user", imageOwnerId);
    params.put("client", AwConstants.apiClientString);
    String paramString = MapUtils.translateToParameters(params);
    return AwConstants.getImageReadUrl() + "?" + paramString;
  }
}
