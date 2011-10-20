package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CampaignDataChangedEventHandler extends EventHandler {
  public void onCampaignDataChanged(CampaignDataChangedEvent event);
}
