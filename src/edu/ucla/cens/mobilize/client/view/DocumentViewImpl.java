package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DocumentViewImpl extends Composite implements DocumentView {

  private static DocumentViewUiBinder uiBinder = GWT
      .create(DocumentViewUiBinder.class);

  @UiTemplate("DocumentView.ui.xml")
  interface DocumentViewUiBinder extends UiBinder<Widget, DocumentViewImpl> {
  }

  public DocumentViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
