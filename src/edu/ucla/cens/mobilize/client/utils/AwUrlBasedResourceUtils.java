package edu.ucla.cens.mobilize.client.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Cookies;

import edu.ucla.cens.mobilize.client.AwConstants;

public class AwUrlBasedResourceUtils {
  public enum ImageSize { ORIGINAL, SMALL }
  
  /**
   * @param imageId String image UUID
   * @param imageOwnerId String userName of the user that uploaded the image
   * @param campaignUrn String Urn of the campaign the image was uploaded to
   * @return Image url
   */
  public static String getImageUrl(String imageId, 
                                   String imageOwnerId, 
                                   String campaignUrn,
                                   ImageSize size) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("id", imageId);
    params.put("campaign_urn", campaignUrn);
    params.put("auth_token", Cookies.getCookie(AwConstants.cookieAuthToken));
    params.put("owner", imageOwnerId);
    params.put("client", AwConstants.apiClientString);
    if (size != null && size.equals(ImageSize.SMALL)) params.put("size", "small");
    String paramString = MapUtils.translateToParameters(params);
    return AwConstants.getImageReadUrl() + "?" + paramString;
  }
  
  /**
   * @param documentId UUID
   * @return document Url
   */
  public static String getDocumentUrl(String documentId) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", Cookies.getCookie(AwConstants.cookieAuthToken));
    params.put("client", AwConstants.apiClientString);
    params.put("document_id", documentId);
    String paramString = MapUtils.translateToParameters(params);
    return AwConstants.getDocumentDownloadUrl() + "?" + paramString;
  }
}
