package edu.ucla.cens.mobilize.client.presenter;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

import edu.ucla.cens.mobilize.client.common.TokenLoginManager;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.AuthorizationTokenQueryAwData;
import edu.ucla.cens.mobilize.client.rpcservice.ServerUnavailableException;
import edu.ucla.cens.mobilize.client.view.LoginView;

public class LoginPresenter implements Presenter,
    LoginView.Presenter {
    private final DataService dataService;
    private final LoginView view;
    private final TokenLoginManager loginManager;
    
    private static Logger _logger = Logger.getLogger(LoginPresenter.class.getName());
    
    public LoginPresenter(DataService dataService, 
            EventBus eventBus, LoginView view, TokenLoginManager loginManager) {
        this.dataService = dataService;
        this.view = view;
        this.view.setPresenter(this);
        this.loginManager = loginManager;
    } 

    @Override
    public void go(Map<String, List<String>> params) {
      // TODO Auto-generated method stub
    }

    @Override
    public void onSubmit() {
      // Check to be sure a username and password have been entered.
      final String userName = view.getUserName();
      final String password = view.getPassword();
      
      if (userName.isEmpty()) {
        view.setLoginFailed("Please enter a user name.");
        return;
      } 
      
      if (password.isEmpty()) {
        view.setLoginFailed("Please enter a password.");
        return;
      } 
      
      dataService.fetchAuthorizationToken(userName, password, new AsyncCallback<AuthorizationTokenQueryAwData>() {
        
          /**
           * Notifies the View that the login failed
           */
          public void onFailure(Throwable caught) {
             _logger.warning("User login failed with reason: " + caught.getMessage());
             if (caught.getClass().equals(ServerUnavailableException.class)) {
               DialogBox errorDialog = new DialogBox();
               errorDialog.setText("There was a problem contacting the server. Please try again.");
               errorDialog.setAutoHideEnabled(true);
               errorDialog.setGlassEnabled(true);
               errorDialog.center();
             } else {
               view.setLoginFailed("Login failed. Please check name and password and try again.");
             }
          }

          /**
           * Informs the login manager upon successful login.
           * 
           * @param result The login data.
           */
          public void onSuccess(AuthorizationTokenQueryAwData result) {
              _logger.info("Successfully logged in user: " + userName);
              
              loginManager.loginWithAuthToken(result.getAuthorizationToken(), userName);
              
              // reload to get logged in app
              Window.Location.reload();
          }
          
      });
    }

}
