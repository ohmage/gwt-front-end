package edu.ucla.cens.mobilize.client.ui;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.utils.AwUrlBasedResourceUtils;

public class ResponseWidgetBasic extends Composite implements ResponseDisplayWidget {

  public interface ResponseWidgetStyle extends CssResource {
    String privacyPrivate();
    String privacyShared();
    String privacyInvisible();
    String prompt();
    String promptQuestion();
    String promptResponse();
    String promptImage();
    String selected();
  }
  
  private static ResponseWidgetBasicUiBinder uiBinder = GWT
      .create(ResponseWidgetBasicUiBinder.class);

  interface ResponseWidgetBasicUiBinder extends
      UiBinder<Widget, ResponseWidgetBasic> {
  }

  @UiField ResponseWidgetStyle style;
  @UiField HTMLPanel container;
  @UiField Hidden responseKeyHiddenField;
  @UiField CheckBox checkBox;
  @UiField InlineLabel dateLabel;
  @UiField InlineLabel userLabel;
  @UiField InlineLabel campaignLabel;
  @UiField InlineLabel surveyLabel;
  @UiField InlineLabel privacyLabel;
  @UiField HTMLPanel promptContainerWrapper;
  @UiField FlowPanel promptContainer;
  
  private SurveyResponse surveyResponseData;
  
  public ResponseWidgetBasic() {
    initWidget(uiBinder.createAndBindUi(this));
    bind();
  }
  
  private void bind() {
    checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        setSelectedStyle(event.getValue());
      }
    });
  }
  
  // show/hide the checkbox
  public void setSelectable(boolean isSelectable) {
    this.checkBox.setVisible(isSelectable);
  }

  @Override
  public String getResponseKey() {
    return responseKeyHiddenField.getValue();
  }

  @Override
  public boolean isSelected() {
    return checkBox.getValue();
  }

  @Override
  public void setSelected(boolean isSelected) {
    checkBox.setValue(isSelected);
    setSelectedStyle(isSelected);
  }
  
  private void setSelectedStyle(boolean isSelected) {
    if (isSelected) {
      container.addStyleName(style.selected());
    } else {
      container.removeStyleName(style.selected());
    }
  }
    

  @Override
  public void setPrivacy(Privacy privacy) {
    privacyLabel.setText(privacy.toUserFriendlyString());
    switch (privacy) {
    case PRIVATE:
      privacyLabel.setStyleName(style.privacyPrivate());
      break;
    case SHARED:
      privacyLabel.setStyleName(style.privacyShared());
      break;
    case INVISIBLE:
      privacyLabel.setStyleName(style.privacyInvisible());
      break;
    default:
      privacyLabel.setStyleName("");
      break;
    }
  }

  @Override
  public void setResponse(SurveyResponse response) {
    this.surveyResponseData = response;
    responseKeyHiddenField.setValue(response.getResponseKey());
    userLabel.setText(response.getUserName());
    dateLabel.setText(response.getResponseDate().toString());
    campaignLabel.setText(response.getCampaignName());
    surveyLabel.setText(response.getSurveyName());
    setPrivacy(response.getPrivacyState());
    for (PromptResponse prompt : response.getPromptResponses()) {
      addPromptResponse(prompt);
    }
  }

  private void addPromptResponse(PromptResponse prompt) {
    Widget responseDisplayWidget = null;
    switch (prompt.getPromptType()) {
    case PHOTO:
      String raw = prompt.getResponseRaw();
      if (raw.equals("SKIPPED") || raw.equals("NOT_DISPLAYED") || raw.equals("MEDIA_NOT_UPLOADED")) {
        responseDisplayWidget = new HTML(raw);
      } else {
        // generate urls for thumbnail and full sized photo and pass to widget
        String thumbUrl = AwUrlBasedResourceUtils.getImageUrl(prompt.getResponseRaw(), 
            surveyResponseData.getUserName(),
            surveyResponseData.getCampaignId(),
            AwUrlBasedResourceUtils.ImageSize.SMALL);
        final String fullSizedImageUrl = AwUrlBasedResourceUtils.getImageUrl(prompt.getResponseRaw(), 
            surveyResponseData.getUserName(),
            surveyResponseData.getCampaignId(),
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
      responseDisplayWidget = new HTML(prompt.getResponsePrepared());
      break;
    }
    
    // set up and style question
    HTML question = new HTML(prompt.getText());
    question.setStyleName(style.promptQuestion());

    // add style to response
    responseDisplayWidget.setStyleName(style.promptResponse());
    
    // add question and response to styled div
    FlowPanel panel = new FlowPanel();
    panel.setStyleName(style.prompt());
    panel.add(question);
    panel.add(responseDisplayWidget);
    
    // add the whole thing to prompt list
    promptContainer.add(panel);
    
  }

  @Override
  public void expand() {
    this.promptContainerWrapper.setVisible(true);
  }

  @Override
  public void collapse() {
    this.promptContainerWrapper.setVisible(false);
  }

}
