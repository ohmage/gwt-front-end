package edu.ucla.cens.mobilize.client.common;

public enum Privacy {
  UNDEFINED,
  PRIVATE,
  SHARED,
  INVISIBLE;
  
  public String toServerString() {
    return this.toString().toLowerCase();
  }
  
  public String toUserFriendlyString() { // first char uppercase, others lower
    return this.toString().substring(0, 1).concat(this.toString().substring(1).toLowerCase());
  }
  
  /**
   * @param privacyStringInServerFormat
   * @return Privacy enum object or UNDEFINED if match is not found
   */
  public static Privacy fromServerString(String privacyStringInServerFormat) {
    Privacy retval = null;
    try {
      retval = Privacy.valueOf(privacyStringInServerFormat.toUpperCase());
    } catch (Exception e) { 
      retval = UNDEFINED;
    }
    return retval;
  }
}
