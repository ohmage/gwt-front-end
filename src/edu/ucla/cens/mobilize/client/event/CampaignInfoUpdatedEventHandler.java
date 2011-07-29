package edu.ucla.cens.mobilize.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CampaignInfoUpdatedEventHandler extends EventHandler {
  void onCampaignInfoUpdated(CampaignInfoUpdatedEvent event);
}
