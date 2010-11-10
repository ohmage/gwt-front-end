package edu.ucla.cens.AndWellnessVisualizations.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.ucla.cens.AndWellnessVisualizations.client.model.CampaignInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.PromptInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.SurveyInfo;

/**
 * Triggered whenever a new datapoint is selected from any UI that allows data point selection.
 * Contains enough information to ask for data points from the server excluding date range.
 * 
 * @author jhicks
 *
 */
public class NewDataPointSelectionEvent extends
        GwtEvent<NewDataPointSelectionEventHandler> {
    public static Type<NewDataPointSelectionEventHandler> TYPE = new Type<NewDataPointSelectionEventHandler>();
    
    // Data held in the event
    private CampaignInfo campaign;
    private String userName;
    private SurveyInfo survey;
    private PromptInfo dataPoint;
    
    public NewDataPointSelectionEvent(CampaignInfo campaign, String userName, SurveyInfo survey, PromptInfo dataPoint) {
        this.campaign = campaign;
        this.userName = userName;
        this.survey = survey;
        this.dataPoint = dataPoint;
    }
        
    public CampaignInfo getCampaign() { return campaign; }
    public String getUserName() { return userName; }
    public SurveyInfo getSurvey() { return survey; }
    public PromptInfo getDataPoint() { return dataPoint; }
    
    protected void dispatch(NewDataPointSelectionEventHandler handler) {
        handler.onSelect(this);
    }

    public Type<NewDataPointSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

}
