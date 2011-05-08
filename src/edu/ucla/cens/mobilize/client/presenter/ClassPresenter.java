package edu.ucla.cens.mobilize.client.presenter;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.view.ClassView;

public class ClassPresenter implements ClassView.Presenter, Presenter {
  private ClassView view;
  private UserInfo user;
  private DataService dataService;
  private EventBus eventBus;
  
  private ClassInfo oldClassInfo;
  
  // logging
  private static Logger _logger = Logger.getLogger(ClassPresenter.class.getName());

  public ClassPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.user = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  public void setView(ClassView classView) {
    this.view = classView;
    bind(); // wire up event handlers
  }
  
  private void bind() {
    // submit edit form when save button is clicked
    this.view.getEditFormSubmitButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        submitEditForm();
      }
    });

    // clear edit form and go back one token when edit cancel button is clicked
    this.view.getEditFormCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.clearEditForm();
        History.back();
      }
    });
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
    dataService.fetchClassDetail(classId, new AsyncCallback<ClassInfo>() {

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
    dataService.fetchClassDetail(classId, new AsyncCallback<ClassInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.fine(caught.getMessage());
        view.showError("There was a problem retrieving the class data.");        
      }

      @Override
      public void onSuccess(ClassInfo result) {
        oldClassInfo = result;
        view.showEditForm(result);
      }
    });
  }

  // fetch and display all classes visible to user (where a class is
  // visible if it's listed in the user's userinfo)
  // FIXME: should supervisors be able to see all classes in a school instead?
  private void fetchAndShowClasses() { 
    List<String> classIdList = new ArrayList<String>();
    classIdList.addAll(this.user.getClassIds());
    dataService.fetchClassList(classIdList, new AsyncCallback<List<ClassInfo>>() {

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
  
  private void submitEditForm() {
    final ClassUpdateParams params = getUpdateParamValuesFromForm();
    dataService.updateClass(params, new AsyncCallback<String>() {

      @Override
      public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onSuccess(String result) {
        view.showMsg("Successfully updated class: " + params.classId);
      }
    });
  }
  
  private ClassUpdateParams getUpdateParamValuesFromForm() {
    // get user input
    List<String> selectedMembers = this.view.getMembers();
    List<String> selectedPrivilegedMembers = this.view.getPrivilegedMembers();
    String description = this.view.getDescription();
    // compare input to previous values to see what changed
    ClassUpdateParams params = new ClassUpdateParams();
    params.classId = this.view.getClassId();    
    if (!oldClassInfo.getDescription().equals(description)) {
      params.description_opt = description;
    }
    Set<String> oldMembers = oldClassInfo.getMembers().keySet();
    // if users are selected in the form but weren't in the old members, add them
    params.usersToAdd_opt.addAll(CollectionUtils.setDiff(selectedMembers, oldMembers));
    // if users were in the old member list but aren't in the form, remove them
    params.usersToRemove_opt.addAll(CollectionUtils.setDiff(oldMembers, selectedMembers));
    
    Set<String> oldPrivilegedMembers = oldClassInfo.getPrivilegedMembers().keySet();
    // if users are selected in the form but weren't in the old members, add them
    params.usersToAddAsPrivileged_opt.addAll(CollectionUtils.setDiff(selectedPrivilegedMembers, oldPrivilegedMembers));
    // note: just one list for removing both privileged and restricted users
    params.usersToRemove_opt.addAll(CollectionUtils.setDiff(oldPrivilegedMembers, selectedPrivilegedMembers));
    return params;
  }


}
