package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.common.TokenLoginManager;
import edu.ucla.cens.AndWellnessVisualizations.client.common.SetModel;
import edu.ucla.cens.AndWellnessVisualizations.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.AwDataTranslators;
import edu.ucla.cens.AndWellnessVisualizations.client.view.LoginBoxView;

public class LoginBoxPresenter implements Presenter,
    LoginBoxView.Presenter {
    private final AndWellnessRpcService rpcService;
    private final EventBus eventBus;  
    private final LoginBoxView view;
    private final TokenLoginManager loginManager;
    
    // SelectionModels to hold state from the View
    private final SetModel<String> userNameModel;
    private final SetModel<String> passwordModel;

    private static Logger _logger = Logger.getLogger(LoginBoxPresenter.class.getName());
    
    public LoginBoxPresenter(AndWellnessRpcService rpcService, 
            EventBus eventBus, LoginBoxView view, TokenLoginManager loginManager) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.view = view;
        this.view.setPresenter(this);
        this.loginManager = loginManager;
        
        this.userNameModel = new SetModel<String>();
        this.passwordModel = new SetModel<String>();
    } 
    
    /**
     * Attaches the view to the container.
     * 
     * @param The container to attach to.
     */
    public void go(HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
    }

    /**
     * Updates the userNameModel with the new user name.
     * 
     * @param userName The user name to update.
     */
    public void onUserNameChange(String userName) {
        userNameModel.updateSetItem(userName);
    }

    /**
     * Updates the passwordModel with the new password.
     * 
     * @param password The password to update.
     */
    public void onPasswordChange(String password) {
        passwordModel.updateSetItem(password);
    }

    /**
     * Handles a login request.  Call the rpcService directly to login.
     * 
     */
    public void onLoginButtonClicked() {
        // Check to be sure a username and password have been entered.
        if (!userNameModel.isSet()) {
            view.setInvalidUserName("Please enter a user name");
            return;
        }
        
        if (!passwordModel.isSet()) {
            view.setInvalidPassword("Please enter a password");
        }
        
        rpcService.fetchAuthorizationToken(userNameModel.getSetItem(), passwordModel.getSetItem(), new AsyncCallback<AuthorizationTokenQueryAwData>() {

            /**
             * Notifies the View that the login failed
             */
            public void onFailure(Throwable caught) {
               _logger.warning("User login failed with reason: " + caught.getMessage());
                
               view.setLoginFailed();
            }

            /**
             * Informs the login manager upon successful login.
             * 
             * @param result The login data.
             */
            public void onSuccess(AuthorizationTokenQueryAwData result) {
                _logger.info("Successfully logged in user: " + userNameModel.getSetItem());
                
                loginManager.loginWithAuthToken(result.getAuthorizationToken(), userNameModel.getSetItem());
            }
            
        });
    }

}
