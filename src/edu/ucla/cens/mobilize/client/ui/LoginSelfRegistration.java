package edu.ucla.cens.mobilize.client.ui;

import com.claudiushauptmann.gwt.recaptcha.client.RecaptchaWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.RegistrationInfo;
import edu.ucla.cens.mobilize.client.utils.InputValidationUtils;

public class LoginSelfRegistration extends Composite {
	private static LoginSelfRegistrationUiBinder uiBinder = GWT
			.create(LoginSelfRegistrationUiBinder.class);

	interface LoginSelfRegistrationUiBinder extends
	UiBinder<Widget, LoginSelfRegistration> {
	}

	@UiField HTMLPanel registrationPanel;
	@UiField HTMLPanel submissionPanel;
	@UiField Label errorText;
	@UiField TextBox username;
	@UiField PasswordTextBox password;
	@UiField PasswordTextBox passwordConfirm;
	@UiField TextBox email;
	@UiField HTMLPanel recaptchaContainer;
	@UiField TextArea tos;
	@UiField CheckBox agree;
	@UiField Button submit;
	@UiField HTMLPanel submitSpinner;

	DataService dataService;
	RecaptchaWidget captcha;
	
	// --- Init
	
	public LoginSelfRegistration(DataService dataService) {
		this.dataService = dataService;	// This must be called first!
		
		initWidget(uiBinder.createAndBindUi(this));
		loadRecaptchaAndTOS();	// Fetch data from registration/read
		resetAll();				// Reset all UI input and views
		
		// Init event handlers
		submit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				validateAndSubmitRegistration();
			}
		});
	}
	
	public void resetAll() {
		username.setValue("");
		password.setValue("");
		passwordConfirm.setValue("");
		email.setValue("");
		errorText.setVisible(false);
		tos.setReadOnly(true);
		agree.setValue(false);
		setSuccessTextVisible(false);
		setSubmitSpinnerVisible(false);
	}
	
	private void loadRecaptchaAndTOS() {
		dataService.fetchRegistrationInfo(new AsyncCallback<RegistrationInfo>() {
			@Override
			public void onFailure(Throwable caught) {
				ErrorDialog.show("Could not obtain server registration information", "The ohmage self-registration feature is currently unavailable for this server.");	
			}

			@Override
			public void onSuccess(RegistrationInfo result) {
				if (result.getRecaptchaKey().isEmpty()) {
					
				}
				
				setRecaptchaKey(result.getRecaptchaKey());
				setTermsOfServiceText(result.getTermsOfService());
			}
		});
	}

	// --- Form submission

	private void validateAndSubmitRegistration() {
		if (captcha == null) {
			displayRegistrationError("Sorry, ReCaptcha is unavailable at this time. Please try again later or contact us if the problem persists.");
			return;
		}

		// Get values from UI and captcha
		final String username = this.getUsername();
		final String password = this.getPassword();
		final String passwordConfirm = this.getPasswordConfirm();
		final String email = this.getEmail();
		final String recaptcha_challenge_field = this.captcha.getChallenge();
		final String recaptcha_response_field = this.captcha.getResponse();
		
		// Validate
		// NOTE: We let the server report back errors in the username and password formats, in case we decide to change them
		if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || email.isEmpty()) {
			displayRegistrationError("Looks like you're missing something. Please fill in all fields and try again.");
			return;
		}
		if (!agree.getValue()) {
			displayRegistrationError("Whoops! You must read and agree to the terms of service to register.");
			return;
		}
		if (!InputValidationUtils.isValidEmail(email)) {
			displayRegistrationError("The e-mail you provided is invalid. Please re-enter and try again.");
			return;
		}
		if (!password.equals(passwordConfirm)) {
			displayRegistrationError("Your passwords do not match. Please re-enter both carefully and try again.");
			return;
		}
		
		// Show loading spinner image
		setSubmitSpinnerVisible(true);
		
		// Submit
		dataService.registerUser(username, password, email, recaptcha_challenge_field, recaptcha_response_field, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				setSubmitSpinnerVisible(false);
				displayRegistrationError(caught.getMessage());
				captcha.reload();
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
		registrationPanel.setVisible(!isFinished);
		submissionPanel.setVisible(isFinished);
	}
	
	public void setSubmitSpinnerVisible(boolean isVisible) {
		submitSpinner.setVisible(isVisible);
	}
	
	public void displayRegistrationError(String msg) {
		if (msg != null && !msg.isEmpty()) {
			errorText.setText(msg);
			errorText.setVisible(true);
		} else {
			errorText.setVisible(false);
		}
	}
	
	public void setRecaptchaKey(String recaptchaKey) {
		if (recaptchaKey == null || recaptchaKey.isEmpty()) {
			recaptchaContainer.add(new Label("(this ReCaptcha is currently unavailable at this time)"));
		} else {
			captcha = new RecaptchaWidget(recaptchaKey, "en", "clean");
			recaptchaContainer.add(captcha);
		}
	}
	
	public void setTermsOfServiceText(String tosText) {
		if (tosText == null || tosText.isEmpty()) {
			tos.setText("(the terms of service is currently unavailable at this time)");
		} else {
			tos.setText(tosText);
		}
	}
	
	// --- Input controls
	
	public String getUsername() {
		return this.username.getText();
	}

	public void setUsername(String firstNameSearchString) {
		this.username.setText(firstNameSearchString);
	}

	public String getPassword() {
		return this.password.getText();
	}

	public void setPassword(String lastNameSearchString) {
		this.password.setText(lastNameSearchString);
	}
	
	public String getPasswordConfirm() {
		return this.passwordConfirm.getText();
	}

	public void setPasswordConfirm(String lastNameSearchString) {
		this.passwordConfirm.setText(lastNameSearchString);
	}

	public String getEmail() {
		return this.email.getText();
	}

	public void setEmail(String emailSearchString) {
		this.email.setText(emailSearchString);
	}
	
	public boolean isTermsOfServiceAgreed() {
		return agree.getValue();
	}
}
