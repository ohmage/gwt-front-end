package edu.ucla.cens.AndWellnessVisualizations.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

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
    private String campaignName;
    private String campaignVersion;
    private String userName;
    private List<String> promptIdList;
    
    public NewDataPointSelectionEvent(String campaignName, String campaignVersion, String userName, List<String> promptIdList) {
        this.campaignName = campaignName;
        this.campaignVersion = campaignVersion;
        this.userName = userName;
        this.promptIdList = promptIdList;
    }
        
    public String getCampaignName() { return campaignName; }
    public String getCampaignVersion() { return campaignVersion; }
    public String getUserName() { return userName; }
    public List<String> getPromptIds() { return promptIdList; }
    
    protected void dispatch(NewDataPointSelectionEventHandler handler) {
        handler.onSelect(this);
    }

    public Type<NewDataPointSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

}
