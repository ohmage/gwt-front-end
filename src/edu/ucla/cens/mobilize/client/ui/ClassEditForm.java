package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ClassEditForm extends Composite {

  private static ClassEditFormUiBinder uiBinder = GWT
      .create(ClassEditFormUiBinder.class);

  interface ClassEditFormUiBinder extends UiBinder<Widget, ClassEditForm> {
  }

  public ClassEditForm() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
