package edu.ucla.cens.mobilize.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.ui.DocumentDetail;
import edu.ucla.cens.mobilize.client.ui.DocumentEdit;
import edu.ucla.cens.mobilize.client.ui.DocumentList;
import edu.ucla.cens.mobilize.client.ui.MessageWidget;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;

public class DocumentViewImpl extends Composite implements DocumentView {

  private static DocumentViewUiBinder uiBinder = GWT
      .create(DocumentViewUiBinder.class);

  @UiTemplate("DocumentView.ui.xml")
  interface DocumentViewUiBinder extends UiBinder<Widget, DocumentViewImpl> {
  }

  @UiField MessageWidget msgWidget;
  @UiField DocumentList documentList;
  @UiField DocumentDetail documentDetail;
  @UiField DocumentEdit documentEdit;
  
  public DocumentViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  private void showListSubview() {
    hideAllSubviews();
    this.documentList.setVisible(true);
  }
  
  private void showDetailSubview() {
    hideAllSubviews();
    this.documentDetail.setVisible(true);
  }
  
  private void showEditSubview() {
    hideAllSubviews();
    this.documentEdit.setVisible(true);
  }
  
  private void hideAllSubviews() {
    this.documentList.setVisible(false);
    this.documentDetail.setVisible(false);
    this.documentEdit.setVisible(false);
  }
  
  @Override
  public void showDocumentList(List<DocumentInfo> documents) {
    this.documentList.setDocuments(documents);
    showListSubview();
  }

  @Override
  public void showMsg(String msg) {
    this.msgWidget.showInfoMessage(msg);
  }

  @Override
  public void showError(String error) {
    this.msgWidget.showErrorMessage(error);
  }

  @Override
  public void hideMsg() {
    this.msgWidget.hide();
  }

  @Override
  public void showDocumentDetail(DocumentInfo documentInfo, boolean canEdit) {
    this.documentDetail.setDocumentDetail(documentInfo, canEdit);
    showDetailSubview();
  }

  @Override
  public void showDocumentEdit(DocumentInfo document) {

  }

  @Override
  public void showDocumentCreate() {
    // TODO Auto-generated method stub
    
  }

}
