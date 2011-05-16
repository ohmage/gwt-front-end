package edu.ucla.cens.mobilize.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface DocumentView extends IsWidget {

  void showDocumentList(List<DocumentInfo> documents);
  void showMsg(String msg);
  void showError(String error);
  void hideMsg();

}
