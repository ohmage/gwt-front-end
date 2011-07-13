package edu.ucla.cens.mobilize.client.ui;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;

public class ResponseWidgetFull extends Composite implements ResponseDisplayWidget {

  public interface ResponseWidgetStyle extends CssResource {
    String privacyPrivate();
    String privacyShared();
    String prompt();
    String promptQuestion();
    String promptResponse();
    String promptImage();
    String selected();
  }
  
  private static ResponseWidgetFullUiBinder uiBinder = GWT
      .create(ResponseWidgetFullUiBinder.class);

  interface ResponseWidgetFullUiBinder extends
      UiBinder<Widget, ResponseWidgetFull> {
  }

  @UiField ResponseWidgetStyle style;
  @UiField Hidden responseKeyHiddenField;
  @UiField CheckBox checkBox;
  @UiField InlineLabel dateLabel;
  @UiField InlineLabel campaignLabel;
  @UiField InlineLabel surveyLabel;
  @UiField InlineLabel privacyLabel;
  @UiField FlowPanel promptContainer;
  @UiField HorizontalPanel imageContainer;
  
  public ResponseWidgetFull() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public int getResponseKey() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isSelected() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean setSelected(boolean isSelected) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setPrivacyState(Privacy privacy) {
    privacyLabel.setText(privacy.toUserFriendlyString());
    switch (privacy) {
    case PRIVATE:
      privacyLabel.setStyleName(style.privacyPrivate());
      break;
    case SHARED:
      privacyLabel.setStyleName(style.privacyShared());
      break;
    default:
      privacyLabel.setStyleName("");
      break;
    }
  }

  @Override
  public void setResponse(SurveyResponse response) {
    responseKeyHiddenField.setValue(Integer.toString(response.getResponseKey()));
    dateLabel.setText(response.getResponseDate().toString());
    campaignLabel.setText(response.getCampaignName());
    surveyLabel.setText(response.getSurveyName());
    setPrivacyState(response.getPrivacyState());
    for (PromptResponse prompt : response.getPromptResponses()) {
      addPromptResponse(prompt);
    }
  }

  private void addPromptResponse(PromptResponse prompt) {
    String response = "";
    switch (prompt.getPromptType()) {
    case PHOTO:
      Image img = new Image(prompt.getResponsePrepared());
      img.setStyleName(style.promptImage());
      response = img.toString();
      break;
    // TODO: special case timestamp?
    default:
      response = prompt.getResponsePrepared();
      break;
    }
    
    StringBuilder sb = new StringBuilder();
    sb.append("<div class='" + style.promptQuestion() +"'>");
      sb.append(prompt.getText());
    sb.append("</div>");
    sb.append("<div class='" + style.promptResponse() +"'>");
      sb.append(response);
    sb.append("</div>");
    
    HTML html = new HTML(sb.toString());
    html.setStyleName(style.prompt());
    promptContainer.add(html);
    
  }
  
  private void addImage(String url, int promptIndex) {
  }
}
