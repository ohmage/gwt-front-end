package edu.ucla.cens.mobilize.client.common;

/**
 * Uniquely identifies a campaign.
 * Urn for now, but wrapped in a class in case that ever changes.
 * Overrides hashCode and equals so id can be used in HashMaps. 
 * 
 * @author vhajdik
 */
public class CampaignId {
  public String urn;
  //
  
  public CampaignId(String urn) {
    this.urn = urn;
    this.urn = urn;
  }
  
  public String toString() {
    return urn;
  }
  
  public static CampaignId fromString(String s) {
    return s != null ? new CampaignId(s) : new CampaignId("INVALID_ID_STRING");
  }
  
  @Override
  public int hashCode() {
    return this.urn.hashCode();
  }
  
  @Override
  public boolean equals(Object other) {    
    return other != null &&
           this.getClass() == other.getClass() &&
           this.urn.equals(((CampaignId)other).urn);
  }
}
