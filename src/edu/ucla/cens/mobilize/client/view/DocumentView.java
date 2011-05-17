package edu.ucla.cens.mobilize.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.model.DocumentInfo;

public interface DocumentView extends IsWidget {

  void showDocumentList(List<DocumentInfo> documents);
  void showDocumentDetail(DocumentInfo document, boolean userCanEdit);
  void showDocumentEdit(DocumentInfo document);
  void showDocumentCreate();
  void showMsg(String msg);
  void showError(String error);
  void hideMsg();


}
