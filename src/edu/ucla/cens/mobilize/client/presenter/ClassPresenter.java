package edu.ucla.cens.mobilize.client.presenter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.view.ClassView;

public class ClassPresenter implements ClassView.Presenter, Presenter {
  private ClassView view;
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  
  private ClassInfo oldClassInfo;
  
  // logging
  private static Logger _logger = Logger.getLogger(ClassPresenter.class.getName());

  public ClassPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
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
    
    this.view.getEditFormAddMembersButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // FIXME: hard coded users because we don't have this api yet
        // Update(2011/06/05): There are privacy concerns about allowing someone
        //   to fetch a list of all users in the system. Waiting for a decision about it.
        List<String> hardCodedUsers = Arrays.asList(new String[] {
            "user.adv.supa", "user.adv.upa", "user.adv.spa", "user.adv.sup", 
            "user.adv.sa", "user.adv.ua", "user.adv.su", "user.adv.sp", 
            "user.adv.sa", "user.adv.up", "user.adv.a", "user.adv.p", 
            "user.adv.u", "user.adv.s"
        });
        _logger.warning("FIXME: hard-coded users in class tab");
        view.showEditFormAddMembersDialog(hardCodedUsers);
      }
    });
    
  }
  
  @Override
  public void go(Map<String, String> params) {
    // hide any leftover notifications
    this.view.hideMsg();
    
    // get subview from url params
    if (params.isEmpty()) {
      this.fetchAndShowClasses();
    } else if (params.get("v").equals("detail") && params.containsKey("id")) {
      // anything after first id is ignored
      this.fetchAndShowClassDetail(params.get("id"));
    } else if (params.get("v").equals("edit") && params.containsKey("id")) {
      // anything after first id is ignored
      this.fetchAndShowClassEdit(params.get("id"));
    } else {
      _logger.warning("Unrecognized subview: " + params.get("v"));
    }
  }

  private void fetchAndShowClassDetail(String classId) {
    dataService.fetchClassDetail(classId, new AsyncCallback<ClassInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.fine(caught.getMessage());
        view.showListSubview();
        view.showError("There was a problem retrieving the class data.");
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(ClassInfo result) {
        view.setDetail(result, result.userCanEdit(userInfo.getUserName()));
        view.showDetailSubview();
      }
    });
    
  }
  
  private void fetchAndShowClassEdit(String classId) {
    dataService.fetchClassDetail(classId, new AsyncCallback<ClassInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.fine(caught.getMessage());
        view.showError("There was a problem retrieving the class data.");
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(ClassInfo result) {
        oldClassInfo = result;
        view.setEdit(result);
        view.showEditSubview();
      }
    });
  }

  // fetch and display all classes visible to user (where a class is
  // visible if it's listed in the user's userinfo)
  // FIXME: should supervisors be able to see all classes in a school instead?
  // FIXME: we get both class urn and class name in user info now, so fetch 
  //   may not be needed here
  private void fetchAndShowClasses() { 
    List<String> classIdList = new ArrayList<String>();
    classIdList.addAll(this.userInfo.getClassIds());
    dataService.fetchClassList(classIdList, new AsyncCallback<List<ClassInfo>>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.fine(caught.getMessage());
        view.setList(null);
        view.showListSubview();
        view.showError("There was a problem loading the class list.");
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(List<ClassInfo> result) {
        view.setList(result);
        view.showListSubview();
      }
    });
  }
  
  private void submitEditForm() {
    final ClassUpdateParams params = getUpdateParamValuesFromForm();
    dataService.updateClass(params, new AsyncCallback<String>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.fine(caught.getMessage());
        view.showError("Update failed.");
        AwErrorUtils.logoutIfAuthException(caught);
      }

      @Override
      public void onSuccess(String result) {
        view.showListSubview();
        view.showMsg("Successfully updated class: " + params.classId);
      }
    });
  }
  
  private ClassUpdateParams getUpdateParamValuesFromForm() {
    // get user input
    List<String> selectedMembers = this.view.getMembers();
    String description = this.view.getDescription();
    // compare input to previous values to see what changed
    ClassUpdateParams params = new ClassUpdateParams();
    params.classId = this.view.getClassId();    
    if (!oldClassInfo.getDescription().equals(description)) {
      params.description_opt = description;
    }
    List<String> oldMembers = oldClassInfo.getMemberLogins();
    // if users are selected in the form but weren't in the old members, add them
    params.usersToAdd_opt.addAll(CollectionUtils.setDiff(selectedMembers, oldMembers));
    // if users were in the old member list but aren't in the form, remove them
    params.usersToRemove_opt.addAll(CollectionUtils.setDiff(oldMembers, selectedMembers));
    
    return params;
  }


}
