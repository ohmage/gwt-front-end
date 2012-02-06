package edu.ucla.cens.mobilize.client.presenter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.ResponseStatus;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.AuditLogSearchParams;
import edu.ucla.cens.mobilize.client.model.AuditLogEntry;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.ui.WaitIndicator;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.view.AdminAuditLogView;

public class AdminAuditLogPresenter implements Presenter {
  private AdminAuditLogView view;
  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  
  public AdminAuditLogPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  @Override
  public void go(Map<String, String> params) {
    Date date = null;
    String uri = null;
    boolean onlyFailures = false;
    if (params.containsKey("uri")) {
      uri = params.get("uri");
      
    }
    if (params.containsKey("fail") && params.get("fail").equals("1")) {
      onlyFailures = true;
      view.setOnlyFailuresFlag(onlyFailures);
    }
    if (params.containsKey("date")) {
      date = DateUtils.translateFromHistoryTokenFormat(params.get("date"));
      view.setDate(date);
      fetchAndShowAuditLogEntries(date, DateUtils.addOneDay(date), uri, onlyFailures);
    } else { // default is show today
      view.setDate(new Date()); // now
      fireHistoryTokenToMatchParams();
    }
  }
  
  public void setView(AdminAuditLogView view) {
    this.view = view;
    if (view != null) addEventHandlersToView();
  }

  private void addEventHandlersToView() {
    view.getGoButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        fireHistoryTokenToMatchParams();
      }
    });
  }
  
  private void fetchAndShowAuditLogEntries(Date startDate, Date endDate, String uri, boolean onlyFailures) {
    AuditLogSearchParams params = new AuditLogSearchParams();
    params.startDate_opt = startDate;
    params.endDate_opt = endDate;
    params.uri_opt = uri;
    if (onlyFailures) params.responseType_opt = ResponseStatus.FAILURE;
    view.showWaitIndicator();
    this.dataService.fetchAuditLog(params, new AsyncCallback<List<AuditLogEntry>>() {
      @Override
      public void onFailure(Throwable caught) {
        view.hideWaitIndicator();
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem fetching the audit log.", caught.getMessage());
      }

      @Override
      public void onSuccess(List<AuditLogEntry> result) {
        view.hideWaitIndicator();
        view.showLog(result);
      }
    });
  }
  
  private void fireHistoryTokenToMatchParams() {
    History.newItem(HistoryTokens.auditLog(view.getDate(), 
                                           view.getUri(), 
                                           view.getOnlyFailuresFlag()));
  }
  
  
}
