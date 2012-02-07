package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class ClassSearchParams implements RequestParams {
  public String client;
  public String authToken;
  public String className_opt;
  public String classUrn_opt;
  public String description_opt;
  
  public String toString() {
    assert client != null : "client is required";
    assert authToken != null : "authToken is required";
    Map<String, String> params = new HashMap<String, String>();
    params.put("client", client);
    params.put("auth_token", authToken);
    if (className_opt != null && !className_opt.isEmpty()) {
      params.put("class_name", className_opt);
    }
    if (classUrn_opt != null && !classUrn_opt.isEmpty()) {
      params.put("class_urn", classUrn_opt);
    }
    if (description_opt != null && !description_opt.isEmpty()) {
      params.put("description", description_opt);
    }
    return MapUtils.translateToParameters(params);
  }
}
