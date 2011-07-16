package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.utils.AwUrlBasedResourceUtils;

public class ResponseWidgetPopup extends Composite {

  private static ResponseWidgetPopupUiBinder uiBinder = GWT
      .create(ResponseWidgetPopupUiBinder.class);

  interface ResponseWidgetPopupStyle extends CssResource {
    String promptContainer();
    String prompt();
    String promptImage();
    String promptQuestion();
    String promptResponse();
  }
  
  interface ResponseWidgetPopupUiBinder extends
      UiBinder<Widget, ResponseWidgetPopup> {
  }

  @UiField Label date;
  @UiField InlineLabel campaign;
  @UiField InlineLabel survey;
  //@UiField InlineLabel user;
  @UiField FlowPanel prompts;
  
  @UiField ResponseWidgetPopupStyle style;
  
  public ResponseWidgetPopup() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setResponse(SurveyResponse surveyResponse) {
    date.setText(surveyResponse.getResponseDate().toString());
    campaign.setText(surveyResponse.getCampaignName());
    survey.setText(surveyResponse.getSurveyName());
    //user.setText(surveyResponse.getUserName());
    
    for (PromptResponse promptResponse : surveyResponse.getPromptResponses()) {
      Widget responseDisplayWidget = null;
      switch (promptResponse.getPromptType()) {
      case PHOTO:
        String raw = promptResponse.getResponseRaw();
        if (raw.equals("SKIPPED") || raw.equals("NOT_DISPLAYED")) {
          responseDisplayWidget = new HTML(raw);
        } else {
          // generate urls for thumbnail and full sized photo and pass to widget
          String thumbUrl = AwUrlBasedResourceUtils.getImageUrl(promptResponse.getResponseRaw(), 
              surveyResponse.getUserName(),
              surveyResponse.getCampaignId(),
              AwUrlBasedResourceUtils.ImageSize.SMALL);
          final String fullSizedImageUrl = AwUrlBasedResourceUtils.getImageUrl(promptResponse.getResponseRaw(), 
              surveyResponse.getUserName(),
              surveyResponse.getCampaignId(),
              AwUrlBasedResourceUtils.ImageSize.ORIGINAL);
          Image img = new Image(thumbUrl);
          img.setStyleName(style.promptImage());
          img.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
              Window.open(fullSizedImageUrl, "_blank", "");
            }
          });
          FlowPanel panel = new FlowPanel();
          panel.add(img);
          responseDisplayWidget = panel;
        }
        break;
      // TODO: special case timestamp?
      default:
        // anything other than a photo, just copy it verbatim
        responseDisplayWidget = new HTML(promptResponse.getResponsePrepared());
        break;
      }
      
      // set up and style question
      HTML question = new HTML(promptResponse.getText());
      question.setStyleName(style.promptQuestion());

      // add style to response
      responseDisplayWidget.setStyleName(style.promptResponse());
      
      // add question and response to styled div
      FlowPanel panel = new FlowPanel();
      panel.setStyleName(style.prompt());
      panel.add(question);
      panel.add(responseDisplayWidget);
      
      // add the whole thing to prompt list
      prompts.add(panel);
      
    }
  }
  
  
}
