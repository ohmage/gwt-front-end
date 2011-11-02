package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;

public class ClassSearchInfo {
  private String classUrn;
  private String className;
  private String description;
  private List<String> members = new ArrayList<String>();
  private List<String> campaigns = new ArrayList<String>();
  
  public String getClassUrn() {
    return classUrn;
  }
  public void setClassUrn(String classUrn) {
    this.classUrn = classUrn;
  }
  public String getClassName() {
    return className;
  }
  public void setClassName(String className) {
    this.className = className;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public List<String> getMembers() {
    return members;
  }
  public void addMember(String username) {
    this.members.add(username);
  }
  public void clearMembers() {
    this.members.clear();
  }
  public List<String> getCampaigns() {
    return campaigns;
  }
  public void addCampaign(String campaignUrn) {
    this.campaigns.add(campaignUrn);
  }
  public void clearCampaigns() {
    this.campaigns.clear();
  }

}
