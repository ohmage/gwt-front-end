package edu.ucla.cens.mobilize.client.dataaccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.PlotType;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.common.RoleDocument;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.common.UserRoles;
import edu.ucla.cens.mobilize.client.common.UserStats;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.DocumentReadParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseReadParams;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserShortInfo;
import edu.ucla.cens.mobilize.client.utils.XmlConfigTranslator;

public class MockDataService implements DataService {
  List<CampaignShortInfo> campaignsConcise = new ArrayList<CampaignShortInfo>();
  List<SurveyResponse> surveyResponses = new ArrayList<SurveyResponse>();
  
  Map<String, CampaignDetailedInfo> campaigns = new HashMap<String, CampaignDetailedInfo>();
  List<DocumentInfo> documents = new ArrayList<DocumentInfo>();
  List<String> classes = new ArrayList<String>();
  List<ClassInfo> classInfos = new ArrayList<ClassInfo>();
  
  XmlConfigTranslator configTranslator = new XmlConfigTranslator();

  private static Logger _logger = Logger.getLogger(MockDataService.class.getName());
  
  public MockDataService() {
    loadFakeClasses();
    loadFakeClassInfos();
    loadFakeCampaigns();
  }

  private void loadFakeClasses() {
    classes.clear();
    classes.add("ADDAMS_HS_CS101_Fall_2011");
    classes.add("BH_HS_CS102_Spring_2011");
    classes.add("Carson_HS_CS103_Spring_2011");
    classes.add("Crenshaw_HS_CS104_Fall_2011");
    classes.add("Gardena_HS_CS104_Fall_2011");
  }
  
  private void loadFakeClassInfos() {
    classInfos.clear();
    ClassInfo class1 = new ClassInfo();
    class1.setClassId("urn:class:ca:lausd:ADDAMS_HS:CS101:Fall:2011");
    class1.setClassName("ADDAMS_HS_CS101_Fall_2011");
    class1.addMember("slarson", RoleClass.RESTRICTED);
    class1.addMember("mhardy", RoleClass.RESTRICTED);
    class1.addMember("awhine", RoleClass.RESTRICTED);
    class1.addMember("rmoran", RoleClass.RESTRICTED);
    class1.addMember("ajones", RoleClass.RESTRICTED);
    class1.addMember("smcmike", RoleClass.RESTRICTED);
    class1.addMember("tuser", RoleClass.PRIVILEGED);
    classInfos.add(class1);
    
    ClassInfo class2 = new ClassInfo();
    class2.setClassId("urn:class:ca:lausd:Boyle_Heights_HS:CS102:Spring:2011");
    class2.setClassName("BH_HS_CS102_Spring_2011");
    class2.addMember("testuser.aa", RoleClass.RESTRICTED);
    class2.addMember("testuser.ab", RoleClass.RESTRICTED);
    class2.addMember("testuser.ac", RoleClass.RESTRICTED);
    class2.addMember("testuser.bb", RoleClass.RESTRICTED);
    class2.addMember("testuser.aa", RoleClass.PRIVILEGED);
    class2.addMember("testuser.ab", RoleClass.PRIVILEGED);
    classInfos.add(class2);
    
    ClassInfo class3 = new ClassInfo();
    class3.setClassId("urn:class:ca:lausd:Carson_HS:CS103:Spring:2011");
    class3.setClassName("Carson_HS_CS103_Spring_2011");
    class3.addMember("testuser.ac", RoleClass.RESTRICTED);
    class3.addMember("testuser.bb", RoleClass.RESTRICTED);
    class3.addMember("testuser.aa", RoleClass.PRIVILEGED);
    classInfos.add(class3);
  }
  
  private void loadFakeCampaigns() {
    List<String> authors = new ArrayList<String>();
    authors.add("Amy Willerton");
    authors.add("Greg Mondavi");
    authors.add("Gary McLauren");
    
    Date dateNow = new Date();
    
    campaigns.clear();
    campaignsConcise.clear();
    for (int i = 0; i < 3; i++) {
      CampaignDetailedInfo info = new CampaignDetailedInfo();
      
      // detail 1
      info.setCampaignName("CS202_Fall_2011_Media" + Integer.toString(i));
      info.setCampaignId("urn:campaign:ca:lausd:ADDAMS_HS:CS202:Fall:2011:media" + Integer.toString(i));
      List<RoleCampaign> roles = new ArrayList<RoleCampaign>();
      roles.add(RoleCampaign.PARTICIPANT);
      roles.add(RoleCampaign.ANALYST);
      for (RoleCampaign role : roles) info.addUserRole(role);
      info.setDescription("Monitor sleeping patterns");
      info.setPrivacy(Privacy.SHARED);
      info.setRunningState(RunningState.RUNNING);
      for (int j = 0; j < classes.size(); j++) {
        info.addClass(classes.get(j));
      }
      for (int j = 3; j < authors.size(); j++) {
        info.addAuthor(authors.get(j));
      }
      info.setXmlConfig(this.getConfigXml());
      
      campaigns.put(info.getCampaignId(), info);
      
      info = new CampaignDetailedInfo();
      info.setCampaignName("NIH_DietSens" + Integer.toString(i));
      info.setCampaignName("CS301_Spring_2011_DietSens" + Integer.toString(i));
      info.setCampaignId("urn:campaign:ca:lausd:ADDAMS_HS:CS301:Spring:2011:dietsens" + Integer.toString(i));
      roles = new ArrayList<RoleCampaign>();
      roles.add(RoleCampaign.PARTICIPANT);
      roles.add(RoleCampaign.AUTHOR);
      for (RoleCampaign role : roles) info.addUserRole(role);
      info.setDescription("What people eat");
      info.setPrivacy(Privacy.SHARED);
      info.setRunningState(RunningState.STOPPED);
      for (int j = 4; j < classes.size(); j++) {
        info.addClass(classes.get(j));
      }
      for (int j = 2; j < authors.size(); j++) {
        info.addAuthor(authors.get(j));
      }
      info.setXmlConfig(this.getConfigXml());
      
      campaigns.put(info.getCampaignId(), info);
      
      info = new CampaignDetailedInfo();
      info.setCampaignName("Advertising" + Integer.toString(i));
      info.setCampaignName("CS101_Fall_2011_SleepSens" + Integer.toString(i));
      info.setCampaignId("urn:campaign:ca:lausd:ADDAMS_HS:CS101:Fall:2011:sleepsens" + Integer.toString(i));
      roles = new ArrayList<RoleCampaign>();
      roles.add(RoleCampaign.ADMIN);
      roles.add(RoleCampaign.PARTICIPANT);
      roles.add(RoleCampaign.ANALYST);
      for (RoleCampaign role : roles) info.addUserRole(role);
      info.setDescription("Raise awareness of advertisements in the community");
      info.setPrivacy(Privacy.PRIVATE);
      info.setRunningState(RunningState.STOPPED);
      for (int j = 3; j < classes.size(); j++) {
        info.addClass(classes.get(j));
      }
      for (int j = 0; j < authors.size(); j++) {
        info.addAuthor(authors.get(j));
      }
      info.setXmlConfig(this.getConfigXml());
      campaigns.put(info.getCampaignId(), info);
      
      // copy over relevant values
      for (CampaignDetailedInfo cdi : campaigns.values()) {
        campaignsConcise.add(new CampaignShortInfo(cdi.getCampaignId(),
            cdi.getCampaignName(),
            cdi.getRunningState(),
            cdi.getPrivacy(),
            new UserRoles(cdi.getUserRoles()),
            dateNow
            ));
      }
    }
  }

  public void loadFakeDocuments() {
    documents.clear();
    
    DocumentInfo docInfo0 = new DocumentInfo();
    docInfo0.setCreationTimestamp(new Date()); // now
    docInfo0.setCreator("user.adv.supa");
    docInfo0.setDescription("Analysis doc for campaign");
    docInfo0.setDocumentId("123");
    docInfo0.setDocumentName("file.txt");
    docInfo0.setUserRole(RoleDocument.OWNER);
    docInfo0.setLastModifiedTimestamp(new Date());
    docInfo0.setPrivacy(Privacy.PRIVATE);
    docInfo0.setSize(100); // MB
    docInfo0.addCampaign("urn:class:ca:lausd:Boyle_Heights_HS:CS102:Spring:2011:advertisting",
                         RoleDocument.READER);
    docInfo0.addCampaign("urn:campaign:ca:lausd:ADDAMS_HS:CS101:Fall:2011:sleepsens",
                         RoleDocument.READER);
    documents.add(docInfo0);
    
    DocumentInfo docInfo1 = new DocumentInfo();
    docInfo1.setCreationTimestamp(new Date()); // now
    docInfo1.setCreator("user.adv.supa");
    docInfo1.setDescription("Analysis doc for campaign");
    docInfo1.setDocumentId("321");
    docInfo1.setDocumentName("file.txt");
    docInfo1.setUserRole(RoleDocument.WRITER);
    docInfo1.setLastModifiedTimestamp(new Date());
    docInfo1.setPrivacy(Privacy.SHARED);
    docInfo1.setSize(100); // MB
    docInfo1.addCampaign("urn:class:ca:lausd:Boyle_Heights_HS:CS102:Spring:2011:advertising",
                         RoleDocument.READER);
    documents.add(docInfo1);
    
    DocumentInfo docInfo2 = new DocumentInfo();
    docInfo2.setCreationTimestamp(new Date()); // now
    docInfo2.setCreator("user.adv.supa");
    docInfo2.setDescription("Analysis doc for campaign");
    docInfo2.setDocumentId("123squee");
    docInfo2.setDocumentName("file.txt");
    docInfo2.setUserRole(RoleDocument.READER);
    docInfo2.setLastModifiedTimestamp(new Date());
    docInfo2.setPrivacy(Privacy.PRIVATE);
    docInfo2.setSize(100); // MB
    docInfo2.addCampaign("urn:class:ca:lausd:Boyle_Heights_HS:CS102:Spring:2011:advertising",
                         RoleDocument.READER);
    docInfo2.addCampaign("urn:campaign:ca:lausd:ADDAMS_HS:CS101:Fall:2011:sleepsens",
                         RoleDocument.READER);
    documents.add(docInfo2);
  }

  @Override
  public String authToken() {
    return "";
  }
  
  @Override
  public String client() {
    return "gwt-mock";
  }
  
  @Override
  public void init(String username, String auth_token) {
    // TODO Auto-generated method stub
  }


  @Override
  public void changePassword(String userName, 
                             String oldPassword,
                             String newPassword,
                             final AsyncCallback<String> callback) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void fetchUserInfo(String username, AsyncCallback<UserInfo> callback) {
    UserStats counts = new UserStats();
    
    counts.numUnreadResponses = Random.nextInt(20);
    
    boolean canCreate = true;
    
    Map<String, String> classIdToNameMap = new HashMap<String, String>();
    classIdToNameMap.put("urn:class:ca:lausd:Addams_HS:CS101:Fall:2011", "ADDAMS_HS_CS101_Fall_2011");
    classIdToNameMap.put("urn:class:ca:lausd:BoyleHeights_HS:CS102:Spring:2011", "BoyleHeights_HS_CS102_Spring_2011");
    classIdToNameMap.put("urn:class:ca:lausd:Carson_HS:CS103:Spring:2011", "Carson_HS_CS103_Spring_2011");
    
    Map<String, String> campaignIdToNameMap = new HashMap<String, String>();
    campaignIdToNameMap.put("urn:campaign:ca:lausd:BoyleHeights_HS:CS102:Spring:2011:Sleep", "Sleep");
    campaignIdToNameMap.put("urn:campaign:ca:lausd:BoyleHeights_HS:CS102:Spring:2011:Snack", "Snack");
    campaignIdToNameMap.put("urn:campaign:ca:lausd:Addams_HS:CS101:Fall:2011:Advertisement", "Advertisement");
    
    List<CampaignDetailedInfo> infos = new ArrayList<CampaignDetailedInfo>(this.campaigns.values());
    List<RoleCampaign> roles = new ArrayList<RoleCampaign>();
    for (CampaignDetailedInfo campaign : this.campaigns.values()) {
      for (RoleCampaign role : campaign.getUserRoles()) {
        roles.add(role);
      }
    }
    
    UserInfo user = new UserInfo();
    user.setUserName(username);
    user.setCanCreateFlag(canCreate);
    user.setPrivilegeFlag(true);
    user.setClasses(classIdToNameMap);
    user.setCampaigns(campaignIdToNameMap);
    user.setCampaignRoles(roles);
    
    callback.onSuccess(user);
  }
  
  @Override
  public void fetchCampaignListShort(CampaignReadParams params,
      AsyncCallback<List<CampaignShortInfo>> callback) {
    callback.onSuccess(campaignsConcise);    
  }
  
  @Override
  public void fetchCampaignDetail(String campaignId,
      AsyncCallback<CampaignDetailedInfo> callback) {
    for (int i = 0; i < campaigns.size(); i++) {
      if (campaigns.containsKey(campaignId) && 
          campaigns.get(campaignId).getCampaignId().equals(campaignId)) {
        callback.onSuccess(campaigns.get(campaignId));
        break;
      }
    }
  }
  
  @Override
  public void fetchCampaignListDetail(List<String> campaignIds,
      AsyncCallback<List<CampaignDetailedInfo>> callback) {
    List<CampaignDetailedInfo> infos = new ArrayList<CampaignDetailedInfo>();
    for (String id : campaignIds) {
      if (campaigns.containsKey(id)) {
        infos.add(campaigns.get(id));
      }
    }
    callback.onSuccess(infos);
  }
  
  @Override
  public void fetchAuthorizationToken(String userName, String password,
      AsyncCallback<AuthorizationTokenQueryAwData> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteCampaign(String campaignId, AsyncCallback<String> callback) {
    boolean fakeSuccess = Random.nextBoolean();
    if (fakeSuccess) {
      callback.onSuccess("woo!");
    } else {
      callback.onFailure(new Exception("boo!"));
    }
  }
  
  private String getSuccessJson() {
    return "{\"result\":\"success\"}";
  }
  
  private String getFailureJson() {
    return "{\"result\":\"failure\",\"errors\":[{\"code\":0000,\"text\":\"Campaign can not be deleted because it has responses\"}]}}";
  }
  
  private String getConfigXml() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><campaign><campaignName>NIH</campaignName><campaignVersion>1.0</campaignVersion><serverUrl>https://heart.andwellness.org</serverUrl><surveys><survey><id>diet</id><title>Diet</title><description>Thisisthedietsurvey.</description><submitText>Thisisthedietsurveysubmittext.</submitText><showSummary>false</showSummary><anytime>true</anytime><contentList><prompt><id>mealPrepared</id><displayType>category</displayType><displayLabel>Howwasyourlastmealprepared</displayLabel><promptText>Howwasyourlastmealprepared?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>Restaurant</label></property><property><key>1</key><label>Fastfood</label></property><property><key>2</key><label>Homemade</label></property><property><key>3</key><label>Pre-pared(i.e.,deli,saladbar)</label></property><property><key>4</key><label>Frozendinner</label></property><property><key>5</key><label>Other</label></property></properties><skippable>false</skippable></prompt><prompt><id>whoDidYouEatWith</id><displayType>category</displayType><displayLabel>Whodidyoueatwith</displayLabel><promptText>Whodidyoueatwith?</promptText><promptType>multi_choice</promptType><properties><property><key>0</key><label>Alone</label></property><property><key>1</key><label>Friends</label></property><property><key>2</key><label>Family</label></property><property><key>3</key><label>Co-workers</label></property><property><key>4</key><label>Other</label></property></properties><skippable>false</skippable></prompt><prompt><id>howMuchDidYouEat</id><displayType>category</displayType><displayLabel>Howmuchdidyoueat</displayLabel><promptText>Howmuchdidyoueat?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>Toolittle/notenough</label></property><property><key>1</key><label>Justright</label></property><property><key>2</key><label>Toomuch</label></property></properties><skippable>false</skippable></prompt><prompt><id>nutritionalQuality</id><displayType>category</displayType><displayLabel>Ratethenutritionalqualityofthismeal</displayLabel><promptText>Ratethenutritionalqualityofthismeal</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>low</label></property><property><key>1</key><label>medium</label></property><property><key>2</key><label>high</label></property></properties><skippable>false</skippable></prompt></contentList></survey><survey><id>exerciseAndActivity</id><title>ExerciseandActivity</title><description>Thisistheexerciseandactivitysurvey.</description><submitText>Thisistheexerciseandactivitysurveysubmittext.</submitText><showSummary>false</showSummary><anytime>true</anytime><contentList><prompt><id>didYouExercise</id><displayType>category</displayType><displayLabel>Haveyouexercisedtoday</displayLabel><promptText>Haveyouexercisedtoday?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>No</label></property><property><key>1</key><label>Yes</label></property></properties><skippable>false</skippable></prompt><prompt><id>planningToExercise</id><condition>didYouExercise==0</condition><displayType>category</displayType><displayLabel>Wereyouplanningtoexercisetoday</displayLabel><promptText>Wereyouplanningtoexercisetoday?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>No</label></property><property><key>1</key><label>Yes</label></property></properties><skippable>false</skippable></prompt><prompt><id>whyNotExercise</id><condition>didYouExercise==0andplanningToExercise==1</condition><displayType>category</displayType><displayLabel>Disruptedplan</displayLabel><promptText>Whatpreventedyoufromexercisingasplanned?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>Childcare</label></property><property><key>1</key><label>Work</label></property><property><key>2</key><label>Personalneeds</label></property><property><key>3</key><label>Householdresponsibilities</label></property><property><key>4</key><label>Injured,inpain</label></property><property><key>5</key><label>Socialfunction</label></property><property><key>6</key><label>Medicalemergency</label></property><property><key>7</key><label>Noenergy</label></property><property><key>8</key><label>Other</label></property></properties><skippable>false</skippable></prompt><prompt><id>typeExercise</id><condition>didYouExercise==1</condition><displayType>category</displayType><displayLabel>TypeExercise</displayLabel><promptText>Describethetypesofexerciseyoudidtoday.</promptText><promptType>multi_choice</promptType><properties><property><key>0</key><label>Walk</label></property><property><key>1</key><label>Run</label></property><property><key>2</key><label>Yoga</label></property><property><key>3</key><label>Outdoorcyclingorsports</label></property><property><key>4</key><label>Indoorsports</label></property><property><key>5</key><label>Gym</label></property><property><key>6</key><label>Swim</label></property><property><key>7</key><label>Other</label></property></properties><skippable>false</skippable></prompt><prompt><id>timeSpentLight</id><condition>didYouExercise==1</condition><displayType>count</displayType><displayLabel>Timelightexercise</displayLabel><promptText>Howmanyminutesoflightexercisedidyoudotoday?(Noincreaseinbreathingorheartrate.)</promptText><promptType>number</promptType><properties><property><key>min</key><label>0</label></property><property><key>max</key><label>300</label></property></properties><default>15</default><skippable>false</skippable></prompt><prompt><id>timeSpentModerate</id><condition>didYouExercise==1</condition><displayType>count</displayType><displayLabel>Timemoderateexercise</displayLabel><promptText>Howmanyminutesofmoderateexercisedidyoudotoday?(Mildincreaseinbreathingorheartrate.)</promptText><promptType>number</promptType><properties><property><key>min</key><label>0</label></property><property><key>max</key><label>300</label></property></properties><default>15</default><skippable>false</skippable></prompt><prompt><id>timeSpentVigorous</id><condition>didYouExercise==1</condition><displayType>count</displayType><displayLabel>Timevigorousexercise</displayLabel><promptText>Howmanyminutesofvigorousexercisedidyoudotoday?(Significantincreaseinbreathingorheartrate.)</promptText><promptType>number</promptType><properties><property><key>min</key><label>0</label></property><property><key>max</key><label>300</label></property></properties><default>15</default><skippable>false</skippable></prompt><prompt><id>whoWith</id><condition>didYouExercise==1</condition><displayType>category</displayType><displayLabel>Whodidyouexercisewith</displayLabel><promptText>Whodidyoudothisexercisewith?</promptText><promptType>multi_choice</promptType><properties><property><key>0</key><label>Alone</label></property><property><key>1</key><label>Friends</label></property><property><key>2</key><label>Child</label></property><property><key>3</key><label>Partner/Spouse</label></property><property><key>4</key><label>Co-workers</label></property><property><key>5</key><label>Classorgroup</label></property><property><key>6</key><label>Other</label></property></properties><skippable>false</skippable></prompt><prompt><id>physicalActivity</id><displayType>category</displayType><displayLabel>Whatotherkindsofphysicalactivitydidyoudotoday</displayLabel><promptText>Whatotherkindsofphysicalactivitydidyoudotoday?</promptText><promptType>multi_choice</promptType><properties><property><key>0</key><label>None</label></property><property><key>1</key><label>Housework/Gardening</label></property><property><key>2</key><label>Workrelated</label></property><property><key>3</key><label>Carryingheavyloads</label></property><property><key>4</key><label>Activeplaywithchildren</label></property><property><key>5</key><label>Walking</label></property><property><key>6</key><label>Other</label></property></properties><skippable>false</skippable></prompt><prompt><id>timeForYourself</id><displayType>count</displayType><displayLabel>Howmuchtimedidyouhaveforyourselftoday</displayLabel><promptText>Howmuchtimedidyouhaveforyourselftoday?</promptText><promptType>number</promptType><properties><property><key>min</key><label>0</label></property><property><key>max</key><label>300</label></property></properties><default>15</default><skippable>false</skippable></prompt></contentList></survey><survey><id>foodButton</id><title>Stress</title><description>Notdisplayed</description><submitText>Notdisplayed</submitText><showSummary>false</showSummary><anytime>true</anytime><contentList><prompt><id>foodPhoto</id><displayType>event</displayType><displayLabel>photo</displayLabel><promptText>Takeapictureofyourfood</promptText><abbreviatedText>abbreviatedTextmustnotbeemptyifshowSummaryonitsparentsurveyistrue.</abbreviatedText><promptType>photo</promptType><properties><property><key>res</key><label>720</label></property></properties><skippable>true</skippable><skipLabel>skip</skipLabel></prompt><prompt><id>foodSnackMeal</id><displayType>category</displayType><displayLabel>Type</displayLabel><promptText>Wasthisasnackorameal?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>Snack</label><value>0</value></property><property><key>1</key><label>Meal</label><value>1</value></property></properties><skippable>false</skippable></prompt><prompt><id>foodHowHungry</id><displayType>count</displayType><displayLabel>Hunger</displayLabel><unit>hungeramount</unit><promptText>Howhungrywereyoubeforeeating?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>Very</label><value>0</value></property><property><key>1</key><label>Moderately</label><value>1</value></property><property><key>2</key><label>Slightly</label><value>2</value></property><property><key>3</key><label>Notatall</label><value>3</value></property></properties><skippable>false</skippable></prompt><prompt><id>foodHowPrepared</id><displayType>category</displayType><displayLabel>HowPrepared</displayLabel><promptText>Howwasthisfoodprepared?</promptText><promptType>multi_choice</promptType><properties><property><key>0</key><label>Restaurant</label><value>0</value></property><property><key>1</key><label>Fastfood</label><value>1</value></property><property><key>2</key><label>Homemade</label><value>2</value></property><property><key>3</key><label>Pre-pared(i.e.,deli,saladbar)</label><value>3</value></property><property><key>4</key><label>Frozendinner</label><value>4</value></property><property><key>5</key><label>Other</label><value>5</value></property></properties><skippable>false</skippable></prompt><prompt><id>foodWhoAteWith</id><displayType>category</displayType><displayLabel>WhoWith</displayLabel><promptText>Whodidyoueatwith?</promptText><promptType>multi_choice</promptType><properties><property><key>0</key><label>Alone</label><value>0</value></property><property><key>1</key><label>Friends</label><value>1</value></property><property><key>2</key><label>Family</label><value>2</value></property><property><key>3</key><label>Co-workers</label><value>3</value></property><property><key>4</key><label>Other</label><value>4</value></property></properties><skippable>false</skippable></prompt><prompt><id>foodHowMuch</id><displayType>category</displayType><displayLabel>HowMuch</displayLabel><promptText>Howmuchdidyoueat?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>Toolittle/notenough</label><value>0</value></property><property><key>1</key><label>Justright</label><value>1</value></property><property><key>2</key><label>Toomuch</label><value>2</value></property></properties><skippable>false</skippable></prompt><prompt><id>foodQuality</id><displayType>category</displayType><displayLabel>NutritionalQuality</displayLabel><promptText>Ratethenutritionalqualityofthismeal?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>Low</label><value>0</value></property><property><key>1</key><label>Medium</label><value>1</value></property><property><key>2</key><label>High</label><value>2</value></property></properties><skippable>false</skippable></prompt></contentList></survey><survey><id>moodAndStress</id><title>MoodandStress</title><description>Thisisthemoodandstresssurvey.</description><submitText>Thisisthemoodandstresssurveysubmittext.</submitText><showSummary>false</showSummary><anytime>true</anytime><contentList><prompt><id>mood</id><displayType>category</displayType><displayLabel>Howwouldyoudescribeyourmoodatthismoment</displayLabel><promptText>Howwouldyoudescribeyourmoodatthismoment?</promptText><promptType>multi_choice</promptType><properties><property><key>0</key><label>Sad</label></property><property><key>1</key><label>Relaxed</label></property><property><key>2</key><label>Anxious</label></property><property><key>3</key><label>Tired</label></property><property><key>4</key><label>Happy</label></property><property><key>5</key><label>Upset</label></property><property><key>6</key><label>Energetic</label></property><property><key>7</key><label>Irritable</label></property><property><key>8</key><label>Calm</label></property><property><key>9</key><label>Bored</label></property><property><key>10</key><label>Focused</label></property><property><key>11</key><label>Stressed</label></property></properties><skippable>false</skippable></prompt><prompt><id>feltStress</id><displayType>category</displayType><displayLabel>Stressinlasttwohours</displayLabel><promptText>Haveyoufeltstressinthelasttwohours?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>No</label></property><property><key>1</key><label>Yes</label></property></properties><skippable>false</skippable></prompt><prompt><id>howStressed</id><displayType>measurement</displayType><unit>howstressed</unit><condition>feltStress==1</condition><displayLabel>Howstressed</displayLabel><promptText>Howstressedwereyou,onascaleof1-5?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>1</label><value>1</value></property><property><key>1</key><label>2</label><value>2</value></property><property><key>2</key><label>3</label><value>3</value></property><property><key>3</key><label>4</label><value>4</value></property><property><key>4</key><label>5</label><value>5</value></property></properties><skippable>false</skippable></prompt><prompt><id>whatEvent</id><condition>feltStress==1</condition><displayType>category</displayType><displayLabel>Cause</displayLabel><promptText>Whatwasthecauseofyourstress?</promptText><promptType>single_choice</promptType><properties><property><key>0</key><label>Finances</label></property><property><key>1</key><label>Work/School</label></property><property><key>2</key><label>Lackofcontrol</label></property><property><key>3</key><label>Family/Relationships</label></property><property><key>4</key><label>Health</label></property><property><key>5</key><label>Traffic</label></property><property><key>6</key><label>Other</label></property></properties><skippable>false</skippable></prompt></contentList></survey><survey><id>sleep</id><title>Sleep</title><description>Thisisthesleepsurvey.</description><submitText>Thisisthesleepsurveysubmittext.</submitText><showSummary>false</showSummary><anytime>true</anytime><contentList><prompt><id>hoursOfSleep</id><displayType>count</displayType><displayLabel>HoursofSleep</displayLabel><promptText>Howmanyhoursintotaldidyousleeplastnight?</promptText><promptType>number</promptType><properties><property><key>min</key><label>0</label></property><property><key>max</key><label>24</label></property></properties><skippable>false</skippable></prompt></contentList></survey><survey><id>stressButton</id><title>Stress</title><description>Notdisplayed</description><submitText>Notdisplayed</submitText><showSummary>false</showSummary><anytime>true</anytime><contentList><prompt><id>stress</id><displayType>metadata</displayType><displayLabel>Stress</displayLabel><promptText>Notdisplayed</promptText><promptType>text</promptType><properties><property><key>min</key><label>1</label></property><property><key>max</key><label>1</label></property></properties><skippable>false</skippable></prompt></contentList></survey></surveys></campaign>";
  }

  @Override
  public void fetchClassList(List<String> schoolId,
      AsyncCallback<List<ClassInfo>> callback) {
    callback.onSuccess(this.classInfos);    
  }


  @Override
  public void fetchClassDetail(String classId, AsyncCallback<ClassInfo> callback) {
    for (ClassInfo info : this.classInfos) {
      if (info.getClassId().equals(classId)) {
        callback.onSuccess(info);
        return;
      }
    }
    callback.onFailure(new Exception("Class with id " + classId + " not found."));
  }

  @Override
  public void fetchCampaignIds(CampaignReadParams params,
      AsyncCallback<List<String>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchSurveyResponses(String userName, 
                                   String campaignId,
                                   String surveyName, 
                                   Privacy privacy,
                                   Date startDate,
                                   Date endDate,
                                   AsyncCallback<List<SurveyResponse>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void updateClass(ClassUpdateParams params, AsyncCallback<String> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void updateSurveyResponse(String campaignId, int surveyKey,
      Privacy newPrivacyState, AsyncCallback<String> callback) {
    callback.onSuccess("");    
  }

  @Override
  public void deleteSurveyResponse(String campaignId, int surveyKey,
      AsyncCallback<String> callback) {
    callback.onSuccess("");
  }

  @Override
  public void fetchDocumentList(DocumentReadParams params,
      AsyncCallback<List<DocumentInfo>> callback) {
    this.loadFakeDocuments();
    callback.onSuccess(this.documents);
    
  }

  @Override
  public void deleteDocument(String documentId, AsyncCallback<String> callback) {
    callback.onSuccess("");
  }

  @Override
  public void fetchCampaignIdToNameMap(CampaignReadParams params,
      AsyncCallback<Map<String, String>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Map<String, String> getSurveyResponseExportParams(String campaignId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, String> getCampaignXmlDownloadParams(String campaignId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, String> getDocumentDownloadParams(String documentId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void fetchSurveyResponseCount(String userName, String campaignId,
      String surveyName, Privacy privacy, Date startDate, Date endDate,
      AsyncCallback<Integer> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchParticipantsWithResponses(String campaignId,
      boolean onlySharedResponses,
      AsyncCallback<List<String>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getVisualizationUrl(PlotType plotType, int width, int height, String campaignId,
      String participantId, String promptX, String promptY, boolean sharedResponsesOnly) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void fetchVisualizationError(PlotType plotType, int width, int height,
      String campaignId, String participantId, String promptX, String promptY,
      boolean sharedResponsesOnly, AsyncCallback<String> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchClassMembers(String classUrn,
      AsyncCallback<List<UserShortInfo>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchSurveyResponses(SurveyResponseReadParams params,
      AsyncCallback<List<SurveyResponse>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchAppConfig(AsyncCallback<AppConfig> callback) {
    String loginPageHtml = "<h2>Mobilize = Computational Thinking + Data for Social Awareness &amp; Civil Engagement</h2><p>Through Mobilize students use mobile phones and web services tosystematically collect and interpret data about issues important tothem and their communities. The goal of Mobilize is to strengthencomputer science instruction throughout our educational system and todevelop innovative methods for educating and engaging students incomputational thinking and data analysis.</p><p>The core lessons in the Mobilize units will be framed aroundprinciples of Computational Thinking.  Specifically, throughParticipatory Sensing campaigns, students will identify phenomena tostudy, consider \"design\" for how data are to be collected, and rallythe computational resources to execute their plans. Along the way theywill grapple with questions related to the nature of data (itsrepresentation, formats and protocols for sharing) and of algorithms(rules governing data collection, strategies for analysis). Studentswill learn about classical themes in Computer Science, from databasesto networking, as well as lessons in Statistics, from visualization tobasic inference.</p><p>For more information, visit Mobilize at <a href=\"http://www.mobilizingcs.org\">http://www.mobilizingcs.org</a></p>";
    Map<String, String> links = new HashMap<String, String>();
    links.put("The Mobilize Project", "http://www.exploringcs.org/about/related-grants/mobilize");
    links.put("Exploring Computer Science Educational Initiative", "http://www.exploringcs.org");
    links.put("UCLA: Center For Embedded Networked Sensing", "http://research.cens.ucla.edu");
    links.put("How you can get involved", "http://www.exploringcs.org/about/ecs-now");
    List<Privacy> privacyStates = Arrays.asList(Privacy.PRIVATE, Privacy.SHARED);
    
    // set member vars
    AppConfig config = new AppConfig();
    config.setLoginPageHtml(loginPageHtml);
    config.setLoginPageLogoUrl("images/mobilize_logo_300x100.png");
    
    // links
    config.addLink("The Mobilize Project", "http://www.exploringcs.org/about/related-grants/mobilize");
    config.addLink("Exploring Computer Science Educational Initiative", "http://www.exploringcs.org");
    config.addLink("UCLA: Center For Embedded Networked Sensing", "http://research.cens.ucla.edu");
    config.addLink("How you can get involved", "http://www.exploringcs.org/about/ecs-now");
    
    // static member vars (needed across entire app)
    AppConfig.setAppName("Mobilize");
    AppConfig.setPrivacyStates(privacyStates);
    AppConfig.setSharedResponsesOnly(true);
    
    callback.onSuccess(config);
  }

}
