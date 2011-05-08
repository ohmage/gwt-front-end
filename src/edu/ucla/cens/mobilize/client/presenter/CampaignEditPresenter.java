package edu.ucla.cens.mobilize.client.presenter;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.UserInfo;

public class CampaignEditPresenter implements Presenter {
  UserInfo userInfo;
  DataService dataService;
  EventBus eventBus;
  
  public CampaignEditPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  @Override
  public void go(Map<String, List<String>> params) {
    // TODO: load campaign if edit or
    // get create vs edit
    // if edit, get campaign id from params
    // get users classes from userInfo
    // 
  }

  public void onSave() {
    // TODO Auto-generated method stub
     
  }

  public void onSaveComplete() {
    // TODO Auto-generated method stub
    
  }

  public void onCancel() {
    // TODO Auto-generated method stub
    
  }

  public void onAddClass(String classId) {
    // TODO Auto-generated method stub
    
  }

  public void onRemoveClass(String classId) {
    // TODO Auto-generated method stub
    
  }

}
