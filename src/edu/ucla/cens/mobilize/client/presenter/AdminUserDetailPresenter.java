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
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserSearchInfo;
import edu.ucla.cens.mobilize.client.ui.ConfirmDeleteDialog;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.ui.SuccessDialog;
import edu.ucla.cens.mobilize.client.ui.WaitIndicator;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.view.AdminUserDetailView;

public class AdminUserDetailPresenter implements Presenter {
  AdminUserDetailView view;  
  UserInfo userInfo;
  DataService dataService;
  EventBus eventBus;
  
  public AdminUserDetailPresenter(UserInfo userInfo, 
                                  DataService dataService,
                                  EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }

  public void setView(AdminUserDetailView view) {
    this.view = view;
    addEventHandlersToView();
  }

  @Override
  public void go(Map<String, String> params) {
    if (params.containsKey("uid")) {
      String username = params.get("uid");
      fetchAndShowUserInfo(username);
    }
  }
  
  private void addEventHandlersToView() {
    view.getActionLinkEnableUser().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        enableUser(view.getUsername());
      }
    });
    
    view.getActionLinkDisableUser().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        disableUser(view.getUsername());
      }
    });
    
    view.getActionLinkDeleteUser().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final String username = view.getUsername();
        String msg = "Are you sure you want to delete " + username + "?";
        ConfirmDeleteDialog.show(msg,  new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            deleteUser(username); 
          }
        });
      }
    });
    
    view.getActionLinkResetPassword().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        showPasswordChangeDialog(view.getUsername());
      }
    });
  }

  private void fetchAndShowUserInfo(final String username) {
    if (username == null || username.isEmpty()) return;
    dataService.fetchUserSearchInfo(username, new AsyncCallback<UserSearchInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem fetching details for user " + username,
                       caught.getMessage());
      }

      @Override
      public void onSuccess(UserSearchInfo result) {
        // sets personal info
        view.setUsername(result.getUsername());
        view.setFirstName(result.getFirstName());
        view.setLastName(result.getLastName());
        view.setOrganization(result.getOrganization());
        view.setPersonalId(result.getPersonalId());
        view.setEmail(result.getEmail());
        
        // NOTE: makes a link of class urn instead of class name since name is not available
        //   in the user search api. 
        // TODO: fetch info about classes so you can show the class name here instead of urn
        Map<String, String> classUrnMap = new HashMap<String, String>();
        for (String classUrn : result.getClassUrns()) {
          classUrnMap.put(classUrn, classUrn);
        }
        view.setClassList(classUrnMap);

        // NOTE: makes a link from campaign urn instead of name since name is not available
        //   in the user search api. 
        // TODO: fetch info about campaigns so you can show the name here instead of urn
        Map<String, String> campaignUrnMap = new HashMap<String, String>();
        for (String campaignUrn : result.getCampaignUrns()) {
          campaignUrnMap.put(campaignUrn, campaignUrn);
        }
        view.setCampaignList(campaignUrnMap);
        view.setCanCreateFlag(result.canCreateCampaigns());
        view.setEnabledFlag(result.isEnabled());
        view.setAdminFlag(result.isAdmin());
      }
    });
  }

  private void enableUser(final String username) {
    dataService.enableUser(username, new AsyncCallback<String>(){
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem enabling " + username, caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        view.setEnabledFlag(true);
      }
    });
  }
  
  private void disableUser(final String username) {
    dataService.disableUser(username, new AsyncCallback<String>(){
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem disabling " + username, caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        view.setEnabledFlag(false);
      }
    });    
  }
  
  private void deleteUser(final String username) {
    dataService.deleteUsers(Arrays.asList(username), new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem deleting " + username, caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        History.newItem(HistoryTokens.adminUserList());
      }
    });
  }
  
  private void showPasswordChangeDialog(String username) {
    view.showPasswordChangeDialog(username, new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        changePassword();
      }
    });
  }
  
  private void changePassword() {
    String usernameLoggedInUser = userInfo.getUserName();
    String passwordLoggedInUser = view.passwordChangeGetAdminPassword();
    String usernameThatOwnsPassword = view.getUsername();
    String newPassword = view.passwordChangeGetNewPassword();
    WaitIndicator.show();
    dataService.adminChangePassword(usernameLoggedInUser, 
                                    passwordLoggedInUser, 
                                    usernameThatOwnsPassword, 
                                    newPassword,
                                    new AsyncCallback<String>() {

                                      @Override
                                      public void onFailure(Throwable caught) {
                                        WaitIndicator.hide();
                                        AwErrorUtils.logoutIfAuthException(caught);
                                        view.showError("Password change failed.", caught.getMessage());
                                      }

                                      @Override
                                      public void onSuccess(String result) {
                                        WaitIndicator.hide();
                                        if("".equals(result)) {
                                        	SuccessDialog.show("Password change successful.");
                                        } else {
                                        	ErrorDialog.show(result);
                                        }
                                      }
    });
  }
}
