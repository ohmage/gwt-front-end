package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Composite {

  private static AccountMenuUiBinder uiBinder = GWT
      .create(AccountMenuUiBinder.class);

  @UiField InlineLabel userNameLabel;
  @UiField InlineLabel appNameLabel;
  @UiField Button logoutButton;
  
  interface AccountMenuUiBinder extends UiBinder<Widget, Header> {
  }

  public Header() {
    initWidget(uiBinder.createAndBindUi(this));
    
    logoutButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // FIXME: eventbus instead
        History.newItem("logout");
      }
    });
  }

  public void setUserName(String userName) {
    userNameLabel.setText(userName);
  }
  
  public void setAppName(String appName) {
    appNameLabel.setText(appName);
  }
  
  
}
