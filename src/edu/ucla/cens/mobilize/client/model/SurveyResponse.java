package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucla.cens.mobilize.client.utils.DateUtils;

public class SurveyResponse {
	
  public enum ResponseStatus { PUBLIC, PRIVATE }	
	
  Date responseDate;
  ResponseStatus responseStatus;
  String campaignId; // urn
	String campaignName;
	String surveyId;
	String surveyName;
	Map<String, PromptInfo> promptInfos;
	List<DataPointAwData> rawPromptResponses; // from data point api
	List<PromptResponse> promptResponses;
	
  // NOTE: prompt responses above could be images, etc, but everything is 
	// passed as a string in the json(?)
	
	public SurveyResponse() {
	  init();
	}
	
	public SurveyResponse(String campaignName,
	     String surveyName,
	     Date responseDate,
	     ResponseStatus status) {
	  init();
	  
	}
	
	private void init() {
    this.promptInfos = new HashMap<String, PromptInfo>();
    this.promptResponses = new ArrayList<PromptResponse>();
    this.rawPromptResponses = new ArrayList<DataPointAwData>();
	}

	public String getCampaignId() { return this.campaignId; }
	public void setCampaignId(String id) { this.campaignId = id; }
	
	public String getCampaignName() { return this.campaignName; }
	public void setCampaignName(String name) { this.campaignName = name; }

	public String getSurveyName() { return this.surveyName; }
	public void setSurveyName(String name) { this.surveyName = name; }
	
	// id and name same for now?
	public String getSurveyId() { return getSurveyName(); }
	public void setSurveyId(String id) { setSurveyName(id); }
	
	public Date getResponseDate() { return this.responseDate; }
	public void setResponseDate(String dateStringInServerFormat) {
	  try {
      this.responseDate = DateUtils.translateFromServerFormat(dateStringInServerFormat);
	  } catch (Exception exception) { // FIXME: IllegalArgumentException?
	    // TODO: log error message
	    responseDate = new Date();
	    
	  }
	}
	
	public ResponseStatus getResponseStatus() { return this.responseStatus; }
	public void setResponseStatus(ResponseStatus status) { this.responseStatus = status; } 
	
  public List<PromptResponse> getPromptResponses() {
    List<PromptResponse> retval = new ArrayList<PromptResponse>(this.promptResponses);
    return retval;
  }
  
	public void setPromptResponses(List<PromptInfo> infos, List<DataPointAwData> dataPoints) {
	  // create map of prompt ids to prompt infos
	  this.promptInfos.clear();
	  for (int i = 0; i < infos.size(); i++) {
	    this.promptInfos.put(infos.get(i).getPromptId(), infos.get(i));
	  }
	  // for each data point, find matching info and save info/data pair 
	  this.promptResponses.clear();
	  for (int i = 0; i < dataPoints.size(); i++) {
	    DataPointAwData dp = dataPoints.get(i);
	    // NOTE: "label" in DataPointAwData is the promptId
	    if (this.promptInfos.containsKey(dp.getLabel())) {
	      PromptInfo info = this.promptInfos.get(dp.getLabel());
	      this.promptResponses.add(new PromptResponse(info, dp));
	    }
	  }
	}
	
	public void addPromptResponse(PromptInfo promptInfo, DataPointAwData dataPoint) {
	  this.promptResponses.add(new PromptResponse(promptInfo, dataPoint));
	}
	
	public String getDetails() {
		// todo: should return 2d array (prompts/responses) not formatted text
		StringBuilder sb = new StringBuilder();
		sb.append("<div class='prompt'>How many hours did you sleep?</div>");
		sb.append("<div class='promptResponse'>five</div>");
		sb.append("<div class='prompt'>Do you feel rested?</div>");
		sb.append("<div class='promptResponse'>no</div>");
		sb.append("<div class='prompt'>Why not?</div>");
		sb.append("<div class='promptResponse'>are you serious?</div>");
		sb.append("<div class='prompt'>I CAN HAS MOAR SLEEP?.</div>");
		sb.append("<div class='promptResponse'>");
		sb.append("<img src='http://placekitten.com/g/100/130'/>");
		sb.append("</div>");
		return sb.toString();
	}
	
}

