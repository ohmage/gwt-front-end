package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.ui.MessageWidget;
import edu.ucla.cens.mobilize.client.ui.WaitIndicator;

public class AccountViewImpl extends Composite implements AccountView {

	private static AccountViewUiBinder uiBinder = GWT
			.create(AccountViewUiBinder.class);

	@UiTemplate("AccountView.ui.xml")
	interface AccountViewUiBinder extends UiBinder<Widget, AccountViewImpl> {
	}

	@UiField MessageWidget messageWidget;
	@UiField InlineLabel loginLabel;
	@UiField InlineLabel emailLabel;
	@UiField InlineLabel canCreateLabel;
	@UiField VerticalPanel classesVerticalPanel;
	@UiField FormPanel passwordChangeForm;
	@UiField HTMLPanel passwordChangePanel;
	@UiField Button passwordChangeButton;
	@UiField Button passwordChangeCancelButton;
	@UiField PasswordTextBox oldPasswordTextBox;
	@UiField PasswordTextBox newPasswordTextBox;
	@UiField PasswordTextBox newPasswordConfirmTextBox;
	@UiField Button passwordChangeSubmitButton;

	public AccountViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

		// some elements stay hidden unless needed
		hidePasswordChangeForm();
	}

	@Override
	public void setUserName(String userName) {
		this.loginLabel.setText(userName);
	}

	@Override
	public void setCanCreate(boolean canCreate) {
		this.canCreateLabel.setText(canCreate ? "Yes" : "No");
	}

	@Override
	public void clearClassList() {
		this.classesVerticalPanel.clear();
	}

	@Override
	public void addClass(String classId, String className) {
		this.classesVerticalPanel.add(new InlineHyperlink(className, 
				HistoryTokens.classDetail(classId)));    
	}

	@Override
	public void showPasswordChangeForm() {
		this.passwordChangePanel.setVisible(true);
	}

	@Override
	public void hidePasswordChangeForm() {
		this.passwordChangePanel.setVisible(false);
	}

	@Override
	public void resetPasswordChangeForm() {
		this.passwordChangeForm.reset();
	}

	@Override
	public void enablePasswordChangeForm() {
		this.passwordChangeSubmitButton.setEnabled(true);
		this.oldPasswordTextBox.setEnabled(true);
		this.newPasswordTextBox.setEnabled(true);
		this.newPasswordConfirmTextBox.setEnabled(true);
	}

	@Override
	public void disablePasswordChangeForm() {
		this.passwordChangeSubmitButton.setEnabled(false);
		this.oldPasswordTextBox.setEnabled(false);
		this.newPasswordTextBox.setEnabled(false);
		this.newPasswordConfirmTextBox.setEnabled(false);
	}

	@Override
	public void setPasswordChangeSubmitHandler(SubmitHandler handler) {
		this.passwordChangeForm.addSubmitHandler(handler);
	}

	@Override
	public HasClickHandlers getPasswordChangeButton() {
		return this.passwordChangeButton;    
	}

	@Override
	public HasClickHandlers getPasswordChangeSubmitButton() {
		return this.passwordChangeSubmitButton;
	}

	@Override
	public HasClickHandlers getPasswordChangeCancelButton() {
		return this.passwordChangeCancelButton;
	}

	@Override
	public String getUserName() {
		return this.loginLabel.getText();
	}

	@Override
	public String getOldPassword() {
		return this.oldPasswordTextBox.getText();    
	}

	@Override
	public String getNewPassword() {
		return this.newPasswordTextBox.getText();
	}

	@Override
	public String getNewPasswordConfirm() {
		return this.newPasswordConfirmTextBox.getText();
	}

	@Override
	public void showMessage(String message) {
		this.messageWidget.showInfoMessage(message);
	}

	@Override
	public void showError(String message, String detail) {
		ErrorDialog.show(message, detail);
	}

	@Override
	public void hideMessage() {
		this.messageWidget.hide();
	}

	@Override
	public void showWaitIndicator() {
		WaitIndicator.show();
	}

	@Override
	public void hideWaitIndicator() {
		WaitIndicator.hide();
	}

	@Override
	public void setEmail(String email) {
		if (email == null || email.isEmpty())
			this.emailLabel.setText("(not available)");
		else
			this.emailLabel.setText(email);
	}

	@Override
	public String getEmail() {
		return this.emailLabel.getText();
	}
}
