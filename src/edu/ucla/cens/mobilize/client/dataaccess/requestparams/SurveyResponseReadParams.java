package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class SurveyResponseReadParams implements RequestParams {
  // NOTE: if you add a new param here make sure to add it to the copy constructor as well
  public String authToken;
  public String campaignUrn;
  public String client;
  public OutputFormat outputFormat;
  
  // tell server to include survey response key (needed for updating response)
  public boolean returnId = true;

  public List<String> userList = new ArrayList<String>();
  
  // these will default to all if not provided
  public List<String> columnList_opt = new ArrayList<String>();
  public List<String> promptIdList_opt = new ArrayList<String>();
  public List<String> surveyIdList_opt = new ArrayList<String>();
  
  public Privacy privacyState_opt = Privacy.UNDEFINED;
  public boolean collapse = false; // server removes dupes when true
  
  // start and end date are optional, but if one is present, both must be present
  public Date startDate_opt;
  public Date endDate_opt;
  
  // for paging
  public Integer numToSkip_opt = null;
  public Integer numToProcess_opt = null;
  
  // not used in api query but is needed by response presenter for bookkeeping
  public String promptType_opt = null;
  
  public enum OutputFormat { JSON_ROWS, JSON_COLS, CSV;
    public String toParamString() {
      if (this.equals(JSON_ROWS)) return "json-rows";
      if (this.equals(JSON_COLS)) return "json-cols";
      if (this.equals(CSV)) return "csv";
      throw new RuntimeException("Invalid SurveyResponse.OutputFormat value." + this.toString());
    }
  }

  public SurveyResponseReadParams() {}
  
  /**
   * Copy constructor, useful when you need a defensive copy
   * @param origParams
   */
  public SurveyResponseReadParams(SurveyResponseReadParams origParams) {
    this.authToken = origParams.authToken;
    this.campaignUrn = origParams.campaignUrn;
    this.client = origParams.client;
    this.collapse = origParams.collapse;
    if (origParams.columnList_opt != null) {
      this.columnList_opt = new ArrayList<String>(origParams.columnList_opt);
    }
    this.endDate_opt = origParams.endDate_opt;
    this.numToProcess_opt = origParams.numToProcess_opt;
    this.numToSkip_opt = origParams.numToSkip_opt;
    this.outputFormat = origParams.outputFormat;
    this.privacyState_opt = origParams.privacyState_opt;
    if (origParams.promptIdList_opt != null) {
      this.promptIdList_opt = new ArrayList<String>(origParams.promptIdList_opt);
    }
    this.returnId = origParams.returnId;
    this.startDate_opt = origParams.startDate_opt;
    if (origParams.surveyIdList_opt != null) {
      this.surveyIdList_opt = new ArrayList<String>(origParams.surveyIdList_opt);
    }
    if (origParams.userList != null) {
      this.userList = new ArrayList<String>(origParams.userList);
    }
    this.promptType_opt = origParams.promptType_opt;
  }
  
  @Override
  public String toString() {
    assert this.authToken != null : "authToken is required";
    assert this.campaignUrn != null : "campaignUrn is required";
    assert this.client != null : "client is required";
    assert this.outputFormat != null : "output format is required";
    assert this.userList != null && !this.userList.isEmpty() : "at least one user must be given in user list. (Can be AwConstants.specialAllValuesToken)";
    assert (this.startDate_opt == null && this.endDate_opt == null) || // both dates null is ok 
           (this.startDate_opt != null && this.endDate_opt != null) :  // both dates set is ok
           "startDate and endDate must both be set or both be null";
    
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("campaign_urn", this.campaignUrn);
    params.put("client", this.client);
    params.put("output_format", this.outputFormat.toParamString());
    params.put("user_list", CollectionUtils.join(this.userList, ","));
    // default to all columns if not provided
    params.put("column_list", getStringOrDefault(this.columnList_opt));
    params.put("survey_id_list", getStringOrDefault(this.surveyIdList_opt));
    params.put("return_id", this.returnId ? "true" : "false");
        
    if (this.privacyState_opt != null && !this.privacyState_opt.equals(Privacy.UNDEFINED)) { // leave off if undefined
      params.put("privacy_state", this.privacyState_opt.toString().toLowerCase());
    }
    
    if (this.startDate_opt != null) {
      params.put("start_date", DateUtils.translateToApiRequestFormat(this.startDate_opt));
    }
    
    if (this.endDate_opt != null) {
      params.put("end_date", DateUtils.translateToApiRequestFormat(this.endDate_opt));
    }
    
    if (this.collapse) {
      params.put("collapse", "true");
    }
    
    if (this.numToSkip_opt != null) {
      params.put("num_to_skip", Integer.toString(this.numToSkip_opt));
    }
    
    if (this.numToProcess_opt != null) {
      params.put("num_to_return", Integer.toString(this.numToProcess_opt));
    }
    
    return MapUtils.translateToParameters(params);
  }
  
  /**
   * @return first user in userlist or null if none
   */
  public String getUser() {
    return (this.userList != null && !this.userList.isEmpty()) ? this.userList.get(0) : null;
  }
  
  /**
   * @return First survey in list or null if none
   */
  public String getSurvey() {
    return (this.surveyIdList_opt != null && !this.surveyIdList_opt.isEmpty()) ? this.surveyIdList_opt.get(0) : null;
  }

  // helper method
  // returns serialized list if not empty, special token meaning all values otherwise
  private String getStringOrDefault(List<String> optionalList) {
    String retval = null;
    if (optionalList != null && !optionalList.isEmpty()) {
      retval = CollectionUtils.join(optionalList, ",");
    } else {
      retval = AwConstants.specialAllValuesToken;
    }
    return retval;
  }
  
}
