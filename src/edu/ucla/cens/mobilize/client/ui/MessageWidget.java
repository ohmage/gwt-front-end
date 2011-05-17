package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
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
    msg.setText(infoMsg);
    msgBox.setVisible(true);
    msgBox.setStyleName(style.msgBox() + " " + style.info());
  }
  
  public void showErrorMessage(String errorMsg) {
    msg.setText(errorMsg);
    msgBox.setVisible(true);
    msgBox.setStyleName(style.msgBox() + " " + style.error());
  }
  
  public void hide() {
    msg.setText("");
    msgBox.setVisible(false);
  }

}
