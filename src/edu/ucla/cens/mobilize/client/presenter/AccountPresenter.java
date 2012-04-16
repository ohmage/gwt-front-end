package edu.ucla.cens.mobilize.client.presenter;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
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

	private void setViewEventHandlers() {
		this.view.getPasswordChangeButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				view.showPasswordChangeForm();        
			}
		});

		this.view.setPasswordChangeSubmitHandler(new SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				view.disablePasswordChangeForm();
				view.showWaitIndicator();
				String userName = view.getUserName();
				String oldPassword = view.getOldPassword();
				String newPassword = view.getNewPassword();
				String newPasswordConfirm = view.getNewPasswordConfirm();
				if (!newPassword.equals(newPasswordConfirm)) {
					view.resetPasswordChangeForm();
					view.enablePasswordChangeForm();
					view.hideWaitIndicator();
					view.showError("Password change failed.", 
							"Passwords do not match. Please try again.");
					return;
				}
				dataService.changePassword(userName, oldPassword, newPassword, new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						view.resetPasswordChangeForm();
						view.enablePasswordChangeForm();
						view.hideWaitIndicator();
						view.showError("There was a problem completing the password change request.",
								caught.getMessage());
						// NOTE: If user types her password incorrectly here, it's an auth
						// exception. Just this once, don't logoutIfAuthException().
					}

					@Override
					public void onSuccess(String result) {
						view.showMessage("Password changed.");
						view.hidePasswordChangeForm();
						view.resetPasswordChangeForm();
						view.enablePasswordChangeForm();
						view.hideWaitIndicator();
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
		setViewEventHandlers();
	}

	@Override
	public void go(Map<String, String> params) {
		// hide any leftover messages
		view.hideMessage();
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
		//showUserDetails(this.userInfo);

		this.dataService.fetchUserInfo(userName, new AsyncCallback<UserInfo>() {
			@Override
			public void onFailure(Throwable caught) {
				AwErrorUtils.logoutIfAuthException(caught);
				ErrorDialog.show("We were unable to fetch your user info", caught.getMessage());
			}

			@Override
			public void onSuccess(UserInfo result) {
				showUserDetails(result);
			}
		});
	}

	private void showUserDetails(UserInfo userInfo) {
		this.view.setUserName(userInfo.getUserName());
		this.view.setEmail(userInfo.getEmail());
		this.view.setCanCreate(userInfo.canCreate());
		this.view.clearClassList();
		Map<String, String> userClasses = userInfo.getClasses();
		for (String classId : userClasses.keySet()) {
			this.view.addClass(classId, userClasses.get(classId));
		}
	}
}
