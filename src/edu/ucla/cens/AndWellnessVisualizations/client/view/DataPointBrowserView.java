package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.common.ColumnDefinition;


public interface DataPointBrowserView<T> {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter {
        void logoutClicked();
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter presenter);
    
    // Sets the column definitions that define how the data point columns are displayed in our view
    public void setDataPointColumnDefinition(ColumnDefinition<T> dataPointColumnDefinition);
    
    // Sets the campaign/version list
    void setCampaignList(List<String> campaignList);
    
    // Sets the user list
    void setUserList(List<String> userList);
    
    // Sets the data point list
    void setDataPointList(List<T> dataPointData);
    
    Widget asWidget();
}
