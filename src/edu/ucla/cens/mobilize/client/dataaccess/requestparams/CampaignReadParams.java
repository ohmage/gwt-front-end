package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class CampaignReadParams implements RequestParams {
  public String authToken;
  public String client;
  public OutputFormat outputFormat;
  public List<String> campaignUrns_opt = new ArrayList<String>();
  public Date startDate_opt; 
  public Date endDate_opt; 
  public Privacy privacyState_opt;
  public RunningState runningState_opt; 
  public RoleCampaign userRole_opt;
  public List<String> classUrns_opt = new ArrayList<String>();

  public enum OutputFormat { SHORT, LONG }
  
  public String toString() {
    // required params
    assert authToken != null : "authToken is required";
    assert client != null : "client is required";
    assert outputFormat != null : "outputFormat is required";
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("auth_token", authToken);
    params.put("client", client);
    params.put("output_format", outputFormat.toString().toLowerCase());
    // optional params
    if (campaignUrns_opt != null && !campaignUrns_opt.isEmpty()) {
      params.put("campaign_urn_list", CollectionUtils.join(campaignUrns_opt, ","));
    }
    if (startDate_opt != null) {
      params.put("start_date", DateUtils.translateToApiRequestFormat(startDate_opt));
    }
    if (endDate_opt != null) {
      params.put("end_date", DateUtils.translateToApiRequestFormat(endDate_opt));
    }
    if (privacyState_opt != null) {
      params.put("privacy_state", privacyState_opt.toString().toLowerCase());
    }
    if (runningState_opt != null) {
      params.put("running_state", runningState_opt.toString().toLowerCase());
    }
    if (userRole_opt != null) {
      params.put("user_role", userRole_opt.toString().toLowerCase());
    }
    if (classUrns_opt != null && !classUrns_opt.isEmpty()) {
      params.put("class_urn_list", CollectionUtils.join(classUrns_opt, ","));
    }
    return MapUtils.translateToParameters(params);
  }
  
}
