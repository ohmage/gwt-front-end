package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class MessageWidget extends Composite {

  private static MessageWidgetUiBinder uiBinder = GWT
      .create(MessageWidgetUiBinder.class);
  
  interface MessageWidgetUiBinder extends UiBinder<Widget, MessageWidget> {
  }

  public interface MessageWidgetStyles extends CssResource {
    String error();
    String info();
    String msgBox();
  }
  
  @UiField HTMLPanel msgBox;
  @UiField InlineLabel msg;
  @UiField Anchor hideLink;
  @UiField MessageWidgetStyles style;
  @UiField UListElement errorListElement;

  private List<String> errorList = new ArrayList<String>();
  
  public MessageWidget() {
    initWidget(uiBinder.createAndBindUi(this));
    bind();
    hide();
  }
  
  private void bind() {
    hideLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hide();
      }
    });
  }
  
  public void showInfoMessage(String infoMsg) {
    clearErrors();
    msg.setText(infoMsg);
    msgBox.setVisible(true);
    msgBox.setStyleName(style.msgBox() + " " + style.info());
  }
  
  public void showErrorMessage(String errorMsg) {
    clearErrors();
    showErrorMessage(errorMsg, null);
  }
  
  public void showErrorMessage(String errorMsg, List<String> errors) {
    clearErrors();
    for (String errorDetail : errors) {
      addError(errorMsg, errorDetail);
    }
  }

  public void addError(String errorMsg, String errorDetail) {
    // if error detail is not already listed, add it
    if (errorDetail != null) {
      boolean isNewError = true;
      for (String knownError : errorList) {
        if (knownError.equals(errorDetail)) isNewError = false;
      }
      if (isNewError) errorList.add(errorDetail);
    }
    // set general error message, overwriting existing message, if any
    msg.setText(errorMsg);
    msgBox.setVisible(true);
    msgBox.setStyleName(style.msgBox() + " " + style.error());
    StringBuilder sb = new StringBuilder();
    for (String error : errorList) {
      sb.append("<li>").append(error).append("</li>");
    }
    errorListElement.setInnerHTML(sb.toString());
  }
  
  public void hide() {
    msg.setText("");
    errorList.clear();
    msgBox.setVisible(false);
  }
  
  public void clearErrors() {
    errorList.clear();
    errorListElement.setInnerHTML("");
  }
}
