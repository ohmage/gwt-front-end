package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.DocumentReadParams;
import edu.ucla.cens.mobilize.client.event.DocumentDownloadHandler;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.ui.DocumentEditPresenter;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwDataTranslators;
import edu.ucla.cens.mobilize.client.utils.MapUtils;
import edu.ucla.cens.mobilize.client.view.DocumentView;

public class DocumentPresenter implements Presenter {

  UserInfo userInfo;
  DataService dataService;
  EventBus eventBus;
  
  DocumentView view;
  
  DocumentEditPresenter documentEditPresenter;

  // Logging utility
  private static Logger _logger = Logger.getLogger(DocumentPresenter.class.getName());
  
  public DocumentPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
    
    this.documentEditPresenter = new DocumentEditPresenter(userInfo, dataService, eventBus);
  }
  
  private void addEventHandlersToView() {
    assert this.view != null : "view must be set before calling addEventHandlersToView()";
    view.getUploadButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.newItem(HistoryTokens.documentCreate());
      }
    });
    
    view.setDocumentDownloadHandler(this.documentDownloadHandler);
  }
  
  @Override
  public void go(Map<String, String> params) {
    // hide any leftover notifications
    this.view.hideMsg();
    
    // display any new notifications
    if (userInfo.hasInfoMessage()) this.view.showMsg(userInfo.getInfoMessage());
    if (userInfo.hasErrorMessage()) this.view.showError(userInfo.getErrorMessage());
    userInfo.clearMessages();
    
    // get subview from url params
    if (params.isEmpty()) {
      fetchAndShowAllDocuments();
    } else if (params.containsKey("v")) {
      String view = params.get("v");
      if (view.equals("all")) {
        fetchAndShowAllDocuments();
      } else if (view.equals("detail") && params.containsKey("id")) {
        // anything after first id is ignored
        fetchAndShowDocumentDetail(params.get("id"));
      } else if (view.equals("create")) {
        showDocumentCreateForm();
      } else if (view.equals("edit") && params.containsKey("id")) {
        // anything after first id is ignored
        fetchDocumentAndShowEditForm(params.get("id"));
      } else if (view.equals("my")) {
        fetchAndShowMyDocuments();
      } 
    } else {
      // unrecognized view - default to list view
      History.newItem(HistoryTokens.documentListAll());
    }    
  }

  private void fetchAndShowMyDocuments() {
    DocumentReadParams params = new DocumentReadParams(); // just use defaults
    this.dataService.fetchDocumentList(params, new AsyncCallback<List<DocumentInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showError("There was a problem loading the document list.");
        view.showListSubview();
      }

      @Override
      public void onSuccess(List<DocumentInfo> result) {
        view.setDocumentList(result);
        view.showListSubview();
      }
    });    
  }
  
  private void fetchAndShowAllDocuments() {
    DocumentReadParams params = new DocumentReadParams(); 
    params.campaignUrnList = userInfo.getCampaignIds();
    params.classUrnList = userInfo.getClassIds();
    this.dataService.fetchDocumentList(params, new AsyncCallback<List<DocumentInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showError("There was a problem loading the document list.");
        view.showListSubview();
      }

      @Override
      public void onSuccess(List<DocumentInfo> result) {
        view.setDocumentList(result);
        view.showListSubview();
      }
    });
  }

  private void fetchAndShowDocumentDetail(final String documentId) {
    this.dataService.fetchDocumentDetail(documentId, new AsyncCallback<DocumentInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showError("Could not load details for " + documentId);
        view.showListSubview();
      }

      @Override
      public void onSuccess(DocumentInfo result) {
        view.setDocumentDetail(result, result.userCanEdit());
        view.showDetailSubview();
      }
    });
    
  }

  private void showDocumentCreateForm() {
    this.documentEditPresenter.initFormForCreate();
    view.showEditSubview();
  }

  private void fetchDocumentAndShowEditForm(String documentId) {
    this.documentEditPresenter.fetchDocumentAndInitFormForEdit(documentId);
    this.view.showEditSubview();    
  }

  public void setView(DocumentView view) {
    this.view = view;
    this.documentEditPresenter.setView(view.getEditView());
    addEventHandlersToView();
  }

  private void downloadDocument(String documentId) {
    String url = AwConstants.getDocumentDownloadUrl();
    Map<String, String> params = dataService.getDocumentDownloadParams(documentId);
    _logger.fine("Attempting to download document from url: " + url +
                 " with params: " + MapUtils.translateToParameters(params));
    view.doDocumentDownloadPost(url, params, documentDownloadCompleteHandler);
  }
  
  // pass this handler to child views so it can be attached to download links
  private DocumentDownloadHandler documentDownloadHandler = new DocumentDownloadHandler() {
    @Override
    public void onDownloadClick(String documentId) {
      downloadDocument(documentId);
    }
  };

  // FIXME: abstract this somehow
  private SubmitCompleteHandler documentDownloadCompleteHandler = 
    new SubmitCompleteHandler() {
    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
      //String result = "{\"result\":\"failure\",\"errors\":[{\"text\":\"Campaign already exists.\",\"code\":\"0804\"}]}"; // sample error response
      String result = event.getResults();
      String status = null;
      Map<String, String> errorCodeToDescriptionMap = null;
      // NOTE: gwt formPanel results can be null if sending to a different domain
      // so you must deploy a compiled version to test this error handling
      if (result != null) {
        try {
          status = JSONParser.parseStrict(result).isObject().get("result").isString().stringValue();
          if (!"success".equals(status)) {
            errorCodeToDescriptionMap = AwDataTranslators.translateErrorResponse(result);
          }
        } catch (Exception e) {
          _logger.severe("Failed to parse json. Response was: " + result + ". Error was: " + e.getMessage());
        }
      } else {
        _logger.fine("null result from formPanel post, assuming this is dev mode and submission was success");
        status = "success";
      } 
      if (!"success".equals(status)) {
        ErrorDialog.showErrorsByCode("There was a problem creating the campaign.", 
                                      errorCodeToDescriptionMap);
        _logger.severe("Document download failed. Response was: " + result);
      }
    }
  };
  
}
