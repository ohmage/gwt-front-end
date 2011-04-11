package edu.ucla.cens.mobilize.client.view;

import java.util.List;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.ui.ResponseDisclosurePanel;

public class ResponseViewImpl extends Composite implements ResponseView {
  
  private static ResponseViewUiBinder uiBinder = GWT
      .create(ResponseViewUiBinder.class);

  @UiTemplate("ResponseView.ui.xml")
  interface ResponseViewUiBinder extends UiBinder<Widget, ResponseViewImpl> {
  }

  @UiField InlineLabel singleParticipantLabel;
  @UiField ListBox participantFilter;
  @UiField ListBox campaignFilter;
  @UiField ListBox surveyFilter;
  //@UiField Label descriptionLabel;
  @UiField Label sectionHeaderTitle;
  @UiField Label sectionHeaderDetail;
  @UiField VerticalPanel responseList;
  @UiField Button shareButtonTop;
  @UiField Button unshareButtonTop;
  @UiField Button deleteButtonTop;
  @UiField Button shareButtonBottom;
  @UiField Button unshareButtonBottom;
  @UiField Button deleteButtonBottom;
  @UiField Anchor selectAllLinkTop;
  @UiField Anchor selectNoneLinkTop;
  @UiField Anchor selectAllLinkBottom;
  @UiField Anchor selectNoneLinkBottom;
  
  @UiField MenuItem privateMenuItem;
  @UiField MenuItem publicMenuItem;
  @UiField MenuItem allMenuItem;
  
  ResponseView.Presenter presenter;

  public ResponseViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    setEventHandlers();
  }
  
  private void setEventHandlers() {
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
    // FIXME: if there's only one participant, this should not be a dropdown
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
    // TODO: private/public styles
    this.responseList.clear();
    for (SurveyResponse response : responses) {
      // widget shows survey title, date, etc. when clicked it reveals full response
      ResponseDisclosurePanel responseWidget = new ResponseDisclosurePanel();
      responseWidget.setResponse(response);
      this.responseList.add(responseWidget);
    }
    this.sectionHeaderTitle.setText("Private Responses");
    this.sectionHeaderDetail.setText("Visible only to you.");
  }

  @Override
  public void renderPublic(List<SurveyResponse> responses) {
    // TODO: private/public styles
    this.responseList.clear();
    for (SurveyResponse response : responses) {
      ResponseDisclosurePanel responseWidget = new ResponseDisclosurePanel();
      responseWidget.setResponse(response);
      this.responseList.add(responseWidget);
    }
  }

  @Override
  public void renderInvisible(List<SurveyResponse> responses) {
    // TODO Auto-generated method stub
  }

  @Override
  public String getSelectedParticipant() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getSelectedCampaign() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getSelectedSurvey() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Privacy getSelectedPrivacyState() {
    // TODO Auto-generated method stub
    return null;
  }


  
}
