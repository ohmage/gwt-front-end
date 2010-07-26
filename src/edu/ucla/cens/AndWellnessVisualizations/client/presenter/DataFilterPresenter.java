package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.DataFilterService;
import edu.ucla.cens.AndWellnessVisualizations.client.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchToUploadViewEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchToUploadViewEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchToGraphViewEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchToGraphViewEventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DataFilterPresenter implements Presenter {
    private String loggedInUserName;
    private String loggedInUserPrivileges;
    private List<String> users;
    private UserInfo userInfo;
    
    // Bogus fields for javascript usage
    private String startDate;
    private String endDate;
    private String userName;
    private int numDays;
    
    public interface Display {
        HasValue<Date> getEndDate();
        String getNumDays();
        String getSelectedUser();
        HasClickHandlers getSendButton();
        void hideUserList(boolean toHide);
        void setData(List<String> data);
        void setSelectedUser(String user);
        Widget asWidget();
    }

    private final DataFilterService rpcService;   // Used to make calls to the server for data
    private final HandlerManager eventBus;  
    private final Display display;  // Pass new data to the views
    
    public DataFilterPresenter(DataFilterService rpcService, HandlerManager eventBus, Display view) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.display = view;
        this.userInfo = new UserInfo();
    }    
    
    public DataFilterPresenter(DataFilterService rpcService, HandlerManager eventBus, Display view, UserInfo userInfo) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.display = view;
        this.userInfo = userInfo;
    }    
    
    public void bind() {
        // Listen for send button clicks from the View
        display.getSendButton().addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
                doFetch();
            } 
        });
        
        // Handle switching to various Views
        eventBus.addHandler(SwitchToUploadViewEvent.TYPE,
                new SwitchToUploadViewEventHandler() {
                    public void onSwitch(SwitchToUploadViewEvent event) {
                        doSwitchToUploadView();
                }
        }); 
        eventBus.addHandler(SwitchToGraphViewEvent.TYPE,
                new SwitchToGraphViewEventHandler() {
                    public void onSwitch(SwitchToGraphViewEvent event) {
                        doSwitchToGraphView();
                }
        }); 
    }
    
    @Override
    public void go(HasWidgets container) {
        bind();
        container.clear();
        container.add(display.asWidget());
        
        // Fetch user info for display
        fetchUserInfo();
        
        // Fetch the user list for display
        fetchUserList();
    }
    
    private void fetchUserInfo() {
        // Grab user info for display
        rpcService.fetchUserInfo(new AsyncCallback<UserInfo>() {
            public void onSuccess(UserInfo result) {
                userInfo = result;
                handleUserInfo();
            }
            // Log this and fail as well as possible
            public void onFailure(Throwable caught) {
              Window.alert("Error fetching UserInfo!");
            }
        });
    }
    
    // Ask for a list of users from the server
    private void fetchUserList() {
        rpcService.fetchUserList(new AsyncCallback<ArrayList<String>>() {
            public void onSuccess(ArrayList<String> result) {
                users = result;
                sortUserList();
                display.setData(users);
            }
            
            public void onFailure(Throwable caught) {
              Window.alert("Error fetching contact details");
            }
          });
    }
    
    // Called to setup display state when we receive a new UserInfo
    private void handleUserInfo() {
        // set selected user and show/hide user list based on privileges
        /*
        if (userInfo.isAdmin() || userInfo.isResearcher()) {
            display.hideUserList(false);
            display.setSelectedUser(userInfo.getUserName());
        }
        else {
            display.hideUserList(true);
        }
        */
        
        // TODO: Remove this javascripty hack later
        this.userName = userInfo.getUserName();
    }
    
    private void sortUserList() {
        Collections.sort(users);
    }
    
    private void doSwitchToUploadView() {
        // Never show the user list when the upload view is selected
        display.hideUserList(true);
    }
    
    private void doSwitchToGraphView() {
        // If this is an admin or researcher, show the user list
        if (userInfo.isAdmin() || userInfo.isResearcher()) {
            display.hideUserList(false);
        }
        else {
            display.hideUserList(true);
        }
    }

    // Ask for new data from the server using the inputs from the view
    private void doFetch() { 
        Date startDate;
        long endTime, numDaysInMilliseconds;
        Date endDate = display.getEndDate().getValue();
        int numDays = Integer.parseInt(display.getNumDays());

        // All calls to the server use the ISO format for dates
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
        
        // We have to actually ask the API for one day later to get the correct time period
        endTime = endDate.getTime() + 1000 * 60 * 60 * 24;
        // Update endDate with the new time
        endDate = new Date(endTime);
        numDaysInMilliseconds = numDays * 1000 * 60 * 60 * 24;
        // Calculate the start of the time period asked for
        startDate = new Date(endTime - numDaysInMilliseconds);
        
        
        // Hack these into Strings for javascript calls
        this.endDate = fmt.format(endDate);
        this.startDate = fmt.format(startDate);
        this.userName = display.getSelectedUser();
        this.numDays = numDays;
        
        doFetchJavascript();

        /*
        rpcService.fetchDateRange(endDate, numDays, new AsyncCallback<ArrayList<Object>>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onSuccess(ArrayList<Object> result) {
                // TODO Auto-generated method stub
                
            }
         
        });
        */
    }
    
    // Send the request for data through DataSource in the javascript code
    private native void doFetchJavascript() /*-{
        var startDateString = this.@edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataFilterPresenter::startDate;
        var endDateString = this.@edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataFilterPresenter::endDate;
        var userName = this.@edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataFilterPresenter::userName;
        var numDays = this.@edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataFilterPresenter::numDays;
        
        // TODO Hack these into the dashboard until we get events working
        $wnd.dashBoard.startDate = $wnd.Date.parseDate(startDateString, "Y-m-d");
        $wnd.dashBoard.numDays = numDays;
        $wnd.dashBoard.setUserName(userName);
        
        var params = {
                's': startDateString,
                'e': endDateString,
        }; 
        
        // Grab hours since last survey information
        $wnd.DataSourceJson.requestData($wnd.DataSourceJson.DATA_HOURS_SINCE_LAST_SURVEY);

        // Grab percentage good location updates
        $wnd.DataSourceJson.requestData($wnd.DataSourceJson.DATA_LOCATION_UPDATES);
        
        // Grab hours since last location update
        $wnd.DataSourceJson.requestData($wnd.DataSourceJson.DATA_HOURS_SINCE_LAST_UPDATE);

        // Grab number of completed surveys per day from server
        $wnd.DataSourceJson.requestData($wnd.DataSourceJson.DATA_SURVEYS_PER_DAY, params);
        
        // Grab number of mobilities from the survey per day
        $wnd.DataSourceJson.requestData($wnd.DataSourceJson.DATA_MOBILITY_MODE_PER_DAY, params);
        
        // Grab EMA data from the server 
        params['u'] = userName;
        $wnd.DataSourceJson.requestData($wnd.DataSourceJson.DATA_EMA, params);
    }-*/;
}
