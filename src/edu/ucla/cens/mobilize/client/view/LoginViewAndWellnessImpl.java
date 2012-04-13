package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.ui.WaitIndicator;


public class LoginViewAndWellnessImpl extends Composite implements LoginView {
	@UiTemplate("LoginViewAndWellness.ui.xml")
	interface LoginBoxViewUiBinder extends UiBinder<Widget, LoginViewAndWellnessImpl> {}
	private static LoginBoxViewUiBinder uiBinder =
			GWT.create(LoginBoxViewUiBinder.class);

	// Fields defined in the ui XML
	@UiField TextBox userNameTextBox;
	@UiField PasswordTextBox passwordTextBox;
	@UiField Button loginButton;

	private Presenter presenter;

	public LoginViewAndWellnessImpl() {
		initWidget(uiBinder.createAndBindUi(this)); 
		initEventHandlers();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute () {
				userNameTextBox.setFocus(true);
			}
		});
	}

	private void initEventHandlers() {
		// clicking login button submits (note: pressing enter while login has focus also submits)
		loginButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submit();
			}
		});

		// pressing enter in user name box submits
		userNameTextBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					submit();
				}
			}
		});

		// pressing enter in password box submits
		passwordTextBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					submit();
				}
			}
		});
	}

	// verifies that both name and password are set then notifies presenter
	private void submit() {
		this.presenter.onSubmit();
	}

	/**
	 * Sets the views presenter to call to handle events.
	 * 
	 * @param presenter The presenter to bind to.
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setLoginFailed(String msg) {
		ErrorDialog.show("Invalid username or password", msg);
	}

	@Override
	public String getUsername() {
		return userNameTextBox.getText();
	}

	@Override
	public String getPassword() {
		return passwordTextBox.getText();
	}

	@Override
	public void disableLoginForm() {
		loginButton.setEnabled(false);
		userNameTextBox.setEnabled(false);
		passwordTextBox.setEnabled(false);
		WaitIndicator.show();
	}

	@Override
	public void enableLoginForm() {
		loginButton.setEnabled(true);
		userNameTextBox.setEnabled(true);
		passwordTextBox.setEnabled(true);
		WaitIndicator.hide();
	}

	@Override
	public void showError(String errorMsg) {
		ErrorDialog.show(errorMsg);
	}

	@Override
	public void setNotificationMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelfRegistrationEnabled(boolean isEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoginRecoveryEnabled(boolean isEnabled) {
		// TODO Auto-generated method stub

	}
}
