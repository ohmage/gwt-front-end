package edu.ucla.cens.mobilize.client.presenter;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.view.AccountView;

public class AccountPresenter implements AccountView.Presenter, Presenter {
  private AccountView view;
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;

  public AccountPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  private void setEventHandlers() {
    this.view.getPasswordChangeButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        view.showPasswordChangeForm();        
      }
    });
    
    this.view.getPasswordChangeSubmitButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String userName = view.getUserName();
        String oldPassword = view.getOldPassword();
        String newPassword = view.getNewPassword();
        String newPasswordConfirm = view.getNewPasswordConfirm();
        if (!newPassword.equals(newPasswordConfirm)) {
          view.showPasswordMismatchError();
          return;
        }
        dataService.changePassword(userName, oldPassword, newPassword, new AsyncCallback<String>() {

          @Override
          public void onFailure(Throwable caught) {
            
            view.showError("There was a problem completing the password change request.");
          }

          @Override
          public void onSuccess(String result) {
            // FIXME: check for success/error
            view.showMessage("Password changed.");
            view.hidePasswordChangeForm();            
          }
        });
      }
    });
    
    this.view.getPasswordChangeCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.resetPasswordChangeForm();
        view.hidePasswordChangeForm();
      }
    });
  }
  
  @Override
  public void setView(AccountView view) {
    this.view = view;
    setEventHandlers();
  }
  
  @Override
  public void go(Map<String, String> params) {
    // shows details about logged in user if no username is given in params
    String userName = null;
    if (params.containsKey("username")) {
      userName = params.get("username");
    } else {
      userName = userInfo.getUserName();
    }
    fetchAndShowUserDetails(userName);
  }

  private void fetchAndShowUserDetails(String userName) {
    showUserDetails(this.userInfo);
    // FIXME: use real data
    /*
    this.dataService.fetchUserInfo(userName, new AsyncCallback<UserInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onSuccess(UserInfo result) {
        showUserDetails(result);
      }
    });*/
  }
  
  private void showUserDetails(UserInfo userInfo) {
    this.view.setUserName(userInfo.getUserName());
    this.view.setCanCreate(userInfo.canCreate());
    this.view.clearClassList();
    Map<String, String> userClasses = userInfo.getClasses();
    for (String classId : userClasses.keySet()) {
      this.view.addClass(classId, userClasses.get(classId));
    }
  }

}
