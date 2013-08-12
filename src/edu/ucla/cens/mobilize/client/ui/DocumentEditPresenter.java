package edu.ucla.cens.mobilize.client.ui;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.CampaignReadParams;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.utils.AwDataTranslators;

public class DocumentEditPresenter {
  private DocumentEditView view;
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  private boolean isCreate = true;
  
  private Logger _logger = Logger.getLogger(DocumentEditPresenter.class.getName());
  
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
          view.disableSaveButton();
          view.submitForm();
          view.showWaitIndicator();
        } // else do nothing (validateForm will have marked errors)
      }
    });
    
    this.view.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.clearFormFields();
        History.newItem(HistoryTokens.documentListAll());
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
    
    this.view.addSubmitCompleteHandler(formSubmitCompleteHandler);
    
    this.view.getFileUploadInput().addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        // Prefill document name form field with file name when user selects file
        String path = view.getFileName();
        String sep = path.contains("/") ? "/" : "\\";
        String file = (path.length() > path.lastIndexOf(sep)) ? path.substring(path.lastIndexOf(sep) + 1) : path;
        view.setDocumentName(file);        
      }
    });
  }

  private SubmitCompleteHandler formSubmitCompleteHandler = new SubmitCompleteHandler() {
    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
      // re-enable gui elements
      view.hideWaitIndicator();
      view.enableSaveButton();
      
      String result = event.getResults();
      //result = "{\"result\":\"failure\",\"errors\":[{\"text\":\"Some document error message.\",\"code\":\"1234\"}]}"; // sample error response
      String status = null;
      Map<String, String> errorCodeToDescriptionMap = null;
      // NOTE: gwt formPanel results can be null if sending to a different domain
      // so you must deploy a compiled version to test this error handling
      if (result != null) {
        try {
          
          // An older version of FF used to wrap the JSON in a pre tag with a style:
          // result = result.replace("<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">", "").replace("</pre>", "");
          // However, newer versions just use a plain pre tag.
        	
          if(result.startsWith("<pre")) {
        	  int closingBracketIndex = result.indexOf(">") + 1; 
        	  result = result.substring(closingBracketIndex);
          }
          
          result = result.replace("</pre>", "");
          
          status = JSONParser.parseStrict(result).isObject().get("result").isString().stringValue();
          errorCodeToDescriptionMap = AwDataTranslators.translateErrorResponse(result);
          
        } catch (Exception e) {
          _logger.severe("Failed to parse json. Response was: " + result + ". Error was: " + e.getMessage());
        }
      } else {
        _logger.fine("null result from formPanel post, assuming this is dev mode and submission was success");
        status = "success";
      } 
      if (status != null && status.equals("success")) { 
        // redirect to document list so user can see results
        view.clearFormFields();
        History.newItem(HistoryTokens.documentListAll());
      } else {
        ErrorDialog.showErrorsByCode("There was a problem creating the document.",
                                     errorCodeToDescriptionMap);
        _logger.severe("Document create failed. Response was: " + result);
      }

    }
  };
  
  // Checks for required fields. Displays error if one is missing. 
  // GOTCHA: Calling method should not fire a history token if this returns
  //   false or user may never see the error message.
  private boolean validateForm() {    
    boolean isValid = true;
    if (this.isCreate && this.view.getFileName().isEmpty()) { 
      isValid = false;
      this.view.showError("Please select a file to upload");
    } else if (this.view.getDocumentName().isEmpty()) {
      isValid = false;
      this.view.showError("Please enter a name for the document.");
    } else if (this.view.getSelectedCampaigns().isEmpty() && 
               this.view.getSelectedClasses().isEmpty()) {
      isValid = false;
      this.view.showError("Document must be linked to at least one class or campaign.");
    }
    return isValid;
  }
  
  public void initFormForCreate() {
    this.isCreate = true;
    view.clearFormFields();
    view.setHeader("Uploading new document.");
    view.setUploadPanelVisible(true);
    view.setDeletePanelVisible(false);
    view.setHiddenFieldsForCreate();
    view.initializeForm(dataService.authToken(), AwConstants.getDocumentCreateUrl());
  }
  
  
  public void initFormForEdit(DocumentInfo document) {
    isCreate = false;
    view.setDocument(document);
    view.setHeader("Editing " + document.getDocumentName());
    view.setUploadPanelVisible(false);
    view.setDeletePanelVisible(true);
    view.setHiddenFieldsForEdit();
    view.initializeForm(dataService.authToken(), AwConstants.getDocumentUpdateUrl());
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
        History.newItem(HistoryTokens.documentListAll());
      }
      
    });
  }
  
}
