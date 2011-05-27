package edu.ucla.cens.mobilize.client.ui;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;

public class DocumentEditPresenter {
  private DocumentEditView view;
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  
  private Logger _logger = Logger.getLogger(DocumentEditPresenter.class.getName());
  private boolean isCreate = true;
  
  public DocumentEditPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  public void setView(DocumentEditView view) {
    this.view = view; 
    addEventHandlersToView();
  }
  
  private void addEventHandlersToView() {
    this.view.getCampaignsAddButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // TODO: load campaigns once when page first loads
        dataService.fetchCampaignIdToNameMap(new CampaignReadParams(), new AsyncCallback<Map<String, String>>() {
          @Override
          public void onFailure(Throwable caught) {
            _logger.severe(caught.getMessage());
            view.showError("There was a problem loading campaign names");
          }

          @Override
          public void onSuccess(Map<String, String> campaignIdToNameMap) {
            view.showCampaignChoices(campaignIdToNameMap);
          }
        });
      }
    });
    
    this.view.getClassesAddButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.showClassChoices(userInfo.getClasses());
      }
    });

    
    this.view.getSaveButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        assert view.formIsInitialized() : "You must call view.initializeForm(authToken, serverLocation) before submitting";;
        if (validateForm()) {
          view.prepareFormForSubmit(); // copies some values into hidden fields
          view.submitForm();
        } // else do nothing (validateForm will have marked errors)
      }
    });
    
    this.view.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.clearFormFields();
        History.back();
      }
    });

    this.view.getDeleteButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.showConfirmDelete(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            deleteDocument(view.getDocumentId(), view.getDocumentName());
          }
        });
      }
    });
    
    // FIXME: commented for debugging - uncomment so form will redirect when done
    //this.view.addSubmitCompleteHandler(formSubmitCompleteHandler);
  }

  private SubmitCompleteHandler formSubmitCompleteHandler = new SubmitCompleteHandler() {
    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
      view.clearFormFields();
      String result = event.getResults();
      // NOTE: gwt formPanel results can be null if sending to a different domain
      // TODO: handle failure
      
      // redirect to campaign list so user can see results
      userInfo.setInfoMessage("Document saved.");
      History.newItem(HistoryTokens.documentList());
    }
  };
  
  private boolean validateForm() {
    // TODO
    return true;
  }
  
  public void initFormForCreate() {
    this.isCreate  = true;
    view.clearFormFields();
    view.setHeader("Uploading new document.");
    view.setUploadPanelVisible(true);
    view.setDeletePanelVisible(false);
    view.initializeForm(dataService.authToken(), AwConstants.getDocumentCreateUrl());
  }
  
  public void fetchDocumentAndInitFormForEdit(String documentId) {
    this.isCreate = false;
    this.dataService.fetchDocumentDetail(documentId, new AsyncCallback<DocumentInfo>() {

      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showError("Document could not be opened for editing.");
      }

      @Override
      public void onSuccess(DocumentInfo result) {
        view.setDocument(result);
        view.setHeader("Editing " + result.getDocumentName());
        view.setUploadPanelVisible(false);
        view.setDeletePanelVisible(true);
        view.initializeForm(dataService.authToken(), AwConstants.getDocumentUpdateUrl());
      }
    });  
  }

  public void deleteDocument(final String documentId, final String documentName) {
    dataService.deleteDocument(documentId, new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        _logger.severe(caught.getMessage());
        view.showError("There was a problem deleting " + documentName);
      }

      @Override
      public void onSuccess(String result) {
        // redirect to document list and show success message
        userInfo.setInfoMessage("Document " + documentName + " has been deleted.");
        History.newItem(HistoryTokens.documentList());
      }
      
    });
  }
  
}
