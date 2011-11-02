package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ucla.cens.mobilize.client.common.RoleClass;

public class ClassInfo {
  private String classId;
  private String className; // e.g., CS101
  private String description;
  private Map<String, RoleClass> memberNameToRoleMap = new HashMap<String, RoleClass>();
  
  public ClassInfo() {
    classId = className = description = "";
  }
  
  public String getClassId() { return this.classId; }
  public String getClassName() { return this.className; }
  public String getDescription() { return this.description; }

  public List<String> getMemberLogins() {
    return new ArrayList<String>(this.memberNameToRoleMap.keySet());
  }
  
  public RoleClass getMemberRole(String memberLogin) {
    return this.memberNameToRoleMap.get(memberLogin);
  }
  
  public Map<String, RoleClass> getUsernameToRoleMap() {
    return new HashMap<String, RoleClass>(this.memberNameToRoleMap);
  }
  
  public boolean userIsPrivileged(String memberLogin) {
    return this.memberNameToRoleMap.containsKey(memberLogin) &&
           this.memberNameToRoleMap.get(memberLogin).equals(RoleClass.PRIVILEGED);
  }

  public void clearMembers() {
    this.memberNameToRoleMap.clear();
  }
  
  public void addMember(String userName, RoleClass role) {
    this.memberNameToRoleMap.put(userName, role);
  }
  
  public void setClassId(String classId) { this.classId = classId; }
  
  public void setClassName(String className) { this.className = className; }

  public void setDescription(String description) { this.description = description; }
  
  public boolean userCanEdit(String userName) {
    return RoleClass.PRIVILEGED.equals(getMemberRole(userName));
  }
}
