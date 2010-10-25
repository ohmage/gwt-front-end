package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class LoginBoxViewImpl extends Composite implements LoginBoxView {
    @UiTemplate("LoginBoxView.ui.xml")
    interface LoginBoxViewUiBinder extends UiBinder<Widget, LoginBoxViewImpl> {}
    private static LoginBoxViewUiBinder uiBinder =
      GWT.create(LoginBoxViewUiBinder.class);
    
    // Logging capability
    private static Logger _logger = Logger.getLogger(LoginBoxViewImpl.class.getName());
    
    // Fields defined in the ui XML
    @UiField TextBox userLoginBox;
    @UiField TextBox userPasswordBox;
    @UiField Button loginButton;
    
    // Call the presenter in response to events (user clicks)
    private Presenter presenter;
    
    // Very simple constructor now that the UI is defined in XML
    public LoginBoxViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));      
    }

    /**
     * Sets the views presenter to call to handle events.
     * 
     * @param presenter The presenter to bind to.
     */
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
    
    /**
     * Whenever the login box changes, send the change to the presenter.
     * 
     * @param event Notifies the textbox has changed
     */
    @UiHandler("userLoginBox")
    void onUserLoginBoxChanged(ChangeEvent event) {
        _logger.fine("New user login box text: " + userLoginBox.getText());
        
        if (presenter != null) {
            presenter.onUserNameChange(userLoginBox.getText());
        }
    }
    
    /**
     * Whenever the password box changes, send the change to the presenter.
     * 
     * @param event Notifies the textbox has changed
     */
    @UiHandler("userPasswordBox")
    void onUserPasswordBoxChanged(ChangeEvent event) {
        _logger.fine("New password box text: " + userPasswordBox.getText());
        
        if (presenter != null) {
            presenter.onPasswordChange(userPasswordBox.getText());
        }
    }
    
    // When the "Go" button is clicked, send notification to the presenter
    @UiHandler("loginButton")
    void onLoginButtonClicked(ClickEvent event) {
        _logger.info("Login button clicked");
        
        if (presenter != null) {
            presenter.onLoginButtonClicked();
        }
    }
    
    /**
     * Unhides the login failure div to alert the user to a bad username or password.
     * 
     */
    public void setLoginFailed() {
        
    }

    /**
     * Unhides the bad username div and sets it to the passed message.
     * 
     * @param message The message to set.
     */
    public void setInvalidUserName(String message) {
        // TODO Auto-generated method stub
        
    }

    /**
     * Unhides the bas password div and sets it to the passed message.
     * 
     * @param message The message to set.
     */
    public void setInvalidPassword(String message) {
        // TODO Auto-generated method stub
        
    }

    public Widget asWidget() {
        return this;
    }
}
