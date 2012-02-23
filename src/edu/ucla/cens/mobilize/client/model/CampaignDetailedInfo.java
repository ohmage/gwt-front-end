package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.utils.XmlConfigTranslator;

public class CampaignDetailedInfo {
  private String campaignId;
  private String campaignName;
  private String description;
  private RunningState runningState;
  private Privacy privacy;
  private List<String> classes;
  private List<RoleCampaign> userRoles; // roles for logged in user
  private List<String> authors;
  private List<String> supervisors;
  private Date creationTime;
  
  private String xmlConfig;
  private XmlConfigTranslator configTranslator;
  
  public CampaignDetailedInfo() {
    classes = new ArrayList<String>();
    authors = new ArrayList<String>();
    supervisors = new ArrayList<String>();
    userRoles = new ArrayList<RoleCampaign>();
    campaignId = "";
    campaignName = "";
    description = "";
    runningState = RunningState.STOPPED;
    privacy = Privacy.PRIVATE;
    xmlConfig = "";  
    configTranslator = new XmlConfigTranslator();
  };

  /************ ACCESS CONTROL **************/
  public boolean userCanEdit() {
    return this.userRoles.contains(RoleCampaign.AUTHOR) ||
           this.userRoles.contains(RoleCampaign.SUPERVISOR);
  }
  
  public boolean userCanDelete(String username) {
    return authors.contains(username) || supervisors.contains(username); // && no responses FIXME
  }
  
  public boolean userCanAnalyze() {
    // user can analyze if:
    // 1. he is listed as an analyst and the campaign is public
    // 2. he is a supervisor of the campaign
    // 3. he is an author of the campaign
    return (this.userRoles.contains(RoleCampaign.ANALYST) && this.privacy.equals(Privacy.SHARED)) ||
           this.userRoles.contains(RoleCampaign.SUPERVISOR) || 
           this.userRoles.contains(RoleCampaign.AUTHOR);
  }
  
  /************ CONVENIENCE METHODS **************/
  public boolean isRunning() {
    return this.runningState.equals(RunningState.RUNNING);
  }
  
  public boolean isShared() {
    return this.privacy.equals(Privacy.SHARED);
  }
  
  public boolean userIsSupervisorOrAdmin() {
    return this.userRoles.contains(RoleCampaign.SUPERVISOR) || 
           this.userRoles.contains(RoleCampaign.ADMIN);
  }
  
  public boolean userIsParticipant() {
    return this.userRoles.contains(RoleCampaign.PARTICIPANT);
  }
  
  /************ GETTERS **************/
  
  public String getCampaignId() {
    return campaignId;
  }
  
  public String getCampaignName() {
    return campaignName;
  }
  
  public String getDescription() {
    return description;
  }
  
  // returns list of group ids
  public List<String> getClasses() {
    return classes;
  }
  
  // returns list of usernames
  public List<String> getAuthors() {
    return authors;
  }
  
  // returns list of usernames
  public List<String> getSupervisors() {
    return supervisors;
  }
  
  public RunningState getRunningState() {
    return runningState;
  }
  
  public Privacy getPrivacy() {
    return privacy;
  }

  // gets one survey from config. returns null if not found
  public SurveyInfo getSurvey(String surveyId) {
    return this.configTranslator.getSurveyInfo(surveyId);
  }
  
  // gets all surveys from xml config
  public List<SurveyInfo> getSurveys() {
    return this.configTranslator.getSurveyInfos();
  }
  
  // gets list of all survey ids. useful so you can get ids for data point queries
  // without having to build surveyinfo/promptinfo objects
  public List<String> getSurveyIds() {
    return this.configTranslator.getSurveyIds();
  }
  
  public List<String> getPromptIds() {
    return this.configTranslator.getPromptIds(); 
  }
  
  // Prompt ids are unique within a campaign (as of 07/2011) so it's reasonable
  // to get just prompts without getting surveys
  public List<PromptInfo> getPrompts() {
    return this.configTranslator.getPromptInfos();
  }
  
  // permissions 
  public List<RoleCampaign> getUserRoles() {
    return this.userRoles != null ? this.userRoles : new ArrayList<RoleCampaign>();
  }

  // returns xml config as a string, useful for when user wants to view config
  public String getXmlConfig() {
    return this.xmlConfig != null ? this.xmlConfig : "xml config not loaded";
  }
  
  public Date getCreationTime() {
    return this.creationTime;
  }
  
  /************ SETTERS ***************/
  
  public void setCampaignId(String campaignId) {
    this.campaignId = campaignId;
  }
  
  public void setCampaignName(String campaignName) {
    this.campaignName = campaignName;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }

  public void addClass(String classId) {
    this.classes.add(classId);
  }
  
  public void removeClass(String classId) {
    if (this.classes.contains(classId)) {
      this.classes.remove(classId);
    }
  }
  
  public void clearClasses() {
    this.classes.clear();
  }

  public void addAuthor(String author) {
    this.authors.add(author);
  }
  
  public void removeAuthor(String author) {
    if (this.authors.contains(author)) {
      this.authors.remove(author);
    }
  }
  
  public void clearAuthors() {
    this.authors.clear();
  }
  
  public void addSupervisor(String supervisor) {
    supervisors.add(supervisor);
  }
  
  public void removeSupervisor(String supervisor) {
    if (supervisors.contains(supervisor)) {
      supervisors.remove(supervisor);
    }
  }
  
  public void clearSupervisors() {
    supervisors.clear();
  }
  
  public void setXmlConfig(String xmlConfig) {
    if (this.configTranslator.loadFromXml(xmlConfig)) {
      this.xmlConfig = xmlConfig;
    } else {
      this.xmlConfig = "there was a problem loading the xml config";
    }
  }

  public void addUserRole(RoleCampaign roleOfCurrentUser) {
    this.userRoles.add(roleOfCurrentUser);
  }
  
  public void setRunningState (RunningState runningState) {
    this.runningState = runningState;
  }
  
  public void setPrivacy(Privacy privacy) {
    this.privacy = privacy;
  }
  
  public void setCreationTime(Date time) {
    this.creationTime = time;
  }

  public List<String> getSurveyIdsByPromptType(String promptType) { 
    return this.configTranslator.getSurveyIdsByPromptType(promptType);
  }
}
