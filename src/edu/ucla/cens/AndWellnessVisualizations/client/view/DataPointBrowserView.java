package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.common.ColumnDefinition;
import edu.ucla.cens.AndWellnessVisualizations.client.common.DropDownDefinition;


public interface DataPointBrowserView<T,U,V,W> {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter<T,U,V,W> {
        void campaignSelected(T campaign);
        void configurationSelected(U configuration);
        void userSelected(String userName);
        void surveySelected(V survey);
        void dataPointSelected(W dataPoint);
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter<T,U,V,W> presenter);
    
    // Sets the column definitions that define how the data point columns are displayed in our view
    void setDefinitions(DropDownDefinition<T> campaignListDropDownDefinition,
            DropDownDefinition<U> configurationListDropDownDefinition,
            DropDownDefinition<V> surveyListDropDownDefinition,
            ColumnDefinition<W> dataPointColumnDefinition);
    
    // Sets the campaign list
    void setCampaignList(List<T> campaignList);
    
    // Sets the configuration list
    void setConfigurationList(List<U> configurationList);
    
    // Sets the user list
    void setUserList(List<String> userList);
    
    // Sets the survey list
    void setSurveyList(List<V> surveyList);
    
    // Sets the data point list
    void setDataPointList(List<W> dataPointData);
    
    // Resets the data lists
    void resetData();
    
    // Update the panel visiblities
    void setCampaignVisibility(boolean visible);
    void setVersionVisibility(boolean visible);
    void setUserVisibility(boolean visible);
    void setSurveyVisibility(boolean visible);
    void setDPVisibility(boolean visible);
    
    Widget asWidget();
}
