package edu.ucla.cens.mobilize.client.view;

import java.util.List;

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

  @Override
  public void showDocumentList(List<DocumentInfo> documents) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void showMsg(String msg) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void showError(String error) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void hideMsg() {
    // TODO Auto-generated method stub
    
  }

}
