package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.LocationStatus;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.PromptType;

public class SurveyResponse {

  protected class Location {
    public Location(Double latitude, Double longitude) { 
      this.latitude = latitude; 
      this.longitude = longitude; 
    }
    Double latitude; 
    Double longitude; 
  }
	
  String dbKey; // needed for updating response in db
  String campaignId; // urn
  String campaignName;
  
  Date responseDate;
  Location location;
  LocationStatus locationStatus; // even if location is there, it might be inaccurate or stale
  String userName;
  Privacy privacyState;
	String surveyId; // identifies survey in xml config
	String surveyName; 
	List<PromptResponse> promptResponses;
	
  // NOTE: prompt responses above could be images, etc, but everything is 
	// passed as a string in the json(?)
	
	public SurveyResponse() {
	  init();
	}
	
	public SurveyResponse(String campaignName,
	     String surveyName,
	     Date responseDate,
	     Privacy privacyState) {
	  init();
	  
	}
	
	private void init() {
    this.promptResponses = new ArrayList<PromptResponse>();
	}

	public String getCampaignId() { return this.campaignId; }
	public void setCampaignId(String id) { this.campaignId = id; }
	
	public String getCampaignName() { return this.campaignName; }
	public void setCampaignName(String name) { this.campaignName = name; }

	public String getSurveyName() { return this.surveyName; }
	public void setSurveyName(String name) { this.surveyName = name; }
	
	// id and name same for now? // FIXME: get rid of one of them?
	public String getSurveyId() { return getSurveyName(); }
	public void setSurveyId(String id) { setSurveyName(id); }
	
	// response key is db id needed for updating or deleting response
	public String getResponseKey() { return this.dbKey; }
	public void setResponseKey(String dbKey) { this.dbKey = dbKey; } 
	
	public Date getResponseDate() { return this.responseDate; }
	public void setResponseDate(Date responseDate) {
	  this.responseDate = responseDate;
	}
	
	public void setLocation(Double latitude, Double longitude) {
	  location = (latitude != null && longitude != null) ? new Location(latitude, longitude) : null;
	}
	
	public boolean hasLocation() {
	  return this.locationStatus != null && 
	         this.location != null &&
	         !this.locationStatus.equals(LocationStatus.UNAVAILABLE);
	}
	
	public Double getLatitude() { return this.location.latitude; }
	public Double getLongitude() { return this.location.longitude; }
	
	public LocationStatus getLocationStatus() { return this.locationStatus; }
	public void setLocationStatus(LocationStatus status) { 
	  this.locationStatus = status; 
	}
	
	public String getUserName() { return this.userName; }
	public void setUserName(String userName) { this.userName = userName; }
	
	public Privacy getPrivacyState() { return this.privacyState; }
	public void setPrivacyState(Privacy privacy) { this.privacyState = privacy; } 
	
  public List<PromptResponse> getPromptResponses() {
    return this.promptResponses; // NOTE: changes to return vals also change originals
  }
	
  // inserts prompt in order (sorted by the prompt index field so they match the order
  //   in the xml config)
	public void addPromptResponse(PromptResponse newResponse) {
	  int indexOfNewResponse = newResponse.getIndex();
	  int position = 0;
	  while (position < promptResponses.size()) {
	    if (promptResponses.get(position).getIndex() > indexOfNewResponse) {
	      break;
	    }
	    position++;
	  }
	  promptResponses.add(position, newResponse);
	}
	
	/**
	 * Useful for filtering survey responses
	 * @return True if at least one prompt in the response is of type PHOTO
	 */
	public boolean hasImage() {
	  boolean hasImage = false;
	  for (PromptResponse promptResponse : promptResponses) {
	    if (promptResponse.getPromptType().equals(PromptType.PHOTO)) {
	      hasImage = true;
	      break;
	    }
	  }
	  return hasImage;
	}
	
}

