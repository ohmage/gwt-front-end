package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RoleDocument;

public class DocumentInfo {

  String documentId;
  String documentName;
  String creator;
  Date creationTimestamp;
  Date lastModifiedTimestamp;
  List<String> authors = new ArrayList<String>();
  Map<String, RoleDocument> campaignUrnToRoleMap = new HashMap<String, RoleDocument>();
  Map<String, RoleDocument> classUrnToRoleMap = new HashMap<String, RoleDocument>();
  String description;
  Privacy privacy;
  RoleDocument userRole;
  RoleDocument userMaxRole;
  float size;
  private static final int BYTES_PER_KB = 1024;
  private static final int BYTES_PER_MB = 1024 * 1024;
  
  public boolean userCanEdit () {
    return this.userRole.equals(RoleDocument.OWNER) ||
           this.userRole.equals(RoleDocument.WRITER) ||
           this.userMaxRole.equals(RoleDocument.WRITER); //user is supervisor of a class/campaign listed in the document info
  }
  
  public String getDocumentId() {
    return this.documentId;
  }
  
  public void setDocumentId(String id) {
    this.documentId = id;
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

  public Collection<String> getCampaigns() {
    return this.campaignUrnToRoleMap.keySet();
  }
  
  public void addCampaign(String campaignUrn, RoleDocument role) {
    this.campaignUrnToRoleMap.put(campaignUrn, role);
  }

  public void clearCampaigns() {
    this.campaignUrnToRoleMap.clear();
  }

  public Collection<String> getClasses() {
    return this.classUrnToRoleMap.keySet();
  }
  
  public void addClass(String classUrn, RoleDocument role) {
    this.classUrnToRoleMap.put(classUrn, role);
  }
  
  public void clearClasses() {
    this.classUrnToRoleMap.clear();
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
  
  public float getSizeInBytes() {
    return size;
  }
  
  public float getSizeInKB() {
    return size / BYTES_PER_KB;
  }
  
  public float getSizeInMB() {
    return size / BYTES_PER_MB;
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

  public RoleDocument getUserRole() {
    return this.userRole;
  }
  
  public void setUserRole(RoleDocument role) {
    this.userRole = role;
  }
  
  public RoleDocument getUserMaxRole() {
    return this.userMaxRole;
  }
	  
  public void setUserMaxRole(RoleDocument role) {
    this.userMaxRole = role;
  }
}
