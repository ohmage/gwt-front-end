package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.ucla.cens.AndWellnessVisualizations.client.common.ColumnDefinition;
import edu.ucla.cens.AndWellnessVisualizations.client.common.DropDownDefinition;

/**
 * The implementation of data point browser view.  The user must select
 * the campaign/version, user, and survey from which to grab data.  The data point 
 * browser presenter should use this information to populate the data point list.
 * 
 * @author jhicks
 *
 */
public class DataPointBrowserViewImpl<T,U,V,W> extends Composite implements DataPointBrowserView<T,U,V,W> {
    @UiTemplate("DataPointBrowserView.ui.xml")
    interface DataPointBrowserViewUiBinder extends UiBinder<Widget, DataPointBrowserViewImpl<?,?,?,?>> {}
    private static DataPointBrowserViewUiBinder uiBinder =
      GWT.create(DataPointBrowserViewUiBinder.class);
    
    // Logging capability
    private static Logger _logger = Logger.getLogger(DataPointBrowserViewImpl.class.getName());
    
    // Fields defined in the ui XML
    @UiField VerticalPanel dataPointBrowserMainPanel;
    @UiField ListBox dataPointBrowserCampaignList;
    @UiField ListBox dataPointBrowserConfigurationList;
    @UiField ListBox dataPointBrowserUserList;
    @UiField ListBox dataPointBrowserSurveyList;
    
    // To show and hide each selection field
    @UiField VerticalPanel dataPointCampaignPanel;
    @UiField VerticalPanel dataPointCampaignVersionPanel;
    @UiField VerticalPanel dataPointUserPanel;
    @UiField VerticalPanel dataPointSurveyPanel;
    @UiField VerticalPanel dataPointDPPanel;
    
    // The CellTable to hold the data points
    @UiField(provided=true)
    CellTable<W> dataPointBrowserDataPointTable;
    
    // Call the presenter in response to user interaction
    private Presenter<T,U,V,W> presenter;
    
    // Use the render definitions to display various lists
    private DropDownDefinition<T> campaignListDropDownDefinition;
    private DropDownDefinition<U> configurationListDropDownDefinition;
    private DropDownDefinition<V> surveyListDropDownDefinition;
    private ColumnDefinition<W> dataPointColumnDefinition;
    
    // Save the data passed to the view
    List<T> campaignList;
    List<U> configurationList;
    List<String> userList;
    List<V> surveyList;
    List<W> dataPointData;
    
    // Constructor, initialize the widget
    public DataPointBrowserViewImpl() {
        // Create a clickable cell to render each data point
        ClickableTextCell textCell = new ClickableTextCell();
        
        // Initialize the dataPoint cell table
        dataPointBrowserDataPointTable = new CellTable<W>();
        dataPointBrowserDataPointTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        
        // Create name column.
        Column<W,String> nameColumn = new Column<W,String>(textCell) {
            public String getValue(W field) {
                StringBuilder string = new StringBuilder();
                dataPointColumnDefinition.render(field, string);
                return string.toString();
            }
        };
        this.dataPointBrowserDataPointTable.addColumn(nameColumn);
        
        // Add a selection model to handle data point selections
        final SingleSelectionModel<W> selectionModel = new SingleSelectionModel<W>();
        dataPointBrowserDataPointTable.setSelectionModel(selectionModel);
        
        // Define a new SelectionChangeEvent handler to pass the selection to the presenter
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                // Grab the selected W
                W selected = selectionModel.getSelectedObject();
            
                // Pass it back to the present to handle
                if (selected != null && presenter != null) {
                    presenter.dataPointSelected(selected);
                }
            }
        });
        
        initWidget(uiBinder.createAndBindUi(this));
        
        _logger.finer("Instantiated DataPointBrowserViewImpl");
    }
  
    // Setup our presenter
    public void setPresenter(Presenter<T,U,V,W> presenter) {
        this.presenter = presenter;
    }

    public void setDefinitions(
            DropDownDefinition<T> campaignListDropDownDefinition,
            DropDownDefinition<U> configurationListDropDownDefinition,
            DropDownDefinition<V> surveyListDropDownDefinition,
            ColumnDefinition<W> dataPointColumnDefinition) {
        this.campaignListDropDownDefinition = campaignListDropDownDefinition;
        this.configurationListDropDownDefinition = configurationListDropDownDefinition;
        this.surveyListDropDownDefinition = surveyListDropDownDefinition;
        this.dataPointColumnDefinition = dataPointColumnDefinition;
    }

    /**
     * Sets the campaign drop down box with the new campaign information.
     * Uses the campaign drop down definition to determine how to display the
     * generic T type.
     * 
     * @param campaignList A list of generic type T to render.
     */
    public void setCampaignList(List<T> campaignList) {
        this.campaignList = campaignList;
        
        // Clear any old data out
        dataPointBrowserCampaignList.clear();
        
        // Loop over the campaignList, render, and add to the display
        for (T campaign : campaignList) {
            StringBuilder campaignString = new StringBuilder();
            campaignListDropDownDefinition.render(campaign, campaignString);
            dataPointBrowserCampaignList.addItem(campaignString.toString());
        }
    }
    
    /**
     * Sets the configuration drop down box with the new campaign configuration information.
     * Uses the configuration drop down definition to determine how to render the
     * generic U type.
     * 
     * @param configurationList A list of generic type U to render.
     */
    public void setConfigurationList(List<U> configurationList) {
        this.configurationList = configurationList;
        
        // Clear old data out
        dataPointBrowserConfigurationList.clear();
        
        // Loop over the configuration list, render, and add to the display
        for (U configuration : configurationList) {
            StringBuilder configurationString = new StringBuilder();
            configurationListDropDownDefinition.render(configuration, configurationString);
            dataPointBrowserConfigurationList.addItem(configurationString.toString());
        }
        
    }

    /**
     * Sets the user drop down box with new user information.
     * 
     * @param userList A list of user names as Strings to display.
     */
    public void setUserList(List<String> userList) {
        this.userList = userList;
        
        dataPointBrowserUserList.clear();
        
        for (String user : userList) {
            dataPointBrowserUserList.addItem(user);
        }
    }
    
    /**
     * Sets the selected user if it exists in the current user list
     * 
     * @param user The user to select
     */
    public void setSelectedUser(String user) {
    	_logger.finer("Setting selected user to: " + user);
    	
    	int userIndex = userList.indexOf(user);
    	if (userIndex >= 0) {
    		dataPointBrowserUserList.setSelectedIndex(userIndex);
    	}
    }

    /**
     * Sets the survey drop down box with the new survey information.
     * Uses the survey drop down definition to determine how to display
     * the generic V type.
     * 
     * @param surveyList A list of generic type V to render.
     */
    public void setSurveyList(List<V> surveyList) {
        this.surveyList = surveyList;
        
        // Clear any old data out
        dataPointBrowserSurveyList.clear();
        
        // Loop over the surveyList, render, and add to the display
        for (V survey : surveyList) {
            StringBuilder surveyString = new StringBuilder();
            surveyListDropDownDefinition.render(survey, surveyString);
            dataPointBrowserSurveyList.addItem(surveyString.toString());
        }
    }

    /**
     * Sets the data point list with the new data point information.
     * Uses the data point column definition to determine how to display
     * the generic W type.
     * 
     * @param dataPointData A list of generic type W to render.
     */
    public void setDataPointList(List<W> dataPointData) {
        this.dataPointData = dataPointData;
          
        dataPointBrowserDataPointTable.setRowCount(dataPointData.size(), true);
        dataPointBrowserDataPointTable.setRowData(0, this.dataPointData);
    }
    
    /**
     * Methods to show hide the various selection fields
     */
    public void setCampaignVisibility(boolean visible) {
    	dataPointCampaignPanel.setVisible(visible);
    }
    public void setVersionVisibility(boolean visible) {
    	dataPointCampaignVersionPanel.setVisible(visible);
    }
    public void setUserVisibility(boolean visible) {
    	dataPointUserPanel.setVisible(visible);
    }
    public void setSurveyVisibility(boolean visible) {
    	dataPointSurveyPanel.setVisible(visible);
    }
    public void setDPVisibility(boolean visible) {
    	dataPointDPPanel.setVisible(visible);
    }
    
    /**
     * Methods to pass user interactions back to the presenter.
     */

    @UiHandler("dataPointBrowserCampaignList")
    void onDataPointBrowserCampaignListChanged(ChangeEvent event) {        
        // Find which row was selected
        int selectedCampaign = dataPointBrowserCampaignList.getSelectedIndex();
        
        // Pass the selected campaign back to the presenter
        if (presenter != null) {
            presenter.campaignSelected(campaignList.get(selectedCampaign));
        }
    }
    
    @UiHandler("dataPointBrowserConfigurationList")
    void onDataPointBrowserConfigurationListChanged(ChangeEvent event) {
        // Find which row was selected
        int selectedConfiguration = dataPointBrowserConfigurationList.getSelectedIndex();
        
        // Pass the selected campaign back to the presenter
        if (presenter != null) {
            presenter.configurationSelected(configurationList.get(selectedConfiguration));
        }
    }
    
    @UiHandler("dataPointBrowserUserList")
    void onDataPointBrowserUserListChanged(ChangeEvent event) {
        if (presenter != null) {
            int selectedUser = dataPointBrowserUserList.getSelectedIndex();
            presenter.userSelected(dataPointBrowserUserList.getItemText(selectedUser));
        }
    }
    
    @UiHandler("dataPointBrowserSurveyList")
    void onDataPointBrowserSurveyListChanged(ChangeEvent event) {
        // Find which row was selected
        int selectedSurvey = dataPointBrowserSurveyList.getSelectedIndex();
        
        if (presenter != null) {
            presenter.surveySelected(surveyList.get(selectedSurvey));
        }
    }

    
    public void resetData() {
        dataPointBrowserCampaignList.clear();
        dataPointBrowserConfigurationList.clear();
        dataPointBrowserUserList.clear();
        dataPointBrowserSurveyList.clear();
        // TODO: reset data point table
    }
    
    public Widget asWidget() {
        return this;
    }
}
