package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PasswordChangeInstructions extends Composite {

  private static PasswordChangeInstructionsUiBinder uiBinder = GWT
      .create(PasswordChangeInstructionsUiBinder.class);

  interface PasswordChangeInstructionsUiBinder extends
      UiBinder<Widget, PasswordChangeInstructions> {
  }

  public PasswordChangeInstructions() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
