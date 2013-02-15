package edu.ucla.cens.mobilize.client.presenter;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

import edu.ucla.cens.mobilize.client.common.TokenLoginManager;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.exceptions.AuthenticationException;
import edu.ucla.cens.mobilize.client.exceptions.ServerUnavailableException;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.ui.NewAccountPasswordChange;
import edu.ucla.cens.mobilize.client.ui.SuccessDialog;
import edu.ucla.cens.mobilize.client.ui.WaitIndicator;
import edu.ucla.cens.mobilize.client.view.LoginView;

public class LoginPresenter implements Presenter,
    LoginView.Presenter {
    private final DataService dataService;
    private final LoginView view;
    private final TokenLoginManager loginManager;
    
    private DialogBox passwordChangeDialog;
    private NewAccountPasswordChange passwordChangeForm;
    
    private static Logger _logger = Logger.getLogger(LoginPresenter.class.getName());
    
    public LoginPresenter(DataService dataService, 
                          EventBus eventBus, 
                          LoginView view, 
                          TokenLoginManager loginManager,
                          AppConfig appConfig) {
        this.dataService = dataService;
        this.view = view;
        this.view.setPresenter(this);
        this.loginManager = loginManager;
    } 

    @Override
    public void go(Map<String, String> params) {
    }

    @Override
    public void onSubmit() {
      // Check to be sure a username and password have been entered.
      final String username = view.getUsername();
      final String password = view.getPassword();
      
      if (username.isEmpty()) {
        view.setLoginFailed("Please enter a user name.");
        return;
      } 
      
      if (password.isEmpty()) {
        view.setLoginFailed("Please enter a password.");
        return;
      } 
      
      // Check for the optional redirect param in the query params
      String redirect = Window.Location.getParameter("redirect");
      
      // don't let user submit again or you could end up in a bad state 
      // with different auth token on the app than on the server
      view.disableLoginForm();
      login(username, password, redirect);
    }
    
    private void login(final String username, String password, String redirect) {
    	_logger.info("redirect: " + redirect);
    	
    	dataService.fetchAuthorizationToken(username, password, redirect, new AsyncCallback<AuthorizationTokenQueryAwData>() {

    		/**
    		 * Notifies the View that the login failed
    		 */
    		public void onFailure(Throwable caught) {
    			view.enableLoginForm();
    			_logger.warning("User login failed with reason: " + caught.getMessage());
    			if (caught.getClass().equals(ServerUnavailableException.class)) {
    				view.showError("There was a problem contacting the server. Please try again.");
    			} else if (caught.getClass().equals(AuthenticationException.class) &&
    					((AuthenticationException)caught).getErrorCode().equals("0202")) {
    				// New Account: open popup that requires user to enter a new password
    				promptUserToChangePassword(username);
    			} else {
    				view.setLoginFailed("Please check name and password and try again.");
    			}
    		}

    		/**
    		 * Informs the login manager upon successful login.
    		 * 
    		 * @param result The login data.
    		 */
    		public void onSuccess(AuthorizationTokenQueryAwData result) {
    			_logger.info("Successfully logged in user: " + username);
    			
    			// Check to see if there is a local referrer and redirect
    			// if so
    			Document document = Document.get();
    			
    			if(document.getReferrer() != null && ! "".equals(document.getReferrer())) {
    			
    				Window.Location.replace(document.getReferrer());
    				
    			} 
    			else {
	    			loginManager.login(username);
	    			
	    			// reload to get logged in app
	    			// all state (except for cookies) will be lost
	    			Window.Location.reload();
    			}
    		}
    	});
    }
    
    private void promptUserToChangePassword(final String username) {
      this.passwordChangeForm = new NewAccountPasswordChange();
      this.passwordChangeForm.setUsername(username);
      
      this.passwordChangeDialog = new DialogBox();
      this.passwordChangeDialog.setText("Welcome! You must change your password before continuing.");
      this.passwordChangeDialog.setWidget(this.passwordChangeForm);
      this.passwordChangeDialog.setModal(true);
      this.passwordChangeDialog.setGlassEnabled(true);
      
      this.passwordChangeForm.getChangePasswordButton().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          changePassword();
        }
      });
      
      this.passwordChangeForm.getCancelButton().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          passwordChangeForm.reset();
          passwordChangeDialog.hide();
        }
      });

      this.passwordChangeDialog.setWidth("720px"); // hack: manual width fixes incorrect centering
      this.passwordChangeDialog.center();

    }
    
    private boolean validatePassword() {
      boolean allFieldsAreValid = true;
      String oldPassword = this.passwordChangeForm.getCurrentPassword();
      String newPassword = this.passwordChangeForm.getNewPassword();
      String newPasswordConfirm = this.passwordChangeForm.getNewPasswordConfirm();
      if (!newPassword.equals(newPasswordConfirm)) {
        this.passwordChangeForm.showMismatchedPasswordsMessage();
        allFieldsAreValid = false;
      }
      if (newPassword.equals(oldPassword)) {
        this.passwordChangeForm.showReusedPasswordMessage();
        allFieldsAreValid = false;
        // NOTE: user could still "change" to the same password with javascript
      }
      return allFieldsAreValid;
    }
    
    private void changePassword() {
      if (validatePassword()) {
        WaitIndicator.show();
        final String username = this.passwordChangeForm.getUsername();
        String oldPassword = this.passwordChangeForm.getCurrentPassword();
        final String newPassword = this.passwordChangeForm.getNewPassword();
        dataService.changePassword(username, oldPassword, newPassword, new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable caught) {
            WaitIndicator.hide();
            view.showError("Password update failed:" + caught.getMessage());
          }

          @Override
          public void onSuccess(String result) {
            WaitIndicator.hide();
            passwordChangeDialog.hide();
            SuccessDialog.show("Password change successful.");
            login(username, newPassword, null);
          }
        });
      }
    }

}
