package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.utils.InputValidationUtils;

public class LoginRecovery extends Composite {
	private static LoginRecoveryUiBinder uiBinder = GWT
			.create(LoginRecoveryUiBinder.class);

	interface LoginRecoveryUiBinder extends
	UiBinder<Widget, LoginRecovery> {
	}

	@UiField HTMLPanel recoveryPanel;
	@UiField HTMLPanel submissionPanel;
	@UiField Label errorText;
	@UiField TextBox username;
	@UiField TextBox email;
	@UiField Button submit;
	@UiField HTMLPanel submitSpinner;

	DataService dataService;
	
	// --- Init
	
	public LoginRecovery(DataService dataService) {
		this.dataService = dataService;	// This must be called first!
		
		initWidget(uiBinder.createAndBindUi(this));
		resetAll();				// Reset all UI input and views
		
		// Init event handlers
		submit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				validateAndSubmitRecovery();
			}
		});
	}
	
	public void resetAll() {
		username.setValue("");
		email.setValue("");
		errorText.setVisible(false);
		setSuccessTextVisible(false);
		setSubmitSpinnerVisible(false);
	}
	
	// --- Form submission

	private void validateAndSubmitRecovery() {
		// Get values from UI
		final String username = this.getUsername();
		final String email = this.getEmail();
		
		// Validate
		if (username.isEmpty()) {
			displayRecoveryError("You must enter a username.");
			return;
		}
		if (email.isEmpty() || !InputValidationUtils.isValidEmail(email)) {
			displayRecoveryError("You must enter a valid email address.");
			return;
		}
		
		// Show loading spinner image
		setSubmitSpinnerVisible(true);
		
		// Submit
		dataService.resetPassword(username, email, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				setSubmitSpinnerVisible(false);
				displayRecoveryError(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				setSubmitSpinnerVisible(false);
				setSuccessTextVisible(true);
			}
		});
	}
	
	// --- UI Functions
	
	public void setSuccessTextVisible(boolean isFinished) {
		recoveryPanel.setVisible(!isFinished);
		submissionPanel.setVisible(isFinished);
	}
	
	public void setSubmitSpinnerVisible(boolean isVisible) {
		submitSpinner.setVisible(isVisible);
	}
	
	public void displayRecoveryError(String msg) {
		if (msg != null && !msg.isEmpty()) {
			errorText.setText(msg);
			errorText.setVisible(true);
		} else {
			errorText.setVisible(false);
		}
	}
	
	// --- Input controls
	
	public String getUsername() {
		return this.username.getText();
	}

	public void setUsername(String firstNameSearchString) {
		this.username.setText(firstNameSearchString);
	}

	public String getEmail() {
		return this.email.getText();
	}

	public void setEmail(String emailSearchString) {
		this.email.setText(emailSearchString);
	}
}
