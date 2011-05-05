package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucla.cens.mobilize.client.AndWellnessConstants;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class SurveyResponseReadParams extends RequestParams {
  public String authToken;
  public String campaignUrn;
  public String client;
  public OutputFormat outputFormat;

  public List<String> userList = new ArrayList<String>();
  
  // these will default to all if not provided
  public List<String> columnList_opt = new ArrayList<String>();
  public List<String> promptIdList_opt = new ArrayList<String>();
  public List<String> surveyIdList_opt = new ArrayList<String>();
  
  public Privacy privacyState_opt = Privacy.UNDEFINED;
  
  // TODO: start_date, end_date
  
  public enum OutputFormat { JSON_ROWS, JSON_COLS, CSV;
    public String toParamString() {
      if (this.equals(JSON_ROWS)) return "json-rows";
      if (this.equals(JSON_COLS)) return "json-cols";
      if (this.equals(CSV)) return "csv";
      throw new RuntimeException("Invalid SurveyResponse.OutputFormat value." + this.toString());
    }
  }
  
  public String toString() {
    assert this.authToken != null : "authToken is required";
    assert this.campaignUrn != null : "campaignUrn is required";
    assert this.client != null : "client is required";
    assert this.outputFormat != null : "output format is required";
    assert this.userList != null && !this.userList.isEmpty() : "at least one user must be given in user list";  
    // FIXME: is that true? or can users be set to ohmage:all?
    
    Map<String, String> params = new HashMap<String, String>();
    params.put("auth_token", this.authToken);
    params.put("campaign_urn", this.campaignUrn);
    params.put("client", this.client);
    params.put("output_format", this.outputFormat.toParamString());
    params.put("user_list", CollectionUtils.join(this.userList, ","));
    // default to all columns if not provided
    params.put("column_list", getStringOrDefault(this.columnList_opt));
    params.put("survey_id_list", getStringOrDefault(this.surveyIdList_opt));
    //params.put("prompt_id_list", getStringOrDefault(this.promptIdList_opt));
        
    if (!this.privacyState_opt.equals(Privacy.UNDEFINED)) { // leave off if undefined
      params.put("privacy_state", this.privacyState_opt.toString().toLowerCase());
    }
    return MapUtils.translateToParameters(params);
  }

  // returns serialized list if not empty, special token meaning all values otherwise
  private String getStringOrDefault(List<String> optionalList) {
    String retval = null;
    if (optionalList != null && !optionalList.isEmpty()) {
      retval = CollectionUtils.join(optionalList, ",");
    } else {
      retval = AndWellnessConstants.specialAllValuesToken;
    }
    return retval;
  }
}
