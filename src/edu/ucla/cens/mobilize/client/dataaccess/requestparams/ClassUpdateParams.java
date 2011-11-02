package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class ClassUpdateParams {
  public String classId;
  public String authToken;
  public String client;
  public String className;
  public String description_opt; 
  public Map<String, RoleClass> usersToAdd_opt = new HashMap<String, RoleClass>();
  public Collection<String> usersToRemove_opt = new ArrayList<String>();
  
  public String toString() {
    assert classId != null : "classId is required";
    assert authToken != null : "authToken is required";
    assert client != null : "client is required";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("class_urn", this.classId);
    params.put("client", this.client);
    if (this.className != null) {
      params.put("class_name", this.className);
    }
    if (this.description_opt != null) {
      params.put("description", this.description_opt);
    }
    if (this.usersToAdd_opt != null && !this.usersToAdd_opt.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (String username : this.usersToAdd_opt.keySet()) {
        if (first) first = false; else sb.append(",");
        sb.append(username).append(";").append(this.usersToAdd_opt.get(username).toServerString());
      }
      params.put("user_role_list_add", sb.toString());
    }
    if (this.usersToRemove_opt != null && !this.usersToRemove_opt.isEmpty()) {
      params.put("user_list_remove", CollectionUtils.join(this.usersToRemove_opt, ","));
    }
    return MapUtils.translateToParameters(params);
  }
}
