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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.RegistrationInfo;

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
	}
	
	private void loadRecaptchaAndTOS() {
		dataService.fetchRegistrationInfo(new AsyncCallback<RegistrationInfo>() {
			@Override
			public void onFailure(Throwable caught) {
				ErrorDialog.show("Could not obtain server registration information", "The ohmage self-registration feature is currently unavailable for this server.");	
			}

			@Override
			public void onSuccess(RegistrationInfo result) {
				setRecaptchaKey(result.getRecaptchaKey());
				setTermsOfServiceText(result.getTermsOfService());
			}
		});
	}

	// --- Form submission

	private void validateAndSubmitRegistration() {
		if (captcha == null)	return;	// TODO: Display recaptcha error
		
		// Validate
		// TODO: Validate on front-end first
		
		// Submit
		final String username = this.getUsername();
		final String password = this.getPassword();
		final String email = this.getEmail();
		final String recaptcha_challenge_field = this.captcha.getChallenge();
		final String recaptcha_response_field = this.captcha.getResponse();
		
		dataService.registerUser(username, password, email, recaptcha_challenge_field, recaptcha_response_field, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				errorText.setText(caught.getMessage());
				errorText.setVisible(true);
				captcha.reload();
			}

			@Override
			public void onSuccess(String result) {
				setSuccessTextVisible(true);
			}
		});
	}
	
	// --- UI Functions
	
	public void setSuccessTextVisible(boolean isFinished) {
		registrationPanel.setVisible(!isFinished);
		submissionPanel.setVisible(isFinished);
	}
	
	public void clearFields() {
		// TODO
	}
	
	public void highlightErrors() {
		// TODO
	}

	public void setRecaptchaKey(String recaptchaKey) {
		captcha = new RecaptchaWidget(recaptchaKey, "en", "clean");
		recaptchaContainer.add(captcha);
	}
	
	public void setTermsOfServiceText(String tosText) {
		tos.setText(tosText);
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
