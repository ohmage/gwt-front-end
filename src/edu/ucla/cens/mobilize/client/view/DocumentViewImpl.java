package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.ui.DocumentDetail;
import edu.ucla.cens.mobilize.client.ui.DocumentEditView;
import edu.ucla.cens.mobilize.client.ui.DocumentList;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.ui.MessageWidget;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.event.DocumentDownloadHandler;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;

public class DocumentViewImpl extends Composite implements DocumentView {

  private static DocumentViewUiBinder uiBinder = GWT
      .create(DocumentViewUiBinder.class);

  @UiTemplate("DocumentView.ui.xml")
  interface DocumentViewUiBinder extends UiBinder<Widget, DocumentViewImpl> {
  }

  @UiField HTMLPanel centerContainer;
  @UiField InlineHyperlink myDocumentsLink;
  @UiField InlineHyperlink browseDocumentsLink;
  @UiField InlineHyperlink uploadLink;
  @UiField MessageWidget msgWidget;
  @UiField Button documentUploadButton;
  @UiField DocumentList documentList;
  @UiField DocumentDetail documentDetail;
  @UiField DocumentEditView documentEdit;

  public DocumentViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    // set up hyperlinks in top nav bar
    myDocumentsLink.setTargetHistoryToken(HistoryTokens.documentListMy());
    browseDocumentsLink.setTargetHistoryToken(HistoryTokens.documentListAll());
    uploadLink.setTargetHistoryToken(HistoryTokens.documentCreate());
    showListSubview();
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
  public void showError(String error, String detail) {
    ErrorDialog.show(error, detail);
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

  @Override
  public void doDocumentDownloadPost(String url, 
                                   Map<String, String> params,
                                   SubmitCompleteHandler submitCompleteHandler) {
    // NOTE: new form is created for each b/c multiple downloads 
    // can take place concurrently
    FormPanel form = new FormPanel("_blank");
    //FormPanel form = new FormPanel();
    form.setAction(url);
    form.setMethod(FormPanel.METHOD_POST);
    form.setEncoding(FormPanel.ENCODING_URLENCODED);
    FlowPanel innerContainer = new FlowPanel();
    for (String paramName : params.keySet()) {
      Hidden field = new Hidden();
      field.setName(paramName);
      field.setValue(params.get(paramName));
      innerContainer.add(field);
    }
    form.add(innerContainer);
    form.addSubmitCompleteHandler(submitCompleteHandler);
    this.centerContainer.add(form, "formPanelContainer");
    form.submit();
    form.removeFromParent();
    // does gwt clean this up when done?
  }

  @Override
  public void setDocumentDownloadHandler(DocumentDownloadHandler handler) {
    // both list and detail have download links - make sure they work as expected
    this.documentList.setDocumentDownloadHandler(handler);
    this.documentDetail.setDocumentDownloadHandler(handler);
  }

}
