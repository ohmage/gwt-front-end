package edu.ucla.cens.mobilize.client.common;

public enum PromptType {
  TIMESTAMP,
  NUMBER,
  HOURS_BEFORE_NOW,
  TEXT,
  MULTI_CHOICE,
  SINGLE_CHOICE,
  SINGLE_CHOICE_CUSTOM,
  MULTI_CHOICE_CUSTOM,
  PHOTO,
  REMOTE_ACTIVITY,
  VIDEO,
  AUDIO;

  /**
   * @param promptTypeString
   * @return matching enum value or null if none match
   */
  public static PromptType fromString(String promptTypeString) {
    String str = promptTypeString.trim().toUpperCase();
    PromptType retval = null;
    try { 
      retval = PromptType.valueOf(str);
    } catch (Exception exception) { }
    return retval;
  }
}
