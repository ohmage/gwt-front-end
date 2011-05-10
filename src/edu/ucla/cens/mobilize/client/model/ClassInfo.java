package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassInfo {
  private String classId;
  private String className; // e.g., CS101
  private String description;
  private List<String> privilegedMemberLogins;
  private List<String> memberLogins;
  
  public ClassInfo() {
    classId = className = description = "";
    privilegedMemberLogins = new ArrayList<String>();
    memberLogins = new ArrayList<String>();
  }
  
  public String getClassId() { return this.classId; }
  public String getClassName() { return this.className; }
  public String getDescription() { return this.description; }

  public List<String> getMemberLogins() {
    return this.memberLogins;
  }
  
  public List<String> getPrivilegedMemberLogins() {
    return this.privilegedMemberLogins;
  }
  
  public void setClassId(String classId) { this.classId = classId; }
  
  public void setClassName(String className) { this.className = className; }
  
  public void clearPrivilegedMembers() { this.privilegedMemberLogins.clear(); }
  
  public void clearMembers() { this.memberLogins.clear(); }
  
  public void addPrivilegedMember(String userLogin) {
    this.privilegedMemberLogins.add(userLogin);
  }
  
  public void addMember(String userLogin) {
    this.memberLogins.add(userLogin);
  }
}
