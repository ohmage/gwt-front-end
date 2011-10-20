package edu.ucla.cens.mobilize.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;

public class CampaignInfoUpdatedEvent extends GwtEvent<CampaignInfoUpdatedEventHandler> {

  public static Type<CampaignInfoUpdatedEventHandler> TYPE = new Type<CampaignInfoUpdatedEventHandler>();
  
  private List<CampaignShortInfo> campaigns;
  
  public CampaignInfoUpdatedEvent(List<CampaignShortInfo> campaigns) {
    this.campaigns = campaigns;
  }
  
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<CampaignInfoUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CampaignInfoUpdatedEventHandler handler) {
    handler.onCampaignInfoUpdated(this);
  }

  public List<CampaignShortInfo> getCampaigns() {
    return campaigns; // gotcha: not a defensive copy
  }
  
}
