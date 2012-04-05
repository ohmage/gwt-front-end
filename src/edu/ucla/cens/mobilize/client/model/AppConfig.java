package edu.ucla.cens.mobilize.client.model;

import java.util.List;

import edu.ucla.cens.mobilize.client.common.Privacy;

public class AppConfig {
  private static String appName;
  private static String appDisplayName;
  private static boolean sharedResponsesOnly;
  private static boolean responsePrivacyIsEditable;
  private static List<Privacy> privacyStates;
  private static boolean mobilityEnabled;
  private static int documentUploadMaxSize;
  private static boolean selfRegistrationAllowed;
  
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
  
  public static String getAppDisplayName() {
    return appDisplayName;
  }
  public static void setAppDisplayName(String appDisplayName) {
    AppConfig.appDisplayName = appDisplayName;
  }
  
  /**
   * @return True if viz and export should include only shared responses (with
   * the exception of privacy-based visualizations)
   */
  public static boolean exportAndVisualizeSharedResponsesOnly() {
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
  
  public static boolean getMobilityEnabled() {
    return mobilityEnabled;
  }
  public static void setMobilityEnabled(boolean isEnabled) {
    AppConfig.mobilityEnabled = isEnabled;
  }
  
  public static int getDocumentUploadMaxSize() {
    return documentUploadMaxSize;
  }
  public static void setDocumentUploadMaxSize(int maxSize) {
    AppConfig.documentUploadMaxSize = maxSize;
  }
  
  public static boolean getSelfRegistrationEnabled() {
    return selfRegistrationAllowed;
  }
  
  public static void setSelfRegistrationEnabled(boolean isEnabled) {
    AppConfig.selfRegistrationAllowed = isEnabled;
  }
}
