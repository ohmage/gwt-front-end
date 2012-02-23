package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.SurveyResponseReadParams;

public class SurveyResponseData {
  private List<SurveyResponse> surveyResponses;
  private SurveyResponseReadParams params; // params used to fetch this data, useful for refreshing
  private int totalResponses = 0;
  private int startIndex = 0;
  
  public void setResponses(int startIndex, List<SurveyResponse> responses) {
    this.surveyResponses = new ArrayList<SurveyResponse>(responses); 
    this.startIndex = startIndex;
  }
  
  /**
   * Returns slice of responses from startIndex to startIndex + pageSize where the
   *   indexes refer to index from server point of view (not index in local list)
   * @param startIndex 
   * @param pageSize
   * @return sublist of surveyResponses from startIndex to startIndex + pageSize (or to
   *   the end of the list if the number of remaining responses is less than pageSize.)
   */
  public List<SurveyResponse> getResponses(int startIndex, int pageSize) {
    int localStartIndex = startIndex - this.startIndex;
    int localEndIndex = Math.min(localStartIndex + pageSize, this.getNumResponsesLoaded());
    // TODO: handle case where diff is negative (meaning requested data is not loaded)
    return this.surveyResponses.subList(localStartIndex, localEndIndex);
  }
  
  /**
   * Saves defensive copy of params used to fetch this data. Useful for testing
   *   whether a data request can be served from the data in this object instead
   *   of going to the server 
   * @param params
   */
  public void setParams(SurveyResponseReadParams params) {
    this.params = new SurveyResponseReadParams(params);
  }
  
  /**
   * @return campaign urn of all responses in this set or null if set is empty
   */
  public String getCampaignUrn() {
    String urn = null;
    if (this.surveyResponses != null && !this.surveyResponses.isEmpty()) {
      urn = this.surveyResponses.get(0).getCampaignId();
    }
    return urn;
  }
  
  /**
   * @return SurveyResponseReadParams object used in the api query that filled this object
   */
  public SurveyResponseReadParams getParams() {
    return this.params;
  }
  
  /**
   * @param params SurveyResponseReadParams
   * @param startIndex
   * @param pageSize
   * @return True if data loaded in this data structure matches all user-defined search filters
   *   in params and includes the range of reponses from startIndex to startIndex + pageSize
   */
  public boolean hasRequestedData(SurveyResponseReadParams params, int startIndex, int pageSize) {
    // GOTCHA: if new filters are added to view they need to be added here too
    return userListMatches(params) &&
           surveyListMatches(params) &&
           startDateMatches(params) &&
           endDateMatches(params) &&
           privacyStateMatches(params) &&
           hasRequestedRange(startIndex, pageSize);
  }

  // helper function for hasRequestedData
  private boolean hasRequestedRange(int startIndex, int pageSize) {
    boolean firstItemIsLoaded = startIndex >= this.startIndex; // first requested item is loaded
    int lastIndexRequested = startIndex + pageSize;
    int lastIndexLoaded = this.getLastIndexLoaded();
    boolean lastItemIsLoaded = (lastIndexRequested <= lastIndexLoaded) || // requested range is loaded or
                               lastIndexLoaded == this.totalResponses; // there are no more to load
    return firstItemIsLoaded && lastItemIsLoaded;
  }

  // helper function for hasRequestedData
  private boolean startDateMatches(SurveyResponseReadParams params) {
    if (this.params == null || params == null) return false;
    if (this.params.startDate_opt == null && params.startDate_opt == null) return true;
    return this.params.startDate_opt != null && this.params.startDate_opt.equals(params.startDate_opt);
  }

  // helper function for hasRequestedData
  private boolean endDateMatches(SurveyResponseReadParams params) {
    if (this.params == null || params == null) return false;
    if (this.params.endDate_opt == null && params.endDate_opt == null) return true;
    return this.params.endDate_opt != null && this.params.endDate_opt.equals(params.endDate_opt);
  }

  // helper function for hasRequestedData
  private boolean privacyStateMatches(SurveyResponseReadParams params) {
    if (this.params == null || params == null) return false;
    if (this.params.privacyState_opt == null && params.privacyState_opt == null) return true;
    return this.params.privacyState_opt != null && this.params.privacyState_opt.equals(params.privacyState_opt);
  }
  
  // helper function for hasRequestedData
  private boolean userListMatches(SurveyResponseReadParams params) {
    if (this.params == null || params == null) return false;
    if (this.params.userList == null && params.userList == null) return true;
    return this.params.userList != null && this.params.userList.equals(params.userList);
  }
  
  // helper function for hasRequestedData
  private boolean surveyListMatches(SurveyResponseReadParams params) {
    if (this.params == null || params == null) return false;
    if (this.params.surveyIdList_opt == null && params.surveyIdList_opt == null) return true;
    return this.params.surveyIdList_opt != null && this.params.surveyIdList_opt.equals(params.surveyIdList_opt);
  }
  
  // FIXME: list will be the wrong size since there's no way to know how many responses have photos
  public List<SurveyResponse> getResponsesWithPhotos(int startIndex, int pageSize) {
    List<SurveyResponse> responsesWithPhotos = new ArrayList<SurveyResponse>();
    for (SurveyResponse response : this.getResponses(startIndex, pageSize)) {
      if (response.hasImage()) {
        responsesWithPhotos.add(response);
      }
    }
    return responsesWithPhotos;
  }
  
  public void setTotalResponseCount(int count) {
    this.totalResponses = count;
  }
  
  public int getTotalResponseCount() {
    return this.totalResponses;
  }
  
  public int getStartIndex() {
    return this.getStartIndex();
  }
  
  public int getLastIndexLoaded() {
    // calculated every time instead of saved since some responses could be deleted
    return this.startIndex + getNumResponsesLoaded();
  }
  
  public int getNumResponsesLoaded() {
    return this.surveyResponses != null ? this.surveyResponses.size() : 0;
  }
  
  public boolean isEmpty() {
    return this.surveyResponses == null || this.surveyResponses.isEmpty();
  }
  
  public void setCampaignName(String campaignName) {
    if (this.surveyResponses != null) {
      for (SurveyResponse response : this.surveyResponses) {
        response.setCampaignName(campaignName);
      }
    }
  }
  
  public void clear() {
    this.startIndex = 0;
    this.totalResponses = 0;
    if (this.surveyResponses != null) this.surveyResponses.clear();
    this.params = null;
  }
  
  public SurveyResponse getSurveyResponse(String responseKey) {
    SurveyResponse retval = null;
    if (this.surveyResponses != null) {
      for (SurveyResponse response : this.surveyResponses) {
        if (response.getResponseKey().equals(responseKey)) {
          retval = response;
          break;
        }
      }
    }
    return retval;
  }
  
  public void removeResponse(String responseKey) {
    for (int i = 0; i < this.surveyResponses.size(); i++) {
      if (this.surveyResponses.get(i).getResponseKey().equals(responseKey)) {
        this.surveyResponses.remove(i);
        this.totalResponses -= 1;
      }
    }
  }
  
  public void setPrivacyState(String responseKey, Privacy privacy) {
    SurveyResponse response = getSurveyResponse(responseKey);
    if (response != null) {
      response.setPrivacyState(privacy);
    }
  }
}
