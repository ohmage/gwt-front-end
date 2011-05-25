package edu.ucla.cens.mobilize.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
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
  @UiField UListElement errorList;
  
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
    errorList.setInnerHTML("");
    msg.setText(infoMsg);
    msgBox.setVisible(true);
    msgBox.setStyleName(style.msgBox() + " " + style.info());
  }
  
  public void showErrorMessage(String errorMsg) {
    showErrorMessage(errorMsg, null);
  }
  
  public void showErrorMessage(String errorMsg, List<String> errors) {
    errorList.setInnerHTML("");
    msg.setText(errorMsg);
    msgBox.setVisible(true);
    msgBox.setStyleName(style.msgBox() + " " + style.error());
    if (errors != null && !errors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (String error : errors) {
        sb.append("<li>").append(error).append("</li>");
      }
      errorList.setInnerHTML(sb.toString());
    }
  }
  
  public void hide() {
    msg.setText("");
    msgBox.setVisible(false);
  }
}
