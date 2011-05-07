package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CampaignEditViewImpl extends Composite implements CampaignEditView {

  private static CampaignEditViewUiBinder uiBinder = GWT
      .create(CampaignEditViewUiBinder.class);

  @UiTemplate("CampaignEditView.ui.xml")
  interface CampaignEditViewUiBinder extends UiBinder<Widget, CampaignEditViewImpl> {
  }

  public CampaignEditViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
