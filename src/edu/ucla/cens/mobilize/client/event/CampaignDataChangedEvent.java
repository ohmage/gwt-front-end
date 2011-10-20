package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CampaignDataChangedEvent extends GwtEvent<CampaignDataChangedEventHandler> {

  public static Type<CampaignDataChangedEventHandler> TYPE = new Type<CampaignDataChangedEventHandler>(); 
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<CampaignDataChangedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CampaignDataChangedEventHandler handler) {
    handler.onCampaignDataChanged(this);
  }

}
