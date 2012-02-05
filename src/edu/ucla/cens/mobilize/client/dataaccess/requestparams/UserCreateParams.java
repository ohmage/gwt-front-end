package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class UserCreateParams implements RequestParams {
  public String authToken;
  public String client;
  public String username;
  public String password;
  public Boolean admin = false;
  public Boolean enabled = true;
  public Boolean newAccount_opt;
  public Boolean canCreateCampaigns_opt;

  
  public String toString() {
    assert authToken != null && !authToken.isEmpty() : "authToken is required";
    assert client != null && !client.isEmpty() : "client is required";
    assert username != null && !username.isEmpty() : "username is required";
    assert password != null && !password.isEmpty() : "password is required";
    
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", authToken);
    params.put("client", client);
    params.put("username", username);
    params.put("password", password);
    params.put("admin", admin ? "true" : "false");
    params.put("enabled", enabled ? "true" : "false");
    
    if (newAccount_opt != null) params.put("new_account", newAccount_opt ? "true" : "false");
    if (canCreateCampaigns_opt != null) {
      params.put("campaign_creation_privilege", canCreateCampaigns_opt ? "true" : "false");
    }

    return MapUtils.translateToParameters(params);
  }
}
