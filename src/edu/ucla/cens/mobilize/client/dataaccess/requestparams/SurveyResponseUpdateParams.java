package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.utils.MapUtils;


public class SurveyResponseUpdateParams extends RequestParams {
  public String authToken;
  public String campaignUrn;
  public int surveyKey = -1;
  public String client;
  public Privacy privacy;
  
  public String toString() {
    assert authToken != null : "authToken is required";
    assert campaignUrn != null : "campaignUrn is required";
    assert surveyKey != -1 : "surveyKey is required";
    assert client != null : "client is required";
    assert privacy != null : "privacy is required";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", authToken);
    params.put("campaign_urn", campaignUrn);
    params.put("survey_key", Integer.toString(surveyKey));
    params.put("privacy_state", privacy.toServerString());
    params.put("client", client);
    return MapUtils.translateToParameters(params);
  }
}
