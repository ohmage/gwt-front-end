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
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.presenter.CampaignPresenter;

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
          view.setSelectedClasses(classes);
          view.setDeletePanelVisible(true); 
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
              String msg = caught.getMessage();
              if (msg == null || msg.isEmpty()) msg = "There was a problem completing the operation.";
              _logger.severe("Could not delete campaign: " + caught.getMessage());
            }
            @Override
            public void onSuccess(String result) {
              // redirect to campaign list so user can verify that 
              // deleted campaign is gone and display success message
              userInfo.setInfoMessage("Campaign " + campaignUrn + " has been deleted.");
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
        // TODO: only reload data if class list has changed?
        dataService.fetchClassList(campaignClasses, new AsyncCallback<List<ClassInfo>>() {
          @Override
          public void onFailure(Throwable caught) {
            _logger.severe(caught.getMessage());
          }
          @Override
          public void onSuccess(List<ClassInfo> result) {
            Set<String> userLogins = new HashSet<String>(); // enforces uniqueness
            for (ClassInfo classInfo : result) {
              userLogins.addAll(classInfo.getMemberLogins());
              userLogins.addAll(classInfo.getPrivilegedMemberLogins());
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
        History.back();
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
          view.prepareFormForSubmit(); // copies some values into hidden fields
          view.submitForm();
        } // else do nothing (validateForm will have marked errors)
      }
    });
    
    this.view.addSubmitCompleteHandler(formSubmitCompleteHandler);    
  }
  
  private SubmitCompleteHandler formSubmitCompleteHandler = new SubmitCompleteHandler() {
    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
      view.clearFormFields();
      String result = event.getResults();
      // NOTE: gwt formPanel results can be null if sending to a different domain
      // TODO: handle failure
      
      // redirect to campaign list so user can see results
      userInfo.setInfoMessage("Campaign saved.");
      History.newItem(HistoryTokens.campaignList());
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
  
  private void clearValidationErrors() {
    view.clearValidationErrors();
  }

}
