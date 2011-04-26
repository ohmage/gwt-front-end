package edu.ucla.cens.mobilize.client.presenter;


import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.view.ClassView;

public class ClassPresenter implements ClassView.Presenter, Presenter {
  private ClassView view;
  private UserInfo user;
  private DataService dataService;
  private EventBus eventBus;
  
  // logging
  private static Logger _logger = Logger.getLogger(ClassPresenter.class.getName());

  public ClassPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.user = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  public void setView(ClassView classView) {
    this.view = classView;
  }
  
  @Override
  public void go(Map<String, List<String>> params) {
    // hide any leftover notifications
    this.view.hideMsg();
    
    // get subview from url params
    if (params.isEmpty()) {
      this.fetchAndShowClasses();
    } else if (params.get("v").get(0).equals("detail") && params.containsKey("id")) {
      // anything after first id is ignored
      this.fetchAndShowClassDetail(params.get("id").get(0));
    } else if (params.get("v").get(0).equals("edit") && params.containsKey("id")) {
      // anything after first id is ignored
      this.fetchAndShowClassEdit(params.get("id").get(0));
    } else {
      // unrecognized view - do nothing
      // TODO: log?
    }
  }

  private void fetchAndShowClassDetail(String classId) {
    dataService.fetchClass(classId, new AsyncCallback<ClassInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.fine(caught.getMessage());
        view.showError("There was a problem retrieving the class data.");
      }

      @Override
      public void onSuccess(ClassInfo result) {
        view.showDetail(result);
      }
    });
    
  }
  
  private void fetchAndShowClassEdit(String classId) {
    dataService.fetchClass(classId, new AsyncCallback<ClassInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.fine(caught.getMessage());
        view.showError("There was a problem retrieving the class data.");        
      }

      @Override
      public void onSuccess(ClassInfo result) {
        view.showEditForm(result);
      }
    });
  }

  private void fetchAndShowClasses() {
    fetchAndShowClasses("");
  }
  
  private void fetchAndShowClasses(String schoolId) {
    dataService.fetchClassList(schoolId, new AsyncCallback<List<ClassInfo>>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.fine(caught.getMessage());
        view.showError("There was a problem loading the class list.");
      }

      @Override
      public void onSuccess(List<ClassInfo> result) {
        view.showList(result);
      }
    });
  }

  @Override
  public void onFilterChange() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onAddUsersClick() {
    // TODO Auto-generated method stub
    // open addusers dialog
  }

  @Override
  public void onAddUsersSubmit() {
    // TODO Auto-generated method stub
    // get list of users from dialog
    // send list of users to dataservice
    // on success, refresh, goto detail view and show users added message
  }
  
  @Override
  public void onDeleteUserClick() {
    // TODO Auto-generated method stub
    // open confirm delete dialog
  }

  @Override
  public void onDeleteUserConfirm() {
    // TODO Auto-generated method stub
    // send delete request to dataservice
    // on success, refresh, goto detail view and show deleted message
  }



}
