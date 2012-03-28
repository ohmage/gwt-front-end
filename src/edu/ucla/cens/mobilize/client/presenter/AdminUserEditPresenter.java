package edu.ucla.cens.mobilize.client.presenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserUpdateParams;
import edu.ucla.cens.mobilize.client.event.UserDataChangedEvent;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserSearchInfo;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.InputValidationUtils;
import edu.ucla.cens.mobilize.client.view.AdminUserEditView;

public class AdminUserEditPresenter implements Presenter {
  AdminUserEditView view;  
  UserInfo userInfo;
  DataService dataService;
  EventBus eventBus;

  public AdminUserEditPresenter(UserInfo userInfo, 
                                DataService dataService,
                                EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }

  @Override
  public void go(Map<String, String> params) {
    // The same form is used for creating and editing users. If a user id
    // is given in the params, then it's an edit.
    if (params.containsKey("uid")) { 
      fetchUserAndShowEditForm(params.get("uid"));
    } else { // no user id so it's a new user 
      History.newItem(HistoryTokens.adminUserCreate());
    }
  }

  public void setView(AdminUserEditView view) {
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
    
    view.getSaveChangesButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        boolean allFieldsAreValid = validateFormFields();
        if (allFieldsAreValid) {
          saveChanges();
        }
      }
    });
    
    view.getRemovePersonalInfoButton().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          view.showConfirmRemovePersonalInfo(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
              removePersonalInfo(view.getUsername());
            }
          });
        }
      });
    
    view.getDeleteUserButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.showConfirmDelete(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            deleteUser(view.getUsername());
          }
        });
      }
    });
    
    view.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.resetForm();
        History.back();
      }
      
    });
  }

  private void fetchUserAndShowEditForm(final String username) {
    view.resetForm();
 
    dataService.fetchUserSearchInfo(username, new AsyncCallback<UserSearchInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("There was a problem fetching personal info for user " + username,
                          caught.getMessage());
      }

      @Override
      public void onSuccess(UserSearchInfo result) {
        // copy user's info into the form
        view.setUsername(result.getUsername());
        view.setFirstName(result.getFirstName());
        view.setLastName(result.getLastName());
        view.setOrganization(result.getOrganization());
        view.setPersonalId(result.getPersonalId());
        view.setEmail(result.getEmail());
        view.setCanCreateCampaignsFlag(result.canCreateCampaigns());
        view.setEnabledFlag(result.isEnabled());
        view.setIsAdminFlag(result.isAdmin());
      }
    });
  }
  
  private void removePersonalInfo(final String username) {
	  dataService.removePersonalUserInfo(username, new AsyncCallback<String>() {
		  @Override
		  public void onFailure(Throwable caught) {
			  ErrorDialog.show("There was a problem delete personal info for the user.", caught.getMessage());
		  }

		  @Override
		  public void onSuccess(String result) {
			  view.resetForm();
			  History.newItem(HistoryTokens.adminUserDetail(username));
		  }
	  });
  }
  
  private void deleteUser(final String username) {
    dataService.deleteUsers(Arrays.asList(username), new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        ErrorDialog.show("There was a problem deleting the user.", caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        eventBus.fireEvent(new UserDataChangedEvent());
        HistoryTokens.adminUserList();
      }
    });
  }

  // validates form fields, marking those that are invalid
  private boolean validateFormFields() {
    // clear any previous validation error messages
    view.clearInvalidFieldMarkers();
    
    boolean allFieldsAreValid = true;
    String firstName = view.getFirstName();
    String lastName = view.getLastName();
    String organization = view.getOrganization();
    String personalId = view.getPersonalId();
    String email = view.getEmail();
    
    boolean allPersonalInfoIsRequired = false;
    
    // if any of the personal info fields is given, they must all be given
    if (!firstName.isEmpty() || !lastName.isEmpty() || !organization.isEmpty() || !personalId.isEmpty()) {
      allPersonalInfoIsRequired = true;
    }
    
    // if an email is given, email must be valid format
    if (!email.isEmpty()) {
      if (InputValidationUtils.isValidEmail(email) == false) {
    	  view.markEmailInvalid("E-mail address is invalid");
    	  allFieldsAreValid = false;
      }
    }
    
    if (allPersonalInfoIsRequired) {
      if (firstName.isEmpty()) {
        view.markFirstNameInvalid("First name is required.");
        allFieldsAreValid = false;
      }
      if (lastName.isEmpty()) {
        view.markLastNameInvalid("Last name is required.");
        allFieldsAreValid = false;
      }
      if (personalId.isEmpty()) {
        view.markPersonalIdInvalid("Personal id is required.");
        allFieldsAreValid = false;
      }
      if (organization.isEmpty()) {
        view.markOrganizationInvalid("Organization is required.");
        allFieldsAreValid = false;
      }
    }
    
    return allFieldsAreValid;
  }
  
  private void saveChanges() {
    final String username = view.getUsername();
    UserUpdateParams params = new UserUpdateParams();
    params.username = view.getUsername();
    params.firstName_opt = view.getFirstName();
    params.lastName_opt = view.getLastName();
    params.organization_opt = view.getOrganization();
    params.personalId_opt = view.getPersonalId();
    
    // Remove personal info if all 4 personal fields are empty
    if (view.getFirstName().isEmpty() && view.getLastName().isEmpty() && view.getOrganization().isEmpty() && view.getPersonalId().isEmpty())
    	params.deletePersonalInfo_opt = true;
    
    params.email_opt = view.getEmail();
    params.enabled_opt = view.getEnabledFlag();
    params.canCreateCampaigns_opt = view.getCanCreateCampaignsFlag();
    params.isAdmin_opt = view.getIsAdminFlag();
    
    dataService.updateUser(params, new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        ErrorDialog.show("Save failed.", caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        // NOTE: UserDataChangedEvent is not fired here b/c (as of Mar 2012)
        //   only create/delete require refreshing parts of the app.
        view.resetForm();
        History.newItem(HistoryTokens.adminUserDetail(username));
      }
    });
  }
}
