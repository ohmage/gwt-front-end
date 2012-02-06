package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class UserUpdateParams implements RequestParams {
  public String username;
  public String authToken;
  public String client;
  public String firstName_opt;
  public String lastName_opt;
  public String organization_opt;
  public String personalId_opt;
  public String email_opt;
  public Boolean enabled_opt;
  public Boolean canCreateCampaigns_opt;
  public Boolean isAdmin_opt;

  public String toString() {
    assert username != null && !username.isEmpty() : "username is required";
    assert authToken != null && !authToken.isEmpty() : "authToken is required";
    assert client != null && !client.isEmpty() : "client is required";
    
    Map<String, String> params = new HashMap<String, String>();
    params.put("username", username);
    params.put("auth_token", authToken);
    params.put("client", client);
    if (isNotEmpty(firstName_opt)) {
      params.put("first_name", firstName_opt);
    }
    if (isNotEmpty(lastName_opt)) {
      params.put("last_name", lastName_opt);
    }
    if (isNotEmpty(organization_opt)) {
      params.put("organization", organization_opt);
    }
    if (isNotEmpty(personalId_opt)) {
      params.put("personal_id", personalId_opt);
    }
    if (isNotEmpty(email_opt)) {
      params.put("email_address", email_opt);
    }
    if (enabled_opt != null) {
      params.put("enabled", enabled_opt ? "true" : "false");
    }
    if (canCreateCampaigns_opt != null) {
      params.put("campaign_creation_privilege", canCreateCampaigns_opt ? "true" : "false");
    }
    if (isAdmin_opt != null) {
      params.put("admin", isAdmin_opt ? "true" : "false");
    }
    return MapUtils.translateToParameters(params);
  }
  
  private boolean isNotEmpty(String str) {
    return str != null && !str.isEmpty();
  }
}
