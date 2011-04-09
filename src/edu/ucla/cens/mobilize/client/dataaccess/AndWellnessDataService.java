package edu.ucla.cens.mobilize.client.dataaccess;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.dataaccess.request.DataPointFilterParams;
import edu.ucla.cens.mobilize.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.model.CampaignConciseInfo;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.DataPointAwData;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.model.UserInfo;

public class AndWellnessDataService implements DataService {

  // TODO: make sure to throw exception if request fails because of logout

  @Override
  public void fetchAuthorizationToken(String userName, String password,
      AsyncCallback<AuthorizationTokenQueryAwData> callback) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void fetchUserInfo(String username, AsyncCallback<UserInfo> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchCampaignList(Map<String, List<String>> params,
      AsyncCallback<List<CampaignConciseInfo>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchCampaignDetail(String campaignId,
      AsyncCallback<CampaignDetailedInfo> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchCampaignDetailList(List<String> campaignIds,
      AsyncCallback<List<CampaignDetailedInfo>> callback) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public CampaignDetailedInfo getCampaignDetail(String campaignId) {
    // TODO Auto-generated method stub
    return null;
  }
  
  
  @Override
  public void deleteCampaign(String campaignId,
      AsyncCallback<ResponseDelete> asyncCallback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchDataPoints(String campaignId, DataPointFilterParams params,
      AsyncCallback<List<DataPointAwData>> callback) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void fetchSurveyResponses(String campaignId,
      DataPointFilterParams params, AsyncCallback<List<SurveyResponse>> callback) {
    // TODO Auto-generated method stub
    
  }

  
}
