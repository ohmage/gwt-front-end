package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class UserSearchParams implements RequestParams {
  public String client;
  public String authToken;
  // all fields are optional
  public String username_opt;
  public Boolean admin_opt;
  public Boolean enabled_opt;
  public Boolean newAccount_opt;
  public Boolean canCreateCampaigns_opt;
  public String firstName_opt;
  public String lastName_opt;
  public String organization_opt;
  public String personalId_opt;
  public String email_opt;
  public Integer startIndex_opt;
  public Integer pageSize_opt;
  
  public String toString() {
    assert client != null : "client is required";
    assert authToken != null : "authToken is required";
    
    Map<String, String> params = new HashMap<String, String>();
    params.put("client", client);
    params.put("auth_token", authToken);
    if (username_opt != null && !username_opt.isEmpty()) {
      params.put("username", username_opt);
    }
    if (admin_opt != null) {
      params.put("admin", admin_opt ? "true" : "false");
    }
    if (enabled_opt != null) {
      params.put("enabled", enabled_opt ? "true" : "false");
    }
    if (newAccount_opt != null) {
      params.put("new_account", newAccount_opt ? "true" : "false");
    }
    if (canCreateCampaigns_opt != null) {
      params.put("campaign_creation_privilege", canCreateCampaigns_opt ? "true" : "false");
    }
    if (firstName_opt != null && !firstName_opt.isEmpty()) {
      params.put("first_name", firstName_opt);
    }
    if (lastName_opt !=null && !lastName_opt.isEmpty()) {
      params.put("last_name", lastName_opt);
    }
    if (organization_opt != null && !organization_opt.isEmpty()) {
      params.put("organization", organization_opt);
    }
    if (personalId_opt != null && !personalId_opt.isEmpty()) {
      params.put("personal_id", personalId_opt);
    }
    if (email_opt != null && !email_opt.isEmpty()) {
      params.put("email_address", email_opt);
    }
    if (startIndex_opt != null) {
      params.put("num_to_skip", Integer.toString(startIndex_opt));
    }
    if (pageSize_opt != null) {
      params.put("num_to_return", Integer.toString(pageSize_opt));
    }
    return MapUtils.translateToParameters(params);
  }
}
