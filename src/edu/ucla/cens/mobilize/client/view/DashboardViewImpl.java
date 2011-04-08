/**
 * 
 */
package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author vhajdik
 *
 */
public class DashboardViewImpl extends Composite implements DashboardView {

	private static DashboardUiBinder uiBinder = GWT
			.create(DashboardUiBinder.class);

	@UiTemplate("DashboardView.ui.xml")
	interface DashboardUiBinder extends UiBinder<Widget, DashboardViewImpl> {
	}

	private DashboardView.Presenter presenter;
	
	public DashboardViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField HTMLPanel notificationResponses;
	@UiField HTMLPanel notificationParticipant;
	@UiField HTMLPanel notificationAuthor;
	@UiField SpanElement numUnreadResponsesSpan;
	@UiField SpanElement numActiveParticipantCampaignsSpan;
	@UiField SpanElement numActiveAuthorCampaignsSpan;
  @UiField MenuItem quickLinkCreate;
  @UiField MenuItem quickLinkEdit;
  @UiField MenuItem quickLinkUpload;
	
	boolean canEdit = false;
  boolean canUpload = false;	
	
	public DashboardViewImpl(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
  private void updateRoleSpecificDisplay() {
    this.quickLinkCreate.setVisible(this.canEdit);
    this.quickLinkEdit.setVisible(this.canEdit);
    this.quickLinkUpload.setVisible(this.canUpload);
    this.notificationAuthor.setVisible(this.canEdit);
    // FIXME: notifications display should change depending
    // on whether there are any responses, etc
  }
	
	@Override
	public void setPermissions(boolean canEdit, boolean canUpload) {
	  this.canEdit = canEdit;
	  this.canUpload = canUpload;
	  updateRoleSpecificDisplay();
	}

  @Override
  public void setNumUnreadSurveyResponses(int num) {
    this.numUnreadResponsesSpan.setInnerText(Integer.toString(num));    
  }

  @Override
  public void setNumActiveParticipantCampaigns(int num) {
    this.numActiveParticipantCampaignsSpan.setInnerText(Integer.toString(num));    
  }

  @Override
  public void setNumActiveAuthorCampaigns(int num) {
    this.numActiveAuthorCampaignsSpan.setInnerText(Integer.toString(num));
  }

  @Override
  public void setPresenter(Presenter p) {
    this.presenter = p;    
  }
	
}
