package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassSearchParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserSearchParams;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.ClassSearchInfo;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserSearchInfo;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.view.AdminClassListView;

public class AdminClassListPresenter implements Presenter {

  private UserInfo userInfo;
  private DataService dataService;
  private EventBus eventBus;
  private AdminClassListView view;

  private Logger _logger = Logger.getLogger(AdminClassListPresenter.class.getName());
  
  public AdminClassListPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }
  
  public void setView(AdminClassListView view) {
    this.view = view;
    addEventHandlersToView();
  }
  
  private void addEventHandlersToView() {
    view.getSearchClassNameButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.newItem(HistoryTokens.adminClassList(view.getClassNameSearchString(), null, null));
      }
    });
    
    view.getClassNameTextBox().addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          History.newItem(HistoryTokens.adminClassList(view.getClassNameSearchString(), null, null));  
        }
      }
    });
    
    view.getSearchUsernameButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.newItem(HistoryTokens.adminClassList(null, view.getMemberUsernameSearchString(), null));
      }
    });
    
    view.getUsernameTextBox().addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          History.newItem(HistoryTokens.adminClassList(null, view.getMemberUsernameSearchString(), null));
        }
      }
    });
  }

  @Override
  public void go(Map<String, String> params) {
    view.clearSearchStrings();
    view.clearClassList();
    String className = null;
    // if there is a class param, fetch and show results matching class name
    if (params.containsKey("class") && params.get("class").equals("*")) {
      view.showClassList();
      fetchAndShowClassList(null); // shows all classes (big data fetch)
    } else if (params.containsKey("class")) {
      view.showClassList();
      className = params.get("class");
      view.setClassNameSearchString(className);
      fetchAndShowClassList(className);
    } else if (params.containsKey("member")) { // no class name? check for member param
      view.showClassList();
      String memberUsername = params.get("member");
      view.setMemberUsernameSearchString(memberUsername);
      fetchAndShowClassesForMember(memberUsername);
    } else {
      view.showInstructions();
    }
  }

  private void fetchAndShowClassList(String classNameSearchString) {
    view.clearClassList();
    ClassSearchParams params = new ClassSearchParams();
    params.className_opt = classNameSearchString;
    dataService.fetchClassSearchResults(params, new AsyncCallback<List<ClassSearchInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem retrieving class data.", caught.getMessage());
      }

      @Override
      public void onSuccess(List<ClassSearchInfo> result) {
        Collections.sort(result, compareByClassName);
        
        for (ClassSearchInfo classInfo : result) {
          int memberCount = classInfo.getMembers().size();
          int campaignCount = classInfo.getCampaigns().size();
          view.addClass(classInfo.getClassUrn(), 
                        classInfo.getClassName(), 
                        memberCount, 
                        campaignCount);
        }
      }
    });
  }
  
  private Comparator<ClassSearchInfo> compareByClassName = new Comparator<ClassSearchInfo>() {
    @Override
    public int compare(ClassSearchInfo arg0, ClassSearchInfo arg1) {
      return arg0.getClassName().compareTo(arg1.getClassName());
    }
  };
  
  private void fetchAndShowClassListByUrn(final List<String> classUrns) {
    dataService.fetchClassList(classUrns, false, new AsyncCallback<List<ClassInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem fetching class info", caught.getMessage());
      }

      @Override
      public void onSuccess(List<ClassInfo> result) {
        for (ClassInfo classInfo : result) {
          int memberCount = classInfo.getMemberLogins().size();
          int campaignCount = 0; // filler
          view.addClass(classInfo.getClassId(), 
                        classInfo.getClassName(), 
                        memberCount, 
                        campaignCount);
        }
        fetchAndFillInCampaignCounts(classUrns);
      }
    });
  }
  
  // class should already exist in the grid before this is called
  private void fetchAndFillInCampaignCounts(List<String> classUrns) {
    for (String classUrn : classUrns) {
      dataService.fetchClassSearchInfo(classUrn, new AsyncCallback<ClassSearchInfo>() {
        @Override
        public void onFailure(Throwable caught) {
          AwErrorUtils.logoutIfAuthException(caught);
          _logger.fine(caught.getMessage());
          // GOTCHA: user will see 0 as campaign count
        }

        @Override
        public void onSuccess(ClassSearchInfo result) {
          view.setCampaignCount(result.getClassUrn(), result.getCampaigns().size());
        }
      });
    }
  }
  
  private void fetchAndShowClassesForMember(final String username) {
    view.clearClassList();
    UserSearchParams params = new UserSearchParams();
    params.username_opt = username;
    dataService.fetchUserSearchResults(params, new AsyncCallback<List<UserSearchInfo>>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem fetching search info for user " + username, caught.getMessage());
      }

      @Override
      public void onSuccess(List<UserSearchInfo> result) {
        Set<String> classUrns = new TreeSet<String>(); // unique
        for (UserSearchInfo userInfo : result) {
          classUrns.addAll(userInfo.getClassUrns());
        }
        fetchAndShowClassListByUrn(new ArrayList<String>(classUrns));
      }
    });
  }
}
