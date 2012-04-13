package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.event.CampaignDataChangedEvent;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEvent;
import edu.ucla.cens.mobilize.client.event.UserInfoUpdatedEventHandler;
import edu.ucla.cens.mobilize.client.exceptions.AuthenticationException;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.utils.AwDataTranslators;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;

public class CampaignEditFormPresenter {
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  
  private CampaignEditFormView view;
  
  private boolean isCreate = false;

  private static Logger _logger = Logger.getLogger(CampaignEditFormPresenter.class.getName());
  
  public CampaignEditFormPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
    
    bind();
  }
  
  private void bind() {
    eventBus.addHandler(UserInfoUpdatedEvent.TYPE, new UserInfoUpdatedEventHandler() {
      @Override
      public void onUserInfoChanged(UserInfoUpdatedEvent event) {
        // makes sure userInfo is up to date b/c class list view uses it
        userInfo = event.getUserInfo();
      }
    });
  }
  
  public void setView(CampaignEditFormView view) {
    this.view = view;
    addEventHandlersToView();
  }

  public void initFormForCreate() {
    this.isCreate = true;
    view.clearFormFields();
    view.setHeader("Creating New Campaign");
    view.initializeForm(dataService.authToken(), AwConstants.getCampaignCreateUrl());
    view.setDeletePanelVisible(false);
    view.setAuthorPlaceholderText(userInfo.getUserName());
    view.setAuthorsPanelVisible(false);
  }
  
  public void fetchCampaignAndInitFormForEdit(String campaignUrn) {
    // GOTCHA: assumes user is member of any class that will show up in campaign
    final Map<String, String> classUrnToClassNameMap = userInfo.getClasses();
    this.isCreate = false;    
    this.dataService.fetchCampaignDetail(campaignUrn, 
      new AsyncCallback<CampaignDetailedInfo>() {
        @Override
        public void onFailure(Throwable caught) {
          _logger.severe(caught.getMessage());
          // TODO: show error to user
        }
        
        @Override
        public void onSuccess(CampaignDetailedInfo result) {
          view.setHeader("Editing " + result.getCampaignName());
          view.setCampaignName(result.getCampaignName());
          view.setCampaignUrn(result.getCampaignId());
          view.setDescription(result.getDescription());
          view.setPrivacy(result.getPrivacy());
          view.setRunningState(result.getRunningState());
          view.storeOriginalAuthors(result.getAuthors()); // used for diff
          view.setSelectedAuthors(result.getAuthors());
          // fill in class names
          Map<String, String> classes = new HashMap<String, String>();
          for (String classUrn : result.getClasses()) {
            String className = classUrnToClassNameMap.containsKey(classUrn) ?
                                 classUrnToClassNameMap.get(classUrn) : 
                                 classUrn; // put class urn if name not available // FIXME?
            classes.put(classUrn, className);
          }
          view.storeOriginalClasses(new ArrayList<String>(classes.keySet()));
          view.setSelectedClasses(classes);
          view.setDeletePanelVisible(true); 
          view.setAuthorsPanelVisible(true);
          view.initializeForm(dataService.authToken(), AwConstants.getCampaignUpdateUrl());
        }
    });
  }
  
  private void deleteCampaign(final String campaignUrn) {
    if (campaignUrn != null) {
      dataService.deleteCampaign(campaignUrn, 
          new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
              ErrorDialog.show("There was a problem deleting the campaign: " + caught.getMessage());
              AwErrorUtils.logoutIfAuthException(caught);
            }
            @Override
            public void onSuccess(String result) {
              // report event so other displays can be updated
              eventBus.fireEvent(new CampaignDataChangedEvent());
              // redirect to campaign list so user can verify that deleted campaign is gone
              History.newItem(HistoryTokens.campaignList());
            }
      });
    }
  }
  
  private void addEventHandlersToView() {
    // when user clicks Add Authors, fetch a list of people that are allowed
    // to be added as authors (must be members of one of the campaign classes)
    // and show the author chooser dialog
    this.view.getAddAuthorsButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        List<String> campaignClasses = view.getClassUrns();
        
        // if no classes have been selected, display error msg
        if (campaignClasses.isEmpty()) {
          ErrorDialog.show("Please first select class(es) to see users list.");
          return;
        }
        
        // TODO: only reload data if class list has changed?
        dataService.fetchClassList(campaignClasses, false, new AsyncCallback<List<ClassInfo>>() {
          @Override
          public void onFailure(Throwable caught) {
            _logger.severe(caught.getMessage());
          }
          @Override
          public void onSuccess(List<ClassInfo> result) {
            Set<String> userLogins = new HashSet<String>(); // enforces uniqueness
            for (ClassInfo classInfo : result) {
              userLogins.addAll(classInfo.getMemberLogins());
            }
            view.showAuthorChoices(new ArrayList<String>(userLogins));
          }
        });
      }
    }); // end of this.view.getAddAuthorsButton().addClickHandler(new ClickHandler() {
    
    // when user clicks Add Classes, show the class chooser dialog
    this.view.getAddClassesButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.showClassChoices(userInfo.getClasses());
      }
    });
    
    // when user clicks Cancel button, clear form fields and go back to previous page
    this.view.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.clearFormFields();
        History.newItem(HistoryTokens.campaignList()); 
      }
    });
  
    this.view.getDeleteButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // TODO: handle ConfirmDeleteEvent instead of click?
        view.showConfirmDelete(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            deleteCampaign(view.getCampaignUrn());
          }
        });
      }
    });    
    
    // Clicking save sends updates to the server. 
    this.view.getSaveButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        assert view.formIsInitialized() : "You must call view.initializeForm(authToken, serverLocation) before submitting";;
        if (validateForm()) {
          view.disableSubmitButton(); // don't allow double save
          view.prepareFormForSubmit(isCreate); // copies some values into hidden fields
          view.submitForm();
        } // else do nothing (validateForm will have marked errors)
      }
    });
    
    this.view.addSubmitCompleteHandler(formSubmitCompleteHandler);    
  }
  
  private SubmitCompleteHandler formSubmitCompleteHandler = new SubmitCompleteHandler() {
    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
      // make sure submit button gets re-enabled even if there's an error
      view.enableSubmitButton();
      
      //String result = "{\"result\":\"failure\",\"errors\":[{\"text\":\"Campaign already exists.\",\"code\":\"0804    \"}]}"; // sample error response
      //String result = "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">{\"result\":\"failure\",\"errors\":[{\"text\":\"The campaign already exists: urn:campaign:ca:lausd:Carson_HS:CS104:Fall:2011:Advertisement\",\"code\":\"0702\"}]}</pre>"; // Safari, with content-type == application/json
      String result = event.getResults();
      String status = null;
      Map<String, String> errorCodeToDescriptionMap = null;
      // NOTE: gwt formPanel results can be null if sending to a different domain
      // so you must deploy a compiled version to test this error handling
      if (result != null) {
        try {
          result = AwDataTranslators.stripPreTags(result); // in case content-type is not text/html  
          status = JSONParser.parseStrict(result).isObject().get("result").isString().stringValue();
          if (!"success".equals(status)) {
            // FIXME: refactor AndWellnessDataService parse/error methods so they can be reused here
            // (see parseServerErrorResponse and getResponseTextOrThrowException)
            errorCodeToDescriptionMap = AwDataTranslators.translateErrorResponse(result);
          }
        } catch (Exception e) {
          _logger.severe("Failed to parse json. Response was: " + result + ". Error was: " + e.getMessage());
        }
      } else {
        _logger.fine("null result from formPanel post, assuming this is dev mode and submission was success");
        status = "success";
      } 
      if (status != null && status.equals("success")) {
        // report event so other views can be updated
        eventBus.fireEvent(new CampaignDataChangedEvent());
        // redirect to campaign list so user can see results
        view.clearFormFields();
        History.newItem(HistoryTokens.campaignList());
      } else {
        String msg = isCreate ? "There was a problem creating the campaign." :
                                "There was a problem editing the campaign.";
        _logger.severe(msg + " Response was: " + result);
        if (errorCodeToDescriptionMap.containsKey("0200")) {
          AwErrorUtils.logoutIfAuthException(new AuthenticationException("0200"));
          ErrorDialog.show("Authentication failed.");
        } else {
          ErrorDialog.showErrorsByCode(msg, errorCodeToDescriptionMap);
        }
      }
    }
  };
  
  // also marks errors
  private boolean validateForm() {
    boolean isValid = true;
    List<String> errors = new ArrayList<String>();
    if (this.isCreate && this.view.getXmlFilename().isEmpty()) {
      errors.add("You must upload an xml file.");
    }
    if (this.view.getClassUrns().isEmpty()) {
      errors.add("Campaign must belong to at least one class.");
    }
    if (!errors.isEmpty()) {
      isValid = false;
      String errorMsg = "Campaign could not be saved.";
      view.showValidationErrors(errorMsg, errors);
    }
    return isValid;
  }

}
