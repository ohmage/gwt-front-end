package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.Privacy;

public class DocumentInfo {

  int UUID;
  String documentName;
  String creator;
  Date creationTimestamp;
  Date lastModifiedTimestamp;
  List<String> authors = new ArrayList<String>();
  List<String> campaigns = new ArrayList<String>();
  List<String> classes = new ArrayList<String>();
  String description;
  Privacy privacy;
  float size;
  
  boolean userCanEdit = false;
  
  public void setEditPermission(boolean userCanEdit) {
    this.userCanEdit = userCanEdit;
  }
  
  public boolean userCanEdit () {
    return this.userCanEdit;
  }
  
  public int getDocumentId() {
    return this.UUID;
  }
  
  public void setDocumentId(int UUID) {
    this.UUID = UUID;
  }
  
  public String getDocumentName() {
    return documentName;
  }
  
  public void setDocumentName(String documentName) {
    this.documentName = documentName;
  }
  
  public Date getCreationTimestamp() {
    return creationTimestamp;
  }
  
  public void setCreationTimestamp(Date creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }
  
  public Date getLastModifiedTimestamp() {
    return lastModifiedTimestamp;
  }
  
  public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
    this.lastModifiedTimestamp = lastModifiedTimestamp;
  }

  public List<String> getCampaigns() {
    return campaigns;
  }
  
  public void addCampaign(String campaignUrn) {
    if (!this.campaigns.contains(campaignUrn)) {
      this.campaigns.add(campaignUrn);
    }
  }

  public void clearCampaigns() {
    this.campaigns.clear();
  }

  public List<String> getClasses() {
    return this.classes;
  }
  
  public void addClass(String classUrn) {
    if (!this.classes.contains(classUrn)) {
      this.classes.add(classUrn);
    }
  }
  
  public void clearClasses() {
    this.classes.clear();
  }

  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public Privacy getPrivacy() {
    return privacy;
  }
  
  public void setPrivacy(Privacy privacy) {
    this.privacy = privacy;
  }
  
  public float getSize() {
    return size;
  }
  
  public void setSize(float size) {
    this.size = size;
  }

  public String getCreator() {
    return this.creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  
}
