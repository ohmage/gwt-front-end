package edu.ucla.cens.AndWellnessVisualizations.client.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ucla.cens.AndWellnessVisualizations.client.common.SetModel;
import edu.ucla.cens.AndWellnessVisualizations.client.event.CampaignConfigurationEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.event.CampaignConfigurationEventHandler;
import edu.ucla.cens.AndWellnessVisualizations.client.event.DataBrowserSelectionEvent;
import edu.ucla.cens.AndWellnessVisualizations.client.model.CampaignInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigurationInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.PromptInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.SurveyInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.view.DataPointBrowserView;

public class DataPointBrowserPresenter implements Presenter,
        DataPointBrowserView.Presenter<CampaignInfo,ConfigurationInfo,SurveyInfo,PromptInfo> {

    // Standard presenter fields
    private EventBus eventBus;
    private DataPointBrowserView<CampaignInfo,ConfigurationInfo,SurveyInfo,PromptInfo> view;
    
    // Contains all information about the currently logged in user
    private UserInfo userInfo;
    
    // Contains all selection states from the view
    SetModel<CampaignInfo> setCampaign = new SetModel<CampaignInfo>();
    SetModel<ConfigurationInfo> setConfiguration = new SetModel<ConfigurationInfo>();
    SetModel<String> setUserName = new SetModel<String>();
    SetModel<SurveyInfo> setSurvey = new SetModel<SurveyInfo>();
    SetModel<PromptInfo> setDataPoint = new SetModel<PromptInfo>();
    
    private static Logger _logger = Logger.getLogger(DataPointBrowserPresenter.class.getName());
    
    public DataPointBrowserPresenter(EventBus eventBus, 
            DataPointBrowserView<CampaignInfo,ConfigurationInfo,SurveyInfo,PromptInfo> view) {
        this.eventBus = eventBus;
        this.view = view;
        this.view.setPresenter(this);
    }
    
    /**
     * Runs the presenter by attaching the view to the passed container.
     */
    public void go(HasWidgets container) {
        bind();
        container.clear();
        container.add(view.asWidget());
    }

    /**
     * Binds to necessary events on the event bus.
     */
    private void bind() {
        eventBus.addHandler(CampaignConfigurationEvent.TYPE, new CampaignConfigurationEventHandler() {
            // When we receive new campaign configuration information, save locally and load into
            // the attached view
            public void onReceive(CampaignConfigurationEvent event) {
                userInfo = event.getUserInfo();
                receiveCampaignConfiguration(userInfo);
            }
        });
    }
    
    /**
     * Call to show/hide the campaign/config selection fields
     */
    public void setCampaignVisibility(boolean visible) {
    	if (view != null) {
    		view.setCampaignVisibility(visible);
    		view.setVersionVisibility(visible);
    	}
    }
    
    public void setUserVisibility(boolean visible) {
    	if (view != null) {
    		view.setUserVisibility(visible);
    	}
    }
    
    public void setSurveyVisibility(boolean visible) {
    	if (view != null) {
    		view.setSurveyVisibility(visible);
    	}
    }
    
    public void setDPVisibility(boolean visible) {
    	if (view != null) {
    		view.setDPVisibility(visible);
    	}
    }
    
    /**
     * Updates the view based on the new configuration information.  Load the new
     * campaign info into the campaign list and user list.  If there is
     * only one campaign or only one user, automatically select those.  If these is only
     * one campaign, populate the survey list from that campaign, select the first survey in 
     * the list, and automatically populate the data point list from the first survey.
     * 
     * @param newConfig The new configuration information.
     */
    private void receiveCampaignConfiguration(UserInfo newConfig) {
        List<CampaignInfo> campaignList;
        
        // Clear everything out
        clearModels();
        
        // Reset the view to get ready for new data
        view.resetData();
        
        // Update the campaign list
        campaignList = newConfig.getCampaignList();
        view.setCampaignList(campaignList);
        
        // Update the selected campaign as the first campaign in the list
        updateSelectedCampaign(campaignList.get(0));
    }

    /**
     * Call when a new campaign has been selected.  Updates the view with
     * new surveys and data points for the new campaign.
     * 
     * @param campaign The selected campaign.
     */
    public void campaignSelected(CampaignInfo campaign) {
        // Unset the version, user, survey, and datapoint
        setConfiguration.clear();
        setUserName.clear();
        setSurvey.clear();
        setDataPoint.clear();
        
        // Update the view and selections for the new campaign
        updateSelectedCampaign(campaign);
        
        // Send out an event with this info
        sendCampaignSelectionEvent();
    }
    
    /** 
     * Call when a new campaign configuration has been selected.  Updates the
     * view with new surveys and data points.
     */
    public void configurationSelected(ConfigurationInfo configuration) {
        // Unset the current survey and datapoint
        setSurvey.clear();
        setDataPoint.clear();
        
        // Update the view with and selections for the new configuration
        updateSelectedConfiguration(configuration);
        
        sendVersionSelectionEvent();
    }

    /**
     * Call when a new user has been selected.  If a campaign, survey,
     * and data point have also been selected, ask for new data from the server.
     * 
     * @param userName The selected user name.
     */
    public void userSelected(String userName) {
        setUserName.updateSetItem(userName);
        
        sendUserNameSelectionEvent();
    }

    /**
     * Call when a new survey is selected.  Unset the currently set prompt and update the prompt list.
     */
    public void surveySelected(SurveyInfo survey) {
        // Make the user select a new data point after the survey selection switches
        setDataPoint.clear();
        
        updateSelectedSurvey(survey);
    }
    
    /**
     * Call when a new data point is selected.  Ask for new data from server.
     */

    public void dataPointSelected(PromptInfo dataPoint) {
        setDataPoint.updateSetItem(dataPoint);
        
        sendDataPointSelectionEvent();
    }

    /**
     * Sends out a new data request event using the currently selected
     * campaign, user, survey, and prompt id.  Silently fails if one of the
     * above are not selected.
     */
    private void sendCampaignSelectionEvent() {
    	String campaign;
    	DataBrowserSelectionEvent event = new DataBrowserSelectionEvent(DataBrowserSelectionEvent.DataType.campaignName);
    	
        // Make sure we have all necessary data to access the server.
        if (!setCampaign.isSet()) {
            _logger.warning("No campaign is selected.");
            return;
        }
        
        campaign = setCampaign.getSetItem().getCampaignName();
        event.add(campaign);
        
        _logger.fine("Sending out selection event for campaign: " + campaign);
    	
    	eventBus.fireEvent(event);
    }
    
    private void sendVersionSelectionEvent() {
    	String config;
    	DataBrowserSelectionEvent event = new DataBrowserSelectionEvent(DataBrowserSelectionEvent.DataType.campaignVersion);
    	
    	if (!setConfiguration.isSet()) {
    		_logger.warning("No campaign version is selected.");
    		return;
    	}
    	
    	config = setConfiguration.getSetItem().getCampaignVersion();
    	event.add(config);
    	
    	_logger.fine("Sending out selection event for configuration: " + config);
    	
    	eventBus.fireEvent(event);
    }
    
    private void sendUserNameSelectionEvent() {
    	String userName;
    	DataBrowserSelectionEvent event = new DataBrowserSelectionEvent(DataBrowserSelectionEvent.DataType.userName);
    	
    	if (!setUserName.isSet()) {
    		_logger.warning("No user is selected.");
    		return;
    	}
    	
    	userName = setUserName.getSetItem();
    	event.add(userName);
    	
    	_logger.fine("Sending out selection event for user name: " + userName);
    	
    	eventBus.fireEvent(event);
    }
    
    private void sendDataPointSelectionEvent() {
    	String dataPoint;
    	DataBrowserSelectionEvent event = new DataBrowserSelectionEvent(DataBrowserSelectionEvent.DataType.promptIdList);
    	
    	if (!setDataPoint.isSet()) {
    		_logger.warning("No prompt id is selected.");
    		return;
    	}
    	
    	dataPoint = setDataPoint.getSetItem().getPromptId();
    	event.add(dataPoint);
    	
    	_logger.fine("Sending out selection event for prompt id: " + dataPoint);
    	
    	eventBus.fireEvent(event);
    }
    
    
    /**
     * Selects the passed in campaign.  Update the view's configuration list and user list.
     * Auto select the first user and configuration in the list.
     * 
     * @param campaign The newly selected campaign
     */
    private void updateSelectedCampaign(CampaignInfo campaign) {
        setCampaign.updateSetItem(campaign);
        List<ConfigurationInfo> configurationList = campaign.getConfigurationList();
        view.setConfigurationList(configurationList);
        
        // Set the user list from this campaign
        List<String> userList = campaign.getUserList();
        view.setUserList(userList);
        
        // Auto select the first user in the list
        setUserName.updateSetItem(userList.get(0));
        // ...and send out an event announcing this
        sendUserNameSelectionEvent();
        
        // Auto select the first configuration in the list
        updateSelectedConfiguration(configurationList.get(0));        
    }
    
    /**
     * Selects the passed in configuration.  Update the view's survey list and data point list.
     * Auto selects the first survey in the survey list.
     * 
     * @param configuration The newly selected configuration
     */
    private void updateSelectedConfiguration(ConfigurationInfo configuration) {
        setConfiguration.updateSetItem(configuration);
        // .. and send out an announcment of this
        sendVersionSelectionEvent();
        
        List<SurveyInfo> surveyList = configuration.getSurveyList();
        view.setSurveyList(surveyList);
        
        // Set the promptId list with the first survey in the list
        SurveyInfo firstSurvey = surveyList.get(0);
        updateSelectedSurvey(firstSurvey);
    }
    
    /**
     * Selects the passed in survey.  Updates the view's data point list.
     * Filters through the data point list and removes any data points with
     * display type metadata.
     * 
     * @param survey
     */
    private void updateSelectedSurvey(SurveyInfo survey) {
        setSurvey.updateSetItem(survey);
        
        List<PromptInfo> promptList = survey.getPromptList();
        List<PromptInfo> noMetadataPromptList = new ArrayList<PromptInfo>();
        
        // Filter out any metadatas in the list
        for (PromptInfo prompt : promptList) {
            if (! prompt.getDisplayType().equals("metadata")) {
                noMetadataPromptList.add(prompt);
            }
        }
        
        view.setDataPointList(noMetadataPromptList);
    }
    
    /**
     * Clears out all data currently stored in our models.
     */
    private void clearModels() {
        setCampaign.clear();
        setConfiguration.clear();
        setUserName.clear();
        setSurvey.clear();
        setDataPoint.clear();
    }
}
