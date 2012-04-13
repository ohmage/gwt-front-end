package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.ui.WaitIndicator;


public class LoginViewOhmageImpl extends Composite implements LoginView {
	@UiTemplate("LoginViewOhmage.ui.xml")
	interface LoginBoxViewUiBinder extends UiBinder<Widget, LoginViewOhmageImpl> {}
	private static LoginBoxViewUiBinder uiBinder = GWT.create(LoginBoxViewUiBinder.class);

	// Fields defined in the ui XML
	@UiField TextBox userNameTextBox;
	@UiField PasswordTextBox passwordTextBox;
	@UiField Button loginButton;
	@UiField HTMLPanel selfRegistrationText;
	@UiField HTMLPanel loginRecoveryText;
	@UiField HTMLPanel messagePanel;
	@UiField HTMLPanel messageBox;
	@UiField InlineHyperlink selfRegisterLink;

	private Presenter presenter;

	public LoginViewOhmageImpl() {
		initWidget(uiBinder.createAndBindUi(this)); 
		initEventHandlers();

		// Defaults
		setNotificationMessage(null);
		setSelfRegistrationEnabled(false);
		setLoginRecoveryEnabled(true);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute () {
				userNameTextBox.setFocus(true);
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void initEventHandlers() {
		// clicking login button submits (note: pressing enter
		//  while login has focus also submits)
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
		ErrorDialog.show("The login information you entered is incorrect", msg);
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
		if (message == null || message.isEmpty()) {
			messagePanel.setVisible(false);
			return;
		}

		messageBox.clear();
		messageBox.add(new InlineLabel(message));
		messagePanel.setVisible(true);
	}

	@Override
	public void setSelfRegistrationEnabled(boolean isEnabled) {
		selfRegistrationText.setVisible(isEnabled);
	}

	@Override
	public void setLoginRecoveryEnabled(boolean isEnabled) {
		loginRecoveryText.setVisible(isEnabled);
	}
}
