package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;

public class UserSearchInfo implements Comparable<UserSearchInfo> {
  private String username;
  private String firstName;
  private String lastName;
  private String organization;
  private String personalId;
  private String email;
  private List<String> classUrns = new ArrayList<String>();
  private List<String> campaignUrns = new ArrayList<String>();
  private boolean isAdmin;
  private boolean isNewAccount;
  private boolean isEnabled;
  private boolean canCreateCampaigns;
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getOrganization() {
    return organization;
  }
  public void setOrganization(String organization) {
    this.organization = organization;
  }
  public String getPersonalId() {
    return personalId;
  }
  public void setPersonalId(String personalId) {
    this.personalId = personalId;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public List<String> getClassUrns() {
    return classUrns;
  }
  public void addClassUrn(String urn) {
    this.classUrns.add(urn);
  }
  public void clearClasses() {
    this.classUrns.clear();
  }
  public List<String> getCampaignUrns() {
    return campaignUrns;
  }
  public void addCampaignUrn(String urn) {
    this.campaignUrns.add(urn);
  }
  public void clearCampaigns() {
    this.campaignUrns.clear();
  }
  public boolean isAdmin() {
    return isAdmin;
  }
  public void setAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }
  public boolean isNewAccount() {
    return isNewAccount;
  }
  public void setNewAccount(boolean isNewAccount) {
    this.isNewAccount = isNewAccount;
  }
  public boolean isEnabled() {
    return isEnabled;
  }
  public void setEnabled(boolean isEnabled) {
    this.isEnabled = isEnabled;
  }
  public boolean canCreateCampaigns() {
    return canCreateCampaigns;
  }
  public void setCanCreateCampaigns(boolean canCreateCampaigns) {
    this.canCreateCampaigns = canCreateCampaigns;
  }
  @Override
  public int compareTo(UserSearchInfo other) {
    return this.getUsername().compareTo(other.getUsername());
  }
  
  
}
