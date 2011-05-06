package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class ClassUpdateParams {
  public String classId;
  public String authToken;
  public String description_opt; 
  public Collection<String> usersToAdd_opt = new ArrayList<String>();
  public Collection<String> usersToRemove_opt = new ArrayList<String>();
  public Collection<String> usersToAddAsPrivileged_opt = new ArrayList<String>();
  
  public String toString() {
    assert classId != null : "classId is required";
    assert authToken != null : "authToken is required";
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("class_urn", this.classId);
    if (this.description_opt != null) {
      params.put("description", this.description_opt);
    }
    if (this.usersToAdd_opt != null && !this.usersToAdd_opt.isEmpty()) {
      params.put("user_list_add", CollectionUtils.join(this.usersToAdd_opt, ","));
    }
    if (this.usersToRemove_opt != null && !this.usersToRemove_opt.isEmpty()) {
      params.put("user_list_remove", CollectionUtils.join(this.usersToRemove_opt, ","));
    }
    if (this.usersToAddAsPrivileged_opt != null && !this.usersToAddAsPrivileged_opt.isEmpty()) {
      params.put("privileged_user_list_add", 
                 CollectionUtils.join(this.usersToAddAsPrivileged_opt, ","));
    }
    return MapUtils.translateToParameters(params);
  }
}
