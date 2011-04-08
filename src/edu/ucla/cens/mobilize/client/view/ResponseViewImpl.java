package edu.ucla.cens.mobilize.client.view;

import java.util.List;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.ui.ResponseDisclosurePanel;

public class ResponseViewImpl extends Composite implements ResponseView {

  private static ResponseViewUiBinder uiBinder = GWT
      .create(ResponseViewUiBinder.class);

  @UiTemplate("ResponseView.ui.xml")
  interface ResponseViewUiBinder extends UiBinder<Widget, ResponseViewImpl> {
  }
  
  @UiField ListBox participantFilter;
  @UiField ListBox campaignFilter;
  @UiField ListBox surveyFilter;
  @UiField VerticalPanel privateList;
  @UiField VerticalPanel publicList;
  @UiField Button shareButton;
  @UiField Button unshareButton;
  @UiField Button deletePrivateButton;
  @UiField Button deletePublicButton;
  @UiField Anchor selectAllPublicLink;
  @UiField Anchor selectAllPrivateLink;
  @UiField Anchor selectNonePublicLink;
  @UiField Anchor selectNonePrivateLink;
  
  ResponseView.Presenter presenter;

  public ResponseViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    setEventHandlers();
  }
  
  private void setEventHandlers() {
    selectAllPublicLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectAllPublic();
      }
    });
    
    selectAllPrivateLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectAllPrivate();
      }
    });
    
    selectNonePublicLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectNonePublic();
      }
    });
    
    selectNonePrivateLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectNonePrivate();
      }
    });
  }

  private void selectAllPublic() {
    for (int i = 0; i < publicList.getWidgetCount(); i++) {
      if (publicList.getWidget(i).getClass() == ResponseDisclosurePanel.class) {
        ResponseDisclosurePanel panel = (ResponseDisclosurePanel)publicList.getWidget(i);
        panel.setChecked(true);
      }
    }
  }
  
  private void selectAllPrivate() {
    for (int i = 0; i < privateList.getWidgetCount(); i++) {
      if (privateList.getWidget(i).getClass() == ResponseDisclosurePanel.class) {
        ResponseDisclosurePanel panel = (ResponseDisclosurePanel)privateList.getWidget(i);
        panel.setChecked(true);
      }
    }
  }
  
  private void selectNonePublic() {
    for (int i = 0; i < publicList.getWidgetCount(); i++) {
      if (publicList.getWidget(i).getClass() == ResponseDisclosurePanel.class) {
        ResponseDisclosurePanel panel = (ResponseDisclosurePanel)publicList.getWidget(i);
        panel.setChecked(false);
      }
    }
  }
  
  private void selectNonePrivate() {
    for (int i = 0; i < privateList.getWidgetCount(); i++) {
      if (privateList.getWidget(i).getClass() == ResponseDisclosurePanel.class) {
        ResponseDisclosurePanel panel = (ResponseDisclosurePanel)privateList.getWidget(i);
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
    this.privateList.clear();
    for (SurveyResponse response : responses) {
      ResponseDisclosurePanel responseWidget = new ResponseDisclosurePanel();
      responseWidget.setResponse(response);
      this.privateList.add(responseWidget);
    }
  }

  @Override
  public void renderPublic(List<SurveyResponse> responses) {
    this.publicList.clear();
    for (SurveyResponse response : responses) {
      ResponseDisclosurePanel responseWidget = new ResponseDisclosurePanel();
      responseWidget.setResponse(response);
      this.publicList.add(responseWidget);
    }
  }


  
}
