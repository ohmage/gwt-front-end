package edu.ucla.cens.mobilize.client.presenter;


import java.util.Arrays;
import java.util.HashMap;
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
import edu.ucla.cens.mobilize.client.dataaccess.formpanel.AwFormPanel;
import edu.ucla.cens.mobilize.client.event.ClassDataChangedEvent;
import edu.ucla.cens.mobilize.client.model.ClassSearchInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.ui.ConfirmDeleteDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;
import edu.ucla.cens.mobilize.client.view.AdminClassDetailView;

public class AdminClassDetailPresenter implements Presenter {
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  private AdminClassDetailView view;

  private static Logger _logger = Logger.getLogger(AdminClassDetailPresenter.class.getName());
  
  public AdminClassDetailPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  public void setView(AdminClassDetailView view) {
    this.view = view;
    addEventHandlersToView();
  }
  
  private void addEventHandlersToView() {
    view.getDownloadRosterLink().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        downloadRoster(view.getClassUrn());
      }
    });
    
    view.getUploadRosterLink().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String requestUrl = AwConstants.getClassRosterUpdateUrl();
        String authToken = dataService.authToken();
        String client = dataService.client();
        SubmitCompleteHandler onSubmitComplete = new SubmitCompleteHandler() {
          @Override
          public void onSubmitComplete(SubmitCompleteEvent event) {
            String result = event.getResults();
            if (result.contains("success")) {
              fetchAndShowClassInfo(view.getClassUrn()); // refresh class info
            } else {
              view.showError("There was a problem uploading the roster.",  "Response was: " + result);
            }
          }
        };
        view.showClassRosterUploadDialog(requestUrl, authToken, client, onSubmitComplete);
      }
    });
    
    view.getDeleteClassLink().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ConfirmDeleteDialog.show("Are you sure you want to delete " + view.getClassUrn() + "?", 
            new ClickHandler() {
              @Override
              public void onClick(ClickEvent event) {
                deleteClass();
              }
        });
      }
    });
  }

  @Override
  public void go(Map<String, String> params) {
    if (params.containsKey("cid")) {
      fetchAndShowClassInfo(params.get("cid"));
    }
  }
  
  private void fetchAndShowClassInfo(final String classUrn) {
    if (classUrn == null || classUrn.isEmpty()) return;
    dataService.fetchClassSearchInfo(classUrn, new AsyncCallback<ClassSearchInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem fetching details for class " + classUrn, caught.getMessage());
      }

      @Override
      public void onSuccess(ClassSearchInfo result) {
        view.setClassUrn(result.getClassUrn());
        view.setClassName(result.getClassName());
        view.setDescription(result.getDescription());
        view.setMembers(result.getMembers());
        
        // NOTE: only campaign urn (not name) are available in the class search api
        // TODO: fetch campaign name so you can show name instead of urn here
        Map<String, String> campaignUrnToUrnMap = new HashMap<String, String>();
        for (String campaignUrn : result.getCampaigns()) {
          campaignUrnToUrnMap.put(campaignUrn, campaignUrn);
        }
        view.setCampaigns(campaignUrnToUrnMap);
      }
    });
  }
  
  // Requests roster using a FormPanel so user will be prompted to save the
  // file. (Note that the response must also  have Content-disposition header
  // set to attachment.
  private void downloadRoster(String classUrn) {
    Map<String, String> params = dataService.getClassRosterCsvDownloadParams(Arrays.asList(classUrn));
    _logger.fine("Doing form panel post to download roster with params: " + 
                 MapUtils.translateToParameters(params));
    String requestUrl = AwConstants.getClassRosterReadUrl();
    AwFormPanel.post(requestUrl, params, true);
  }
  
  private void deleteClass() {
    final String classUrn = view.getClassUrn();
    dataService.deleteClass(classUrn, new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem deleting " + classUrn, caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        eventBus.fireEvent(new ClassDataChangedEvent());
        History.newItem(HistoryTokens.adminClassList());
      }
    });
  }
  
}
