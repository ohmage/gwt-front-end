package edu.ucla.cens.mobilize.client.presenter;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.MainApp;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.view.DocumentView;

public class DocumentPresenter implements Presenter {

  UserInfo userInfo;
  DataService dataService;
  EventBus eventBus;
  
  DocumentView view;

  // Logging utility
  private static Logger _logger = Logger.getLogger(DocumentPresenter.class.getName());
  
  public DocumentPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  @Override
  public void go(Map<String, List<String>> params) {
    // hide any leftover notifications
    this.view.hideMsg();
    
    // get subview from url params
    if (params.isEmpty()) {
      this.fetchAndShowAllDocuments();
    } else if (params.get("v").get(0).equals("detail") && params.containsKey("id")) {
      // anything after first id is ignored
      this.fetchAndShowDocumentDetail(params.get("id").get(0));
    } else if (params.get("v").get(0).equals("create")) {
      this.showDocumentCreateForm();
    } else if (params.get("v").get(0).equals("edit") && params.containsKey("id")) {
      // anything after first id is ignored
      this.fetchDocumentAndShowEditForm(params.get("id").get(0));
    } else {
      // unrecognized view - do nothing
      // TODO: log?
    }    
  }

  private void fetchAndShowAllDocuments() {
    this.dataService.fetchDocumentList(new AsyncCallback<List<DocumentInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showError("There was a problem loading the document list.");
      }

      @Override
      public void onSuccess(List<DocumentInfo> result) {
        view.showDocumentList(result);
      }
    });    
  }

  private void fetchAndShowDocumentDetail(final String documentUUIDString) {
    int documentUUID = Integer.parseInt(documentUUIDString);
    this.dataService.fetchDocumentDetail(documentUUID, new AsyncCallback<DocumentInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showError("Could not load details for " + documentUUIDString);
      }

      @Override
      public void onSuccess(DocumentInfo result) {
        view.showDocumentDetail(result, result.userCanEdit());
      }
    });
    
  }

  private void showDocumentCreateForm() {
    // TODO Auto-generated method stub
    
  }

  private void fetchDocumentAndShowEditForm(String documentUUIDString) {
    int documentUUID = Integer.parseInt(documentUUIDString);
    this.dataService.fetchDocumentDetail(documentUUID, new AsyncCallback<DocumentInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showError("Document could not be opened for editing.");
      }

      @Override
      public void onSuccess(DocumentInfo result) {
        view.showDocumentEdit(result);
      }
    });
    
  }

  public void setView(DocumentView view) {
    this.view = view;
  }


}
