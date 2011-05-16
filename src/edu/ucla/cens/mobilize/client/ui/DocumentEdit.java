package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DocumentEdit extends Composite {

  private static DocumentEditUiBinder uiBinder = GWT
      .create(DocumentEditUiBinder.class);

  interface DocumentEditUiBinder extends UiBinder<Widget, DocumentEdit> {
  }

  public DocumentEdit() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
