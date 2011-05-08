package edu.ucla.cens.mobilize.client.utils;

import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.AwConstants;

public class AwUrlBasedResourceUtils {
  public static String getImageUrl(String campaignId, String imageId) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("image_id", imageId);
    params.put("campaign_id", campaignId);
    String paramString = MapUtils.translateToParameters(params);
    return AwConstants.getImageWrapperUrl() + paramString; 
  }
}
