package edu.ucla.cens.mobilize.client.model;

import java.util.HashMap;
import java.util.Map;

public class ClassInfo {
  private String classId;
  private String className; // e.g., CS101
  private String description;
  private Map<String, String> privilegedMemberIdToNameMap;
  private Map<String, String> memberIdToNameMap;
  
  public ClassInfo() {
    classId = className = description = "";
    privilegedMemberIdToNameMap = new HashMap<String, String>();
    memberIdToNameMap = new HashMap<String, String>();
  }
  
  public String getClassId() { return this.classId; }
  public String getClassName() { return this.className; }
  public String getDescription() { return this.description; }
  
  /**
   * @return Map of privileged member (supervisor) ids to  names
   */
  public Map<String, String> getPrivilegedMembers() { 
    return this.privilegedMemberIdToNameMap; 
  }
  
  /**
   * @return Map of member ids to member names
   */
  public Map<String, String> getMembers() { return this.memberIdToNameMap; }
  
  public void setClassId(String classId) { this.classId = classId; }
  
  public void setClassName(String className) { this.className = className; }
  
  public void clearPrivilegedMembers() { this.privilegedMemberIdToNameMap.clear(); }
  
  public void clearMembers() { this.memberIdToNameMap.clear(); }
  
  public void addPrivilegedMember(String privilegedMemberId, String privilegedMemberName) {
    this.privilegedMemberIdToNameMap.put(privilegedMemberId, privilegedMemberName);
  }
  
  public void addMember(String memberId, String memberName) {
    this.memberIdToNameMap.put(memberId, memberName);
  }
}
