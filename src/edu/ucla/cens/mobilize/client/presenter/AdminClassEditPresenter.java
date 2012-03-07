package edu.ucla.cens.mobilize.client.presenter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserSearchParams;
import edu.ucla.cens.mobilize.client.event.ClassDataChangedEvent;
import edu.ucla.cens.mobilize.client.event.UserDataChangedEvent;
import edu.ucla.cens.mobilize.client.event.UserDataChangedEventHandler;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserSearchInfo;
import edu.ucla.cens.mobilize.client.ui.ConfirmDeleteDialog;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;
import edu.ucla.cens.mobilize.client.view.AdminClassEditView;

public class AdminClassEditPresenter implements Presenter {
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  private AdminClassEditView view;
  
  private List<String> allUsernames; // store all usernames to avoid excesssive data loads
  private Map<String, RoleClass> currentMemberList; // used to identify deleted users
  private static Logger _logger = Logger.getLogger(AdminClassEditPresenter.class.getName());
  
  public AdminClassEditPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
    bind();
  }
  
  private void bind() {
    // If user was added or deleted, clear user list so it will be reloaded on next use.
    eventBus.addHandler(UserDataChangedEvent.TYPE, new UserDataChangedEventHandler() {
      @Override
      public void onUserDataChanged(UserDataChangedEvent event) {
        allUsernames = null;
      }
    });
  }
  
  public void setView(AdminClassEditView view) {
    this.view = view;
    addEventHandlersToView();
  }
  
  private void addEventHandlersToView() {
    view.getBackLink().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
    
    // when user clicks Add Members button at the bottom of the user list
    view.getAddMembersButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (allUsernames != null) {
          List<String> usernames = getUsernamesMinusMemberList();
          view.setAddMembersPopupUserList(usernames);
          view.showAddMembersPopup();
        } else {
          fetchUsersAndShowAddMembersPopup();
        }
      }
    });
    
    // when popup is already open and user clicks Add Selected Members button in the dialog
    view.getAddMembersPopupAddSelectedUsersButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // get current list of users from the main form
        Map<String, RoleClass> usernameToRoleMap = view.getMembersAndRoles();
        // merge in users selected in the popup (where popup form values override existing values)
        usernameToRoleMap.putAll(view.getAddMembersPopupSelectedUsersAndRoles());
        // update list in main form
        view.setMemberList(usernameToRoleMap);
        // hide popup
        view.hideAddMembersPopup();
      }
    });
    
    view.getAddMembersPopupCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.hideAddMembersPopup();
      }
    });
    
    view.getSelectAllMembersCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        if (event.getValue() == true) {
          view.selectAllMembers();
        } else {
          view.unselectAllMembers();
        }
      }
    });
    
    view.getMembersPrivilegedButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.markSelectedMembersPrivileged();
      }
    });
    
    view.getMembersRestrictedButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.markSelectedMembersRestricted();
      }
    });
    
    view.getMembersRemoveButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.markSelectedMembersRemoved();
      }
    });
    
    view.getSaveButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (validateClassInfo()) {
          if (view.isEdit()) {
            updateClassInfo();
          } else { // new class
            createClass();
          }
        }
      }
    });
    
    view.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.newItem(HistoryTokens.adminClassList());
      }
    });
    
    view.getDeleteClassButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String className = view.getClassName();
        final String classUrn = view.getClassUrn();
        ConfirmDeleteDialog.show("Are you sure you want to delete " + className + "?", new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            deleteClass(classUrn);
          }
        });
      }
    });
  }
  

  @Override
  public void go(Map<String, String> params) {
    view.resetForm();
    // if params contains cid, set view for edit, otherwise set for create
    if (params.containsKey("cid")) {
      view.showFieldsForEdit();
      fetchAndShowClass(params.get("cid"));
    } else {
      view.showFieldsForCreate();
    }
  }
  
  private void fetchAndShowClass(final String classUrn) {
    dataService.fetchClassDetail(classUrn, new AsyncCallback<ClassInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem fetching info for class " + classUrn,
                          caught.getMessage());
      }

      @Override
      public void onSuccess(ClassInfo result) {
        view.setClassUrn(result.getClassId());
        view.setClassName(result.getClassName());
        view.setDescription(result.getDescription());
        // save current member list so it can be used later to identify deleted members
        currentMemberList = result.getUsernameToRoleMap();
        view.setMemberList(currentMemberList);
      }
    });
  }
  
  private void fetchUsersAndShowAddMembersPopup() {
    view.clearAddMembersPopup();
    view.showAddMembersPopup();
    view.showAddMembersPopupWaitIndicator();
    dataService.fetchUserSearchResults(new UserSearchParams(), new AsyncCallback<List<UserSearchInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        view.hideAddMembersPopupWaitIndicator();
        view.hideAddMembersPopup();
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem fetching the user list.", caught.getMessage());
      }

      @Override
      public void onSuccess(List<UserSearchInfo> result) {
        // clear previous data
        if (allUsernames != null) {
          allUsernames.clear();
        } else {
          allUsernames = new ArrayList<String>();
        }
        
        // build a list of usernames and save it so it won't have to be reloaded
        for (UserSearchInfo user: result) {
          allUsernames.add(user.getUsername());
        }
        
        List<String> usernames = getUsernamesMinusMemberList();
        view.setAddMembersPopupUserList(usernames);
        view.hideAddMembersPopupWaitIndicator();
      }
    });
  }
  
  private List<String> getUsernamesMinusMemberList() {
    // NOTE: current members are fetched from the view and not this.currentMembers
    //   because this.currentMembers contains list of members associated with this
    //   class in the db, but we want to compare against members in the display,
    //   which may have been edited since the class data was loaded
    Map<String, RoleClass> currentMembers = view.getMembersAndRoles();
    List<String> usernames = new ArrayList<String>();
    for (String username : this.allUsernames) {
      if (!currentMembers.containsKey(username)) {
        usernames.add(username);
      }
    }
    Collections.sort(usernames);
    return usernames;
  }
  
  private boolean validateClassInfo() {
    view.clearValidationErrors(); // clear previous errors, if any
    boolean allFieldsAreValid = true;
    String classUrn = view.getClassUrn();
    if (classUrn == null || classUrn.isEmpty()) {
      allFieldsAreValid = false;
      view.markClassUrnInvalid("Urn is required."); // TODO: regex and give example?
    }
    String className = view.getClassName();
    if (className == null || className.isEmpty()) {
      allFieldsAreValid = false;
      view.markClassNameInvalid("Class name is required.");
    }
    return allFieldsAreValid;
  }
  
  // gets new info from the form and uses dataservice to update it
  private void updateClassInfo() {
    final String classUrn = view.getClassUrn();
    String className = view.getClassName();
    String description = view.getDescription();
    ClassUpdateParams params = new ClassUpdateParams();
    params.classId = classUrn;
    params.className = className;
    params.description_opt = description;

    Map<String, RoleClass> members = view.getMemberList();
    List<String> selectedMembers = new ArrayList<String>(members.keySet());
    
    // users that appeared in the original list but are no longer shown in gui are removed from class
    Set<String> originalMembers = this.currentMemberList.keySet();
    params.usersToRemove_opt = CollectionUtils.setDiff(originalMembers, selectedMembers);

    // users that were not in original members or had a different role are added/updated
    for (String username : members.keySet()) {
      if (!members.get(username).equals(this.currentMemberList.get(username))) { // equals(null) if user is new 
        params.usersToAdd_opt.put(username, members.get(username));
      }
    }
    
    dataService.updateClass(params, new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem updating class with urn: " + classUrn,
                          caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        view.resetForm();
        eventBus.fireEvent(new ClassDataChangedEvent());
        History.newItem(HistoryTokens.adminClassDetail(classUrn));
      }
    });

  }
  
  private void createClass() {
    ClassUpdateParams params = new ClassUpdateParams();
    final String classUrn = view.getClassUrn();
    params.classId = classUrn;
    params.className = view.getClassName();
    params.description_opt = view.getDescription();
    // NOTE(11/2011): class create api does not include users
    dataService.createClass(params, new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem saving the new class.", caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        view.resetForm();
        eventBus.fireEvent(new ClassDataChangedEvent());
        History.newItem(HistoryTokens.adminClassDetail(classUrn));
      }
    });
  }
  
  private void deleteClass(final String classUrn) {
    dataService.deleteClass(classUrn, new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem deleting " + classUrn, caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        eventBus.fireEvent(new ClassDataChangedEvent());
        History.newItem(HistoryTokens.adminClassList());
      }
    });
  }
}
