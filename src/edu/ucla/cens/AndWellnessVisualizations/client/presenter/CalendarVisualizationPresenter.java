package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.common.DropDownDefinition;
import edu.ucla.cens.AndWellnessVisualizations.client.common.SelectionModel;
import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchViewEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.SwitchViewEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MainViewState;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.AndWellnessRpcService;
import edu.ucla.cens.AndWellnessVisualizations.client.rpcservice.DataFilterService;
import edu.ucla.cens.AndWellnessVisualizations.client.view.CalendarVisualizationView;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataFilterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The Presenter for the CalendarViaulizationView.  Handles any Events from the View.
 * Parses any necessary data into the CalendarVisualizationModel and passes the Model
 * to the View.
 * 
 * @author jhicks
 *
 */
public class CalendarVisualizationPresenter implements Presenter,
        CalendarVisualizationView.Presenter {
   
    private Date currentMonth;
    private Map<Date, Double> currentDayData;
    private final AndWellnessRpcService rpcService;   // Used to make calls to the server for data
    private final HandlerManager eventBus;  
    private final CalendarVisualizationView view;
    
    public CalendarVisualizationPresenter(AndWellnessRpcService rpcService, 
            HandlerManager eventBus, CalendarVisualizationView view) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.view = view;
        this.view.setPresenter(this);
    }    
    
    public void bind() {
        // Handle switching to various Views
        eventBus.addHandler(SwitchViewEvent.TYPE,
            new SwitchViewEventHandler() {
                public void onSwitch(SwitchViewEvent event) {
                    MainViewState mvs = event.getAppState();
                    
                    // Check to see to which view we are switching
                    if (mvs == MainViewState.UPLOADVIEW) {
                        // Turn off the user list in upload view
                        view.enableUserList(false);
                    }
                    else if (mvs == MainViewState.GRAPHVIEW) {
                        // If we are an admin or researcher, enable the user list
                        if (userInfo.isAdmin() || userInfo.isResearcher()) {
                            view.enableUserList(true);
                        }
                    }
                    else {
                        throw new Error("SwitchViewEventHandler:onSwitch - Switching to an unknown view!");
                    }
                    
                }
            }); 
    }
    
    public void go(HasWidgets container) {
        bind();
        container.clear();
        container.add(view.asWidget());
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
        rpcService.fetchUserList(new AsyncCallback<ArrayList<UserInfo>>() {
            public void onSuccess(ArrayList<UserInfo> result) {
                // Reset the userSelectionModel
                userSelectionModel.clear();
                
                // handle the response
                users = result;
                sortUserList();
                view.setRowData(users);
            }
            
            public void onFailure(Throwable caught) {
                Window.alert("Error fetching user list");
            }
          });
    }
    
    // Called to setup display state when we receive a new UserInfo
    private void handleUserInfo() {
        // TODO: Remove this javascripty hack later
        this.userName = userInfo.getUserName();
    }
    
    private void sortUserList() {
        Collections.sort(users);
    }
    
    // Never show the user list when in the upload view
    // Only called from javascript so need to suppress unused
    private void doSwitchToUploadView() {
        view.enableUserList(false);
    }
    
    // Only show user list if the user is an admin or researcher
    // Only called from javascript so need to suppress unused
    private void doSwitchToGraphView() {
        if (userInfo.isAdmin() || userInfo.isResearcher()) {
            view.enableUserList(true);
        }
        else {
            view.enableUserList(false);
        }
    }

    // Ask for new data from the server using selections from the view
    private void doFetch() {
        Date startDate;
        long endTime, numDaysInMilliseconds;
        // make
        Date endDate;
        int numDays;
        
        // Make sure something is selected!
        if (endDateSelectionModel.getSelectedItems().size() == 0 ||
                numDaysSelectionModel.getSelectedItems().size() == 0) {
            // TODO: Throw an error here
            return;
        }
        
        // There should not be more than one selection
        endDate = endDateSelectionModel.getSelectedItems().get(0);
        numDays = numDaysSelectionModel.getSelectedItems().get(0).intValue();

        // All calls to the server use the ISO format for dates
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
        
        // Subtract numDays from the date
        endTime = endDate.getTime();
        numDaysInMilliseconds = numDays * 1000 * 60 * 60 * 24;
        startDate = new Date(endTime - numDaysInMilliseconds);
        
        
        // Hack these into Strings for javascript calls
        this.endDate = fmt.format(endDate);
        this.startDate = fmt.format(startDate);
        this.numDays = numDays;
        
        // If there is a selected userName, set it, else use hte current user name
        if (userSelectionModel.getSelectedItems().size() == 0) {
            this.userName = userInfo.getUserName();
        }
        else {
            this.userName = userSelectionModel.getSelectedItems().get(0).getUserName();
        }
        
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
        if (userName != "")
            params['u'] = userName;
        $wnd.DataSourceJson.requestData($wnd.DataSourceJson.DATA_EMA, params);
    }-*/;

    
    public void onEndDateSelected(Date selectedEndDate) {
        // Update the selection model with the new selected date
        endDateSelectionModel.clear();
        endDateSelectionModel.addSelection(selectedEndDate);
    }

    public void onNumDaysSelected(Integer numDays) {
        numDaysSelectionModel.clear();
        numDaysSelectionModel.addSelection(numDays);
    }

    public void onUserSelected(UserInfo selectedUser) {
        userSelectionModel.clear();
        userSelectionModel.addSelection(selectedUser);
    }
}
