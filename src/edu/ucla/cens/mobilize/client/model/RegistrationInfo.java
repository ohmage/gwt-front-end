package edu.ucla.cens.mobilize.client.model;

public class RegistrationInfo {
	private String recaptchaKey;
	private String termsOfService;
	
	public String getRecaptchaKey() {
		return this.recaptchaKey;
	}
	
	public void setRecaptchaKey(String recaptchaKey) {
		this.recaptchaKey = recaptchaKey;
	}
	
	public String getTermsOfService() {
		return this.termsOfService;
	}
	
	public void setTermsOfService(String termsOfService) {
		this.termsOfService = termsOfService;
	}
}
