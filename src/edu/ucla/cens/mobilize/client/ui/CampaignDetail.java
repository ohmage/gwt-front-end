package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;

public class CampaignDetail extends Composite {

  private static CampaignDetailUiBinder uiBinder = GWT
      .create(CampaignDetailUiBinder.class);

  interface CampaignDetailUiBinder extends UiBinder<Widget, CampaignDetail> {
  }

  public interface CampaignDetailStyle extends CssResource {
    String stopped();
    String running();
    String privacyShared();
    String privacyPrivate();
  }
  
  @UiField CampaignDetailStyle style;
  @UiField InlineLabel campaignName;
  @UiField InlineLabel campaignUrn;
  @UiField InlineLabel desc;
  @UiField VerticalPanel classes;
  @UiField VerticalPanel authors;
  @UiField SpanElement runningStateSpan;
  @UiField SpanElement privacySpan;
  @UiField Hyperlink actionLinkEditCampaign;
  @UiField Anchor viewXmlInlineLink;
  @UiField Anchor downloadXmlInlineLink;
  @UiField HTMLPanel container;
  
  public CampaignDetail() {
    initWidget(uiBinder.createAndBindUi(this));
  }
 
  public void setCampaign(CampaignDetailedInfo campaign, boolean canEdit) {
    if (campaign != null) {
      // copy info from data obj into fields
      this.campaignName.setText(campaign.getCampaignName());
      this.campaignUrn.setText(campaign.getCampaignId());
      this.desc.setText(campaign.getDescription());
      
      // build class list
      this.classes.clear();
      for (String s : campaign.getClasses()) {
        this.classes.add(new HTML("<span>" + s + "</span>")); //fixme
      }
      
      // build author list
      this.authors.clear();
      for (String s : campaign.getAuthors()) {
        this.authors.add(new HTML("<span>" + s + "</span>"));
      }
      
      // running state style is set dynamically
      if (campaign.getRunningState().equals(RunningState.STOPPED)) {
        this.runningStateSpan.setClassName(style.stopped());
        this.runningStateSpan.setInnerText("STOPPED");
      } else if (campaign.getRunningState().equals(RunningState.RUNNING)) { 
        this.runningStateSpan.setClassName(style.running());
        this.runningStateSpan.setInnerText("RUNNING");
      } else {
        this.runningStateSpan.setClassName("");
        this.runningStateSpan.setInnerText(campaign.getRunningState().toString());
      }
      
      // privacy style is set dynamically
      if (campaign.getPrivacy().equals(Privacy.PRIVATE)) {
        privacySpan.setInnerText("PRIVATE");
        privacySpan.setClassName(style.privacyPrivate());
      } else if (campaign.getPrivacy().equals(Privacy.SHARED)) {
        privacySpan.setInnerText("SHARED");
        privacySpan.setClassName(style.privacyShared());
      } else {
        // "INVISIBLE" OR UNRECOGNIZED
        privacySpan.setClassName("");        
      }
      
      // only authors see the edit link
      if (canEdit) {
        this.actionLinkEditCampaign.setVisible(true);
        this.actionLinkEditCampaign.setTargetHistoryToken(HistoryTokens.campaignEdit(campaign.getCampaignId()));
      } else {
        this.actionLinkEditCampaign.setVisible(false);
      }

      // hidden form with target set to _blank does a post request to fetch
      // the file and displays the result in a new window. 
      final FormPanel viewForm = new FormPanel("_blank"); // target="_blank" to open new window
      viewForm.setAction("http://localhost:8000/MobilizeWeb/getfile"); // FIXME
      viewForm.setMethod(FormPanel.METHOD_POST);
      final Hidden fmt = new Hidden();
      fmt.setName("fmt"); // if fmt=download, set content-disposition header
      viewForm.add(fmt);
      //formContainer.setVisible(false);
      container.add(viewForm, "hiddenFormContainer");
      // TODO: also pass desired filename as param
      // TODO: username, auth_token, etc also need to go in form fields
      
      // FIXME: display in a popup panel instead of new window
      this.viewXmlInlineLink.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          fmt.setValue("xml");
          viewForm.submit();
        }
      });
      
      this.downloadXmlInlineLink.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          // when fmt is set to download, server should add content-disposition
          // header, which prompts user to save file
          fmt.setValue("download");
          viewForm.submit();
        }
      });

    }
  }
}
