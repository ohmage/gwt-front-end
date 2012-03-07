package edu.ucla.cens.mobilize.client.presenter;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserCreateParams;
import edu.ucla.cens.mobilize.client.event.UserDataChangedEvent;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.view.AdminUserCreateView;

public class AdminUserCreatePresenter implements Presenter {
  AdminUserCreateView view;  
  UserInfo userInfo;
  DataService dataService;
  EventBus eventBus;

  public AdminUserCreatePresenter(UserInfo userInfo, 
                                  DataService dataService,
                                  EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }

  @Override
  public void go(Map<String, String> params) {
    view.resetForm();
  }

  public void setView(AdminUserCreateView view) {
    this.view = view;
    addEventHandlersToView();
  }
  
  public void addEventHandlersToView() {
    view.getBackLink().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
    
    view.getSaveButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        boolean allFieldsAreValid = validateFormFields();
        if (allFieldsAreValid) {
          createUser();
        }
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
  
  private void createUser() {
    UserCreateParams params = new UserCreateParams();
    final String username = view.getUsername();
    params.username = username;
    params.password = view.getPassword();
    params.enabled = view.getEnabledFlag();
    params.admin = view.getAdminFlag();
    params.newAccount_opt = view.getNewAccountFlag();
    params.canCreateCampaigns_opt = view.getCanCreateCampaignsFlag();
    dataService.createUser(params, new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem creating the user.", caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        eventBus.fireEvent(new UserDataChangedEvent());
        view.resetForm();
        History.newItem(HistoryTokens.adminUserDetail(username));
      }
    });
  }
  
  private boolean validateFormFields() {
    // clear any previous validation error messages
    view.clearInvalidFieldMarkers();
    
    boolean allFieldsAreValid = true;
    String username = view.getUsername();
    String password = view.getPassword();
    String passwordConfirm = view.getPasswordConfirm();
    
    if (username.isEmpty()) {
      view.markUsernameInvalid("Username is required.");
      allFieldsAreValid = false;
    }
    
    if (password.isEmpty()) {
      view.markPasswordInvalid("Password is required.");
      allFieldsAreValid = false;
    }
    
    if (passwordConfirm.isEmpty()) {
      view.markPasswordConfirmInvalid("Please re-enter the password.");
      allFieldsAreValid = false;
    }
    
    if (!password.equals(passwordConfirm)) {
      view.markPasswordConfirmInvalid("Passwords do not match.");
      allFieldsAreValid = false;
    }
    
    return allFieldsAreValid;
  }
}
