package edu.ucla.cens.mobilize.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.ui.DocumentDetail;
import edu.ucla.cens.mobilize.client.ui.DocumentEditView;
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
  @UiField Button documentUploadButton;
  @UiField DocumentList documentList;
  @UiField DocumentDetail documentDetail;
  @UiField DocumentEditView documentEdit;
  
  public DocumentViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public void showListSubview() {
    hideAllSubviews();
    this.documentList.setVisible(true);
  }

  @Override
  public void showDetailSubview() {
    hideAllSubviews();
    this.documentDetail.setVisible(true);
  }
  
  @Override
  public void showEditSubview() {
    hideAllSubviews();
    this.documentEdit.setVisible(true);
  }
  
  private void hideAllSubviews() {
    this.documentList.setVisible(false);
    this.documentDetail.setVisible(false);
    this.documentEdit.setVisible(false);
  }
  
  @Override
  public void setDocumentList(List<DocumentInfo> documents) {
    this.documentList.setDocuments(documents);
    showListSubview();
  }

  @Override
  public void setDocumentDetail(DocumentInfo documentInfo, boolean canEdit) {
    this.documentDetail.setDocumentDetail(documentInfo, canEdit);
    showDetailSubview();
  }

  @Override
  public void setDocumentEdit(DocumentInfo document) {
    this.documentEdit.setDocument(document);
    showEditSubview();
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
  public DocumentEditView getEditView() {
    return this.documentEdit;
  }

  @Override
  public HasClickHandlers getUploadButton() {
    return this.documentUploadButton;
  }

}
