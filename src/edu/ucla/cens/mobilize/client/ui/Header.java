package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Composite {

  private static HeaderUiBinder uiBinder = GWT
      .create(HeaderUiBinder.class);

  @UiField InlineLabel userNameLabel;
  @UiField InlineLabel appNameLabel;
  @UiField Image appNameImage;
  @UiField Button logoutButton;
  
  interface HeaderUiBinder extends UiBinder<Widget, Header> {
  }

  public Header() {
    initWidget(uiBinder.createAndBindUi(this));
    
    appNameImage.addClickHandler(new ClickHandler() {
    	@Override
    	public void onClick(ClickEvent event) {
    		History.newItem("dashboard");
    	}
    });
    
    logoutButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
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
  
  public void useAppLogo(boolean useLogo) {
    appNameImage.setVisible(useLogo);
    appNameLabel.setVisible(!useLogo);
  }
}
