package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HelpView extends Composite {

  private static HelpViewUiBinder uiBinder = GWT.create(HelpViewUiBinder.class);

  interface HelpViewUiBinder extends UiBinder<Widget, HelpView> {
  }

  public HelpView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
