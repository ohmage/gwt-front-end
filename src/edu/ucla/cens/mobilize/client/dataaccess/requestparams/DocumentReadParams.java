package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class DocumentReadParams extends RequestParams {
  public String authToken;
  public boolean includePersonalDocuments = true;
  public List<String> campaignUrnList = new ArrayList<String>();
  public List<String> classUrnList = new ArrayList<String>();
  
  @Override
  public String toString() {
    assert this.authToken != null : "authToken is required";
    //assert (this.campaignUrnList != null && !this.campaignUrnList.isEmpty()) || 
    //       (this.classUrnList != null && !this.classUrnList.isEmpty()) : 
    //         "at least one of campaignUrnList and classUrnList must be non-empty";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("personal_documents", this.includePersonalDocuments ? "true" : "false");
    if (campaignUrnList != null && !campaignUrnList.isEmpty()) {
      params.put("campaign_urn_list", CollectionUtils.join(this.campaignUrnList, ","));
    }
    if (classUrnList != null && !classUrnList.isEmpty()) {
      params.put("class_urn_list", CollectionUtils.join(this.classUrnList, ","));
    }
    return MapUtils.translateToParameters(params);
  }
}
