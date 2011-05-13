package edu.ucla.cens.mobilize.client.presenter;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.view.DocumentView;

public class DocumentPresenter implements Presenter {

  UserInfo userInfo;
  DataService dataService;
  EventBus eventBus;
  
  DocumentView view;
  
  public DocumentPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  @Override
  public void go(Map<String, List<String>> params) {
    
  }
  
  public void setView(DocumentView view) {
    this.view = view;
  }


}
