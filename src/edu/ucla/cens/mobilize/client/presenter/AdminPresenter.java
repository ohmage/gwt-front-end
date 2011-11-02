package edu.ucla.cens.mobilize.client.presenter;

import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.view.AdminView;

public class AdminPresenter implements AdminView.Presenter, Presenter {
  private AdminView view;
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  
  public AdminPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  private void addEventHandlersToView() {
  }

  @Override
  public void setView(AdminView view) {
    this.view = view;
    addEventHandlersToView();
  }
  
  @Override
  public void go(Map<String, String> params) {
    // nothing to load 
  }

}
