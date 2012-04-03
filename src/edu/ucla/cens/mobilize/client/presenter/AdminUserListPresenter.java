package edu.ucla.cens.mobilize.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.dataaccess.DataService;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.ClassUpdateParams;
import edu.ucla.cens.mobilize.client.dataaccess.requestparams.UserSearchParams;
import edu.ucla.cens.mobilize.client.event.UserDataChangedEvent;
import edu.ucla.cens.mobilize.client.model.UserInfo;
import edu.ucla.cens.mobilize.client.model.UserSearchData;
import edu.ucla.cens.mobilize.client.model.UserSearchInfo;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.AwErrorUtils;
import edu.ucla.cens.mobilize.client.view.AdminUserListView;

public class AdminUserListPresenter implements Presenter {
  AdminUserListView view;
  UserInfo userInfo;
  EventBus eventBus;
  DataService dataService;
  Range lastLoadedRange;

  private List<String> errors = new ArrayList<String>();
  private final static int DEFAULT_PAGE_SIZE = 100;
  private Logger _logger = Logger.getLogger(AdminUserListPresenter.class.getName());
  
  public AdminUserListPresenter(UserInfo userInfo, DataService dataService, EventBus eventBus) {
    this.userInfo = userInfo;
    this.dataService = dataService;
    this.eventBus = eventBus;
  }

  public void setView(AdminUserListView view) {
    this.view = view;
    addEventHandlersToView();
  }
  
  @Override
  public void go(Map<String, String> params) {
    view.clearSearchBoxes();
    view.showUserList();
    
    // Set up paging params first, b/c they're needed for "all users" special case
    int startIndex = 0;
    int pageSize = DEFAULT_PAGE_SIZE;
    if (params.containsKey("start_index")) {
      try {
        startIndex = Integer.parseInt(params.get("start_index"));
      } catch (NumberFormatException e) {} // keep default
    }
    if (params.containsKey("page_size")) {
      try {
        int pageSizeParam = Integer.parseInt(params.get("page_size"));
        if (pageSizeParam > 0) {
          pageSize = pageSizeParam;
        }
      } catch (NumberFormatException e) {} // keep default 
    }
    view.setStartIndex(startIndex);
    view.setPageSize(pageSize);
    
    // special case: username=* means show all users
    if (params.containsKey("username") && params.get("username").equals("*")) {
      fetchAndShowAllUsers(startIndex, pageSize);
      return;
    }
    // special case: if no search terms are given, show instructions
    if (params.isEmpty()) {
      view.showInstructions();
      return;
    }

    String username = null;
    if (params.containsKey("username")) {
      username = params.get("username");
      view.setUsernameSearchString(username);
    }
    String personalId = null;
    if (params.containsKey("pid")) {
      personalId = params.get("pid");
      view.setPersonalIdSearchString(personalId);
    }
    Boolean isEnabled = null;
    if (params.containsKey("enabled")) {
      isEnabled = (params.get("enabled").equals("true")) ? true : false;
      view.setAdvancedSearchEnabled(isEnabled);
    }
    Boolean isAdmin = null;
    if (params.containsKey("admin")) {
      isAdmin = (params.get("admin").equals("true")) ? true : false;
      view.setAdvancedSearchIsAdmin(isAdmin);
    }
    Boolean canCreateCampaigns = null;
    if (params.containsKey("can_create")) {
      canCreateCampaigns = (params.get("can_create").equals("true")) ? true : false;
      view.setAdvancedSearchCanCreateCampaigns(canCreateCampaigns);
    }
    String firstNameSearchString = params.containsKey("first_name") ? params.get("first_name") : null;
    String lastNameSearchString = params.containsKey("last_name") ? params.get("last_name") : null;
    String emailSearchString = params.containsKey("email") ? params.get("email") : null;
    String organizationSearchString = params.containsKey("org") ? params.get("org") : null;
    // set search terms in the advanced popup so user can open it to see what results are being displayed
    view.setAdvancedSearchFirstNameSearchString(firstNameSearchString);
    view.setAdvancedSearchLastNameSearchString(lastNameSearchString);
    view.setAdvancedSearchEmailSearchString(emailSearchString);
    view.setAdvancedSearchOrganizationSearchString(organizationSearchString);
    this.fetchAndShowUserList(username,
                              personalId,
                              isEnabled, 
                              canCreateCampaigns, 
                              isAdmin, 
                              firstNameSearchString,
                              lastNameSearchString,
                              emailSearchString, 
                              organizationSearchString, 
                              startIndex,
                              pageSize);
  }

  private void fetchAndShowAllUsers(int startIndex, int pageSize) {
    this.fetchAndShowUserList(null, null, null, null, null, null, null, null, null, startIndex, pageSize);
  }

  private void bind() {
    // TODO: listen for user data update events
  }
  
  private void addEventHandlersToView() {
    view.getUserDeleteButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.showConfirmDelete(view.getSelectedUsernames(), new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            deleteUsers(view.getSelectedUsernames());
          }
        });
      }
    });
    
    view.getUserDisableButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        disableUsers(view.getSelectedUsernames());
      }
    });
    
    view.getUserEnableButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        enableUsers(view.getSelectedUsernames());
      }
    });
    
    view.getUserAddToClassButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        showAddUsersToClassDialog();
      }
    });
    
    view.getSearchUsernameButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        fireHistoryTokenToMatchSearchQuery(view.getUsernameSearchString(), 
                                           null, null, null, null, null, null, null, null,
                                           view.getStartIndex(), view.getPageSize());
      }
    });
    
    view.getSearchUsernameTextBox().addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          fireHistoryTokenToMatchSearchQuery(view.getUsernameSearchString(), 
              null, null, null, null, null, null, null, null, 
              view.getStartIndex(), view.getPageSize());
        }
      }
    });
    
    view.getSearchPersonalIdButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        fireHistoryTokenToMatchSearchQuery(null, // username
                                           view.getPersonalIdSearchString(),
                                           null, null, null, null, null, null, null,
                                           view.getStartIndex(), view.getPageSize());
      }
    });
    
    view.getSearchPersonalIdTextBox().addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          fireHistoryTokenToMatchSearchQuery(null, // username
                                             view.getPersonalIdSearchString(),
                                             null, null, null, null, null, null, null,
                                             view.getStartIndex(), view.getPageSize());          
        }
      }
    });
    
    // Show search form in popup. On submit, fire history token to show search results.
    view.getAdvancedSearchLink().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.showAdvancedSearchPopup(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            fireHistoryTokenToMatchAdvancedSearchQuery();
          }
        });
      }
    });
    
    view.getErrorLink().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        view.showErrorList(errors);
        view.hideErrorLink();
      }
    });
    
    view.addRangeChangeHandler(new RangeChangeEvent.Handler() {
      @Override
      public void onRangeChange(RangeChangeEvent event) {
        Range newRange = event.getNewRange();
        if (!newRange.equals(lastLoadedRange)) {
          refreshUserList();
        }
      }
    });
  }
  
  // Gets current filter values, bookmarks them as a history token, and reloads user data.
  private void refreshUserList() {
    String username = view.getUsernameSearchString();
    String personalId = view.getPersonalIdSearchString();
    Boolean isEnabled = view.getAdvancedSearchEnabled();
    Boolean canCreateCampaigns = view.getAdvancedSearchCanCreateCampaigns();
    Boolean isAdmin = view.getAdvancedSearchIsAdmin();
    String firstName = view.getAdvancedSearchFirstNameSearchString();
    String lastName = view.getAdvancedSearchLastNameSearchString();
    String email = view.getAdvancedSearchEmailSearchString();
    String organization = view.getAdvancedSearchOrganizationSearchString();
    int startIndex = view.getStartIndex();
    int pageSize = view.getPageSize(); 
    
    String historyToken = HistoryTokens.adminUserList(username, personalId, isEnabled, canCreateCampaigns, 
      isAdmin, firstName, lastName, email, organization, startIndex, pageSize);
    
    // save history token so page can be bookmarked, but don't fire it b/c that would reload everything
    History.newItem(historyToken, false);
    
    fetchAndShowUserList(username, personalId, isEnabled, canCreateCampaigns, isAdmin, firstName, lastName,
                         email, organization, startIndex, pageSize);
  }
  
  private void fireHistoryTokenToMatchSearchQuery(String username,
                                                  String personalId,
                                                  Boolean isEnabled,
                                                  Boolean canCreate,
                                                  Boolean isAdmin,
                                                  String firstNameSearchString,
                                                  String lastNameSearchString,
                                                  String emailSearchString,
                                                  String organizationSearchString,
                                                  int startIndex,
                                                  int pageSize) {
    History.newItem(HistoryTokens.adminUserList(username,
                                                personalId,
                                                isEnabled, 
                                                canCreate, 
                                                isAdmin, 
                                                firstNameSearchString,
                                                lastNameSearchString,
                                                emailSearchString, 
                                                organizationSearchString,
                                                startIndex,
                                                pageSize));
  }
  
  private void fireHistoryTokenToMatchAdvancedSearchQuery() {
    Boolean isEnabled = view.getAdvancedSearchEnabled();
    Boolean canCreate = view.getAdvancedSearchCanCreateCampaigns();
    Boolean isAdmin = view.getAdvancedSearchIsAdmin();
    String firstNameSearchString = view.getAdvancedSearchFirstNameSearchString();
    String lastNameSearchString = view.getAdvancedSearchLastNameSearchString();
    String emailSearchString = view.getAdvancedSearchEmailSearchString();
    String organizationSearchString = view.getAdvancedSearchOrganizationSearchString();
    int startIndex = view.getStartIndex();
    int pageSize = view.getPageSize();
    History.newItem(HistoryTokens.adminUserList(null, // username 
                                                null, // personal id
                                                isEnabled, 
                                                canCreate, 
                                                isAdmin,
                                                firstNameSearchString,
                                                lastNameSearchString,
                                                emailSearchString, 
                                                organizationSearchString, 
                                                startIndex,
                                                pageSize));  
  }
  
  private void fetchAndShowUserList(String username,
                                    String personalId,
                                    Boolean isEnabled,
                                    Boolean canCreateCampaigns,
                                    Boolean isAdmin,
                                    String firstNameSearchString,
                                    String lastNameSearchString,
                                    String emailSearchString,
                                    String organizationSearchString,
                                    final int startIndex,
                                    final int pageSize) {
    view.showWaitIndicator();
    UserSearchParams params = new UserSearchParams();
    params.username_opt = username;
    params.personalId_opt = personalId;
    params.enabled_opt = isEnabled;
    params.canCreateCampaigns_opt = canCreateCampaigns;
    params.admin_opt = isAdmin;
    params.firstName_opt = firstNameSearchString;
    params.lastName_opt = lastNameSearchString;
    params.email_opt = emailSearchString;
    params.organization_opt = organizationSearchString;
    params.startIndex_opt = startIndex;
    params.pageSize_opt = pageSize;
    
    dataService.fetchUserSearchData(params, new AsyncCallback<UserSearchData>() {
      @Override
      public void onFailure(Throwable caught) {
        view.hideWaitIndicator();
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("User search failed.", caught.getMessage());
      }

      @Override
      public void onSuccess(UserSearchData result) {
        lastLoadedRange = new Range(startIndex, pageSize);
        view.hideWaitIndicator();
        List<UserSearchInfo> userInfos = result.getUserSearchInfos();
        Collections.sort(userInfos); // FIXME: why aren't these sorted on the server?
        view.setUserList(userInfos);
        view.setVisibleRange(startIndex, pageSize);        
        view.setRowCount(result.getTotalUserCount());
      }
    });
  }
  
  private void deleteUsers(List<String> usernames) {
    dataService.deleteUsers(usernames, new AsyncCallback<String>() {

      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught);
        view.showError("There was a problem deleting users.", caught.getMessage());
      }

      @Override
      public void onSuccess(String result) {
        eventBus.fireEvent(new UserDataChangedEvent());
        refreshUserList();
      }
    });
  }
  
  private void disableUsers(List<String> usernames) {
    for (final String username : usernames) {
      dataService.disableUser(username, new AsyncCallback<String>() {
        @Override
        public void onFailure(Throwable caught) {
          AwErrorUtils.logoutIfAuthException(caught);
          addError(caught.getMessage());
        }

        @Override
        public void onSuccess(String result) {
          view.markUserDisabled(username);
        }
      });
    }
  }
  
  private void enableUsers(List<String> usernames) {
    for (final String username : usernames) {
      dataService.enableUser(username, new AsyncCallback<String>() {
        @Override
        public void onFailure(Throwable caught) {
          AwErrorUtils.logoutIfAuthException(caught);
          addError(caught.getMessage());
        }

        @Override
        public void onSuccess(String result) {
          view.markUserEnabled(username);
        }
      });
    }
  }
  
  private void showAddUsersToClassDialog() {
    final List<String> usernames = view.getSelectedUsernames();
    dataService.fetchClassNamesAndUrns(new AsyncCallback<Map<String, String>>() {
      @Override
      public void onFailure(Throwable caught) {
        AwErrorUtils.logoutIfAuthException(caught); 
        view.showError("There was a problem fetching class urns.", caught.getMessage());
      }

      @Override
      public void onSuccess(Map<String, String> result) {
        List<String> classUrns = new ArrayList<String>(result.keySet());
        view.showAddUsersToClassDialog(usernames, classUrns, new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            addSelectedUsersToClass();
          }
        });
      }
    });
  }
  
  private void addSelectedUsersToClass() {
    String classUrn = view.getClassToAddUsers();
    if (classUrn != null && !classUrn.isEmpty()) { // if class isn't selected, do nothing
      List<String> usernames = view.getSelectedUsernames(); // dialog is modal so these won't have changed
      ClassUpdateParams params = new ClassUpdateParams();
      params.classId = classUrn;
      RoleClass role = view.getClassRoleForUsers();
      for (String username : usernames) {
        params.usersToAdd_opt.put(username, role);
        // gotcha: if user was already in class, previous role will be overwritten
      }
      dataService.updateClass(params, new AsyncCallback<String>() {
        @Override
        public void onFailure(Throwable caught) {
          AwErrorUtils.logoutIfAuthException(caught);
          view.hideAddUsersToClassDialog();
          ErrorDialog.show("There was a problem adding users to the class.", caught.getMessage());
        }
  
        @Override
        public void onSuccess(String result) {
          view.hideAddUsersToClassDialog();
        }
      });
    }
  }
  
  private void clearErrors() {
    view.hideErrorLink();
  }
  
  private void addError(String msg) {
    this.errors.add(msg);
    view.setErrorCount(this.errors.size());
    // NOTE: errors are cleared when user clicks on the error link
  }
}
