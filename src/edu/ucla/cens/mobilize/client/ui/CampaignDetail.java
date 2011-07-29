package edu.ucla.cens.mobilize.client.ui;


import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.utils.XmlUtils;

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
  @UiField InlineLabel creationDate;
  @UiField InlineLabel desc;
  @UiField VerticalPanel classes;
  @UiField VerticalPanel authors;
  @UiField SpanElement runningStateSpan;
  @UiField SpanElement privacySpan;
  @UiField Anchor actionLinkViewXml; 
  @UiField Anchor actionLinkDownloadXml;
  @UiField Hyperlink actionLinkEditCampaign;
  @UiField Anchor actionLinkExportResponses;
  @UiField Anchor viewXmlInlineLink;
  @UiField Anchor downloadXmlInlineLink;
  @UiField HTMLPanel container;
  @UiField Anchor backLinkTop;
  @UiField Anchor backLinkBottom;

  DataService dataService; // FIXME: use presenter instead
  String campaignXml;
  
  public CampaignDetail() {
    initWidget(uiBinder.createAndBindUi(this));
    bind();
  }

  public void setDataService(DataService dataService) {
    this.dataService = dataService;
  }
  
  private void bind() {
    this.actionLinkViewXml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        showXmlInNewWindow(campaignXml);
      }
    });
    
    this.actionLinkDownloadXml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        downloadXml(campaignUrn.getText());
      }
    });
    
    this.actionLinkExportResponses.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        exportCsv(campaignUrn.getText());
      }
    });
    
    this.viewXmlInlineLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        showXmlInNewWindow(campaignXml);
      }
    });
    
    this.downloadXmlInlineLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        downloadXml(campaignUrn.getText());
      }
    });
    
    this.backLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
    
    this.backLinkBottom.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
  }
  
  private void showXmlInNewWindow(String xml) {
    final DialogBox popup = new DialogBox();
    popup.setText("Xml Config for Campaign: " + this.campaignName.getText());
    popup.setGlassEnabled(true);
    VerticalPanel vertical = new VerticalPanel();
    HTMLPanel topButtons = new HTMLPanel("<div id='topButtonContainer' style='text-align:right;'></div>");
    Button closeButton = new Button("Close");
    closeButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        popup.hide();
      }
    });
    topButtons.add(closeButton, "topButtonContainer");
    vertical.add(topButtons);
    TextArea textArea = new TextArea();
    textArea.setSize("500px", "500px");
    String prettyXml = XmlUtils.prettyPrint(xml);
    textArea.setText(prettyXml); // text area renders xml nicely
    textArea.setReadOnly(true);
    vertical.add(textArea);
    popup.add(vertical);
    popup.center();
  }
  
  private void downloadXml(String campaignId) {
    FormPanel downloadForm = new FormPanel("_blank"); // _blank opens new window
    downloadForm.setAction(AwConstants.getCampaignReadUrl());
    downloadForm.setMethod(FormPanel.METHOD_POST);
    FlowPanel innerContainer = new FlowPanel();
    Map<String, String> params = dataService.getCampaignXmlDownloadParams(campaignId);
    for (String paramName : params.keySet()) {
      Hidden field = new Hidden();
      field.setName(paramName);
      field.setValue(params.get(paramName));
      innerContainer.add(field);
    }
    downloadForm.add(innerContainer);
    this.container.add(downloadForm, "formPanelContainer"); // second arg needed in compiled version
    downloadForm.submit();
    downloadForm.removeFromParent();
  }
  
  private void exportCsv(String campaignId) {
    assert dataService != null : "DataService is null. Did you forget to call CampaignDetail.setDataService?";
    FormPanel exportForm = new FormPanel("_blank"); // won't work in firefox without the "_blank"
    exportForm.setAction(AwConstants.getSurveyResponseReadUrl()); 
    exportForm.setMethod(FormPanel.METHOD_POST);
    FlowPanel innerContainer = new FlowPanel();
    
    Map<String, String> params = dataService.getSurveyResponseExportParams(campaignId);
    for (String paramName : params.keySet()) {
      Hidden field = new Hidden();
      field.setName(paramName);
      field.setValue(params.get(paramName));
      innerContainer.add(field);
    }
    exportForm.add(innerContainer);
    container.add(exportForm, "formPanelContainer"); 
    exportForm.submit();
    exportForm.removeFromParent();
  }
  
  public void setCampaign(CampaignDetailedInfo campaign) {
    boolean canEdit = campaign.userCanEdit();
    boolean canAnalyze = campaign.userCanAnalyze();
    if (campaign != null) {
      // copy info from data obj into fields
      this.campaignName.setText(campaign.getCampaignName());
      this.campaignUrn.setText(campaign.getCampaignId());
      this.creationDate.setText(campaign.getCreationTime().toString());
      this.campaignXml = campaign.getXmlConfig();
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
      
      this.actionLinkExportResponses.setVisible(canAnalyze);

    }
  }
}
