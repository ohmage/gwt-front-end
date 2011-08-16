package edu.ucla.cens.mobilize.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;

public class AppConfig {
  private String logoUrl;
  private String loginPageHtml;
  // for each i, linkTexts[i] is the user-facing text for linkUrls[i]
  private List<String> linkTexts = new ArrayList<String>(); 
  private List<String> linkUrls = new ArrayList<String>();
  private static String appName;
  private static boolean sharedResponsesOnly;
  private static boolean responsePrivacyIsEditable;
  private static List<Privacy> privacyStates;
  
  public static boolean isLoaded() {
    return appName != null && privacyStates != null;
  }
  
  /**
   * @return Name of app to be used in page title, etc
   */
  public static String getAppName() {
    assert appName != null : "App config must be loaded before calling getAppName";
    return appName;
  }
  public static void setAppName(String appName) {
    AppConfig.appName = appName;
  }

  /**
   * @return Url to use in image src field for app-specific logo
   */
  public String getLoginPageLogoUrl() {
    return logoUrl;
  }
  
  public void setLoginPageLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }
  
  /**
   * @return Text/html describing the app that will be displayed on the login page.
   */
  public String getLoginPageHtml() {
    return loginPageHtml;
  }
  public void setLoginPageHtml(String loginPageHtml) {
    this.loginPageHtml = loginPageHtml;
  }
  
  /**
   * @return List of strings user will see (as links) in the lower right corner of login page
   */
  public List<String> getLoginPageLinkTexts() {
    return this.linkTexts;
  }
  
  /**
   * List of urls for the links in the lower right corner of the login page
   * @return
   */
  public List<String> getLoginPageLinkUrls() {
    return this.linkUrls;
  }
  
  public void addLink(String text, String url) {
    this.linkTexts.add(text);
    this.linkUrls.add(url);
  }
  
  /**
   * @return True if viz and export should include only shared responses (with
   * the exception of privacy-based visualizations)
   */
  public static boolean isSharedResponsesOnly() {
    return sharedResponsesOnly;
  }
  public static void setSharedResponsesOnly(boolean sharedResponsesOnly) {
    AppConfig.sharedResponsesOnly = sharedResponsesOnly;
  }
  
  /**
   * @return True if supervisors and owners of responses should be able to 
   * update the response privacy state (e.g., share it or make it private)
   * and/or delete a response
   */
  public static boolean responsePrivacyIsEditable() {
    return responsePrivacyIsEditable;
  }
  public static void setResponsePrivacyIsEditable(boolean isEditable) {
    AppConfig.responsePrivacyIsEditable = isEditable;
  }
  
  public static List<Privacy> getResponsePrivacyStates() {
    return AppConfig.privacyStates;
  }
  public static void setPrivacyStates(List<Privacy> privacyStates) {
    AppConfig.privacyStates = privacyStates;
  }
  
  
}
