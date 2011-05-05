package edu.ucla.cens.mobilize.client.common;

public enum PromptType {
  UNRECOGNIZED,
  TIMESTAMP,
  NUMBER,
  HOURS_BEFORE_NOW,
  TEXT,
  MULTI_CHOICE,
  SINGLE_CHOICE,
  SINGLE_CHOICE_CUSTOM,
  MULTI_CHOICE_CUSTOM,
  PHOTO;

  
  /**
   * @param promptTypeString
   * @return matching enum value or UNRECOGNIZED if none match
   */
  public static PromptType fromString(String promptTypeString) {
    String str = promptTypeString.trim().toUpperCase();
    try {
      return PromptType.valueOf(str);
    } catch (Exception exception) { // FIXME: specific exception
      return PromptType.UNRECOGNIZED;
    }
  }
}
