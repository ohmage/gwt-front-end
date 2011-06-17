package edu.ucla.cens.mobilize.client.view;


import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.event.DocumentDownloadHandler;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.ui.DocumentEditView;

public interface DocumentView extends IsWidget {

  void showListSubview();
  void showDetailSubview();
  void showEditSubview();
  void setDocumentList(List<DocumentInfo> documents);
  void setDocumentDetail(DocumentInfo document, boolean userCanEdit);
  void setDocumentEdit(DocumentInfo document);
  void showMsg(String msg);
  void showError(String error, String detail);
  void hideMsg();
  DocumentEditView getEditView();
  HasClickHandlers getUploadButton();
  void setDocumentDownloadHandler(DocumentDownloadHandler handler);
  void doDocumentDownloadPost(String url, 
                            Map<String, String> params,
                            SubmitCompleteHandler submitCompleteHandler);

}
