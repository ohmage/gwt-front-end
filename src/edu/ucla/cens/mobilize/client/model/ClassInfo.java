package edu.ucla.cens.mobilize.client.model;

import java.util.HashMap;
import java.util.Map;

public class ClassInfo {
  private String classId;
  private String className; // e.g., CS101
  private String description;
  private Map<String, String> supervisorIdToNameMap;
  private Map<String, String> memberIdToNameMap;
  
  public ClassInfo() {
    classId = className = description = "";
    supervisorIdToNameMap = new HashMap<String, String>();
    memberIdToNameMap = new HashMap<String, String>();
  }
  
  public String getClassId() { return this.classId; }
  public String getClassName() { return this.className; }
  public String getDescription() { return this.description; }
  
  /**
   * @return Map of supervisor ids to supervisor names
   */
  public Map<String, String> getSupervisors() { return this.supervisorIdToNameMap; }
  
  /**
   * @return Map of member ids to member names
   */
  public Map<String, String> getMembers() { return this.memberIdToNameMap; }
  
  public void setClassId(String classId) { this.classId = classId; }
  
  public void setClassName(String className) { this.className = className; }
  
  public void clearSupervisors() { this.supervisorIdToNameMap.clear(); }
  
  public void clearMembers() { this.memberIdToNameMap.clear(); }
  
  public void addSupervisor(String supervisorId, String supervisorName) {
    this.supervisorIdToNameMap.put(supervisorId, supervisorName);
  }
  
  public void addMember(String memberId, String memberName) {
    this.memberIdToNameMap.put(memberId, memberName);
  }
}
