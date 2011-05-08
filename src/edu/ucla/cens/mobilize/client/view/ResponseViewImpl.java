package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.ui.ResponseDisclosurePanel;
import edu.ucla.cens.mobilize.client.utils.DateUtils;

public class ResponseViewImpl extends Composite implements ResponseView {
  
  private static ResponseViewUiBinder uiBinder = GWT
      .create(ResponseViewUiBinder.class);

  @UiTemplate("ResponseView.ui.xml")
  interface ResponseViewUiBinder extends UiBinder<Widget, ResponseViewImpl> {
  }

  public interface ResponseViewStyles extends CssResource {
    String sideBarItemSelected();
  }

  @UiField ResponseViewStyles style;
  
  @UiField InlineLabel singleParticipantLabel;
  @UiField ListBox participantFilter;
  @UiField ListBox campaignFilter;
  @UiField ListBox surveyFilter;
  @UiField Label sectionHeaderTitle;
  @UiField Label sectionHeaderDetail;
  @UiField VerticalPanel responseList;
  @UiField Button shareButtonTop;
  @UiField Button makePrivateButtonTop;
  @UiField Button deleteButtonTop;
  @UiField Button shareButtonBottom;
  @UiField Button makePrivateButtonBottom;
  @UiField Button deleteButtonBottom;
  @UiField Anchor selectAllLinkTop;
  @UiField Anchor selectNoneLinkTop;
  @UiField Anchor selectAllLinkBottom;
  @UiField Anchor selectNoneLinkBottom;
  
  @UiField MenuBar leftSideBarMenu;
  @UiField MenuItem privateMenuItem;
  @UiField MenuItem sharedMenuItem;
  @UiField MenuItem allMenuItem;
  
  ResponseView.Presenter presenter;
  Privacy selectedPrivacy = Privacy.UNDEFINED;

  public ResponseViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    setEventHandlers();
  }
  
  private void clearLeftSideBarStyles() {
    this.allMenuItem.setStyleName("");
    this.sharedMenuItem.setStyleName("");
    this.privateMenuItem.setStyleName("");
  }
  
  private void setEventHandlers() {
    privateMenuItem.setCommand(new Command() {
      @Override
      public void execute() {
        selectedPrivacy = Privacy.PRIVATE;
        presenter.onFilterChange();
      }
    });
    
    sharedMenuItem.setCommand(new Command() {
      @Override
      public void execute() {
        selectedPrivacy = Privacy.SHARED;
        presenter.onFilterChange();
      }
    });
    
    allMenuItem.setCommand(new Command() {
      @Override
      public void execute() {
        selectedPrivacy = Privacy.UNDEFINED;
        presenter.onFilterChange();
      }
    });
    
    selectAllLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectAll();
      }
    });
    
    selectNoneLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectNone();
      }
    });
   
    selectAllLinkBottom.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectAll();
      }
    });
    
    selectNoneLinkBottom.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectNone();
      }
    });
  }

  private void selectAll() {
    for (int i = 0; i < responseList.getWidgetCount(); i++) {
      if (responseList.getWidget(i).getClass() == ResponseDisclosurePanel.class) {
        ResponseDisclosurePanel panel = (ResponseDisclosurePanel)responseList.getWidget(i);
        panel.setChecked(true);
      }
    }
  }
  
  private void selectNone() {
    for (int i = 0; i < responseList.getWidgetCount(); i++) {
      if (responseList.getWidget(i).getClass() == ResponseDisclosurePanel.class) {
        ResponseDisclosurePanel panel = (ResponseDisclosurePanel)responseList.getWidget(i);
        panel.setChecked(false);
      }
    }
  }
  
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setParticipantList(List<String> participantNames) {
    participantFilter.clear();
    for (String name : participantNames) {
      participantFilter.addItem(name); // FIXME: two param method to add value?
    }
    
    participantFilter.clear();
    if (participantNames.size() == 1) {
      singleParticipantLabel.setVisible(true);
      singleParticipantLabel.setText(participantNames.get(0));
      participantFilter.setVisible(false);
      
    } else {
      singleParticipantLabel.setVisible(false);
      participantFilter.setVisible(true);
      participantFilter.clear();
      for (String name : participantNames) {
        participantFilter.addItem(name); 
      }
    }
  } 

  @Override
  public void setCampaignList(List<String> campaignNames) {
    campaignFilter.clear();
    for (String name : campaignNames) {
      campaignFilter.addItem(name);
    }
  }

  @Override
  public void setSurveyList(List<String> surveyNames) {
    surveyFilter.clear();
    for (String name : surveyNames) {
      surveyFilter.addItem(name);
    }
  }

  @Override
  public void selectParticipant(String participantName) {
    for (int i = 0; i < participantFilter.getItemCount(); i++) {
      if (participantFilter.getItemText(i) == participantName) {
        participantFilter.setItemSelected(i, true);
        break;
      }
    }
  }

  @Override
  public void selectCampaign(String campaignName) {
    for (int i = 0; i < campaignFilter.getItemCount(); i++) {
      if (campaignFilter.getItemText(i) == campaignName) {
        campaignFilter.setItemSelected(i, true);
        break;
      }
    }
  }

  @Override
  public void selectSurvey(String surveyName) {
    for (int i = 0; i < surveyFilter.getItemCount(); i++) {
      if (surveyFilter.getItemText(i) == surveyName) {
        surveyFilter.setItemSelected(i, true);
        break;
      }
    }
  }
  
  @Override
  public void renderPrivate(List<SurveyResponse> responses) {
    selectedPrivacy = Privacy.PRIVATE;
    clearLeftSideBarStyles();
    privateMenuItem.setStyleName(style.sideBarItemSelected());
    this.sectionHeaderTitle.setText("Private Responses");
    this.sectionHeaderDetail.setText("Visible only to you.");
    renderResponses(responses);
    // FIXME: supervisor should see different text ("Visible only to responder"?)
  }

  @Override
  public void renderShared(List<SurveyResponse> responses) {
    selectedPrivacy = Privacy.SHARED;
    clearLeftSideBarStyles();
    sharedMenuItem.setStyleName(style.sideBarItemSelected());
    this.sectionHeaderTitle.setText("Shared responses");
    this.sectionHeaderDetail.setText("Visible to all campaign participants.");
    renderResponses(responses);
  }

  @Override
  public void renderInvisible(List<SurveyResponse> responses) {
    this.sectionHeaderTitle.setText("Invisible Responses");
    this.sectionHeaderDetail.setText("Visible only to supervisor, not to responder or any other participants.");
    renderResponses(responses);
  }
  
  @Override
  public void renderAll(List<SurveyResponse> responses) {
    selectedPrivacy = Privacy.UNDEFINED;
    clearLeftSideBarStyles();
    allMenuItem.setStyleName(style.sideBarItemSelected());
    this.sectionHeaderTitle.setText("All Responses");
    this.sectionHeaderDetail.setText("Private responses are visible only to you. " +
                                     "Shared responses are visible to all participants. ");
    renderResponses(responses);
  }
  
  
  private void renderResponses(List<SurveyResponse> responses) {
    this.responseList.clear();
    for (SurveyResponse response : responses) {
      ResponseDisclosurePanel responseWidget = new ResponseDisclosurePanel();
      responseWidget.setCampaignName(response.getCampaignName());
      responseWidget.setDate(response.getResponseDate());
      responseWidget.setPrivacy(response.getPrivacyState());
      responseWidget.setResponseKey(response.getResponseKey());
      responseWidget.setSurveyName(response.getSurveyName());
      for (PromptResponse promptResponse : response.getPromptResponses()) {
        switch (promptResponse.getPromptType()) {
          case TIMESTAMP:
            Date timestamp = DateUtils.translateFromServerFormat(promptResponse.getResponseRaw());
            responseWidget.addPromptResponseTimestamp(promptResponse.getText(), timestamp);
            break;
          case PHOTO:
            responseWidget.addPromptResponsePhoto(promptResponse.getText(), promptResponse.getResponsePrepared());
            break;
          default:
            responseWidget.addPromptResponseText(promptResponse.getText(), promptResponse.getResponsePrepared());
            break;
        }
      }
      this.responseList.add(responseWidget);
      
    }
  }

  @Override
  public String getSelectedParticipant() {
    int index = this.participantFilter.getSelectedIndex();
    return (index > -1) ? this.participantFilter.getValue(index) : "";
  }

  @Override
  public String getSelectedCampaign() {
    int index = this.campaignFilter.getSelectedIndex();
    return (index > -1) ? this.campaignFilter.getValue(index) : "";
  }

  @Override
  public String getSelectedSurvey() {
    int index = this.surveyFilter.getSelectedIndex();
    return (index > -1) ? this.surveyFilter.getValue(index) : "";
  }

  @Override
  public Privacy getSelectedPrivacyState() {
    return this.selectedPrivacy;
  }

  @Override
  public List<HasClickHandlers> getShareButtons() {
    List<HasClickHandlers> retval = new ArrayList<HasClickHandlers>();
    retval.add(this.shareButtonTop);
    retval.add(this.shareButtonBottom);
    return retval;
  }

  @Override
  public List<HasClickHandlers> getMakePrivateButtons() {
    List<HasClickHandlers> retval = new ArrayList<HasClickHandlers>();
    retval.add(this.makePrivateButtonTop);
    retval.add(this.makePrivateButtonBottom);
    return retval;
  }

  @Override
  public List<HasClickHandlers> getDeleteButtons() {
    List<HasClickHandlers> retval = new ArrayList<HasClickHandlers>();
    retval.add(this.deleteButtonTop);
    retval.add(this.deleteButtonBottom);
    return retval;
  }

  @Override
  public List<HasValueChangeHandlers<String>> getFilters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getSelectedSurveyResponseKeys() {
    List<String> keys = new ArrayList<String>();
    for (int i = 0; i < responseList.getWidgetCount(); i++) {
      if (responseList.getWidget(i).getClass() == ResponseDisclosurePanel.class) {
        ResponseDisclosurePanel panel = (ResponseDisclosurePanel)responseList.getWidget(i);
        if (panel.isSelected()) {
          keys.add(panel.getResponseKey());
        }
      }
    }    
    return keys;
  }

  @Override
  public void clearResponseList() {
    this.responseList.clear();
  }

  
}
