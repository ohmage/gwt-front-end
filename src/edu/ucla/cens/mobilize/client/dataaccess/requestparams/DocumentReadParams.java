package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class DocumentReadParams implements RequestParams {
  public String authToken;
  public String client;
  public Collection<String> campaignUrnList = new ArrayList<String>();
  public Collection<String> classUrnList = new ArrayList<String>();
  
  @Override
  public String toString() {
    assert this.authToken != null : "authToken is required";
    assert this.client != null : "client is required";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("client", this.client);
    if (campaignUrnList != null && !campaignUrnList.isEmpty()) {
      params.put("campaign_urn_list", CollectionUtils.join(this.campaignUrnList, ","));
    }
    if (classUrnList != null && !classUrnList.isEmpty()) {
      params.put("class_urn_list", CollectionUtils.join(this.classUrnList, ","));
    }
    return MapUtils.translateToParameters(params);
  }
}
