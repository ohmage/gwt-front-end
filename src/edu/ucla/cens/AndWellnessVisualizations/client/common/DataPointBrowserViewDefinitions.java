package edu.ucla.cens.AndWellnessVisualizations.client.common;

import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.model.CampaignInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ConfigurationInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.PromptInfo;
import edu.ucla.cens.AndWellnessVisualizations.client.model.SurveyInfo;

public class DataPointBrowserViewDefinitions {
    private DropDownDefinition<CampaignInfo> campaignInfoDropDownDefintion = 
        new DropDownDefinition<CampaignInfo>() {
        
            public void render(CampaignInfo t, StringBuilder sb) {
                sb.append(t.getCampaignName());
            }
    };
    
    
    private DropDownDefinition<ConfigurationInfo> configurationInfoDropDownDefinition =
        new DropDownDefinition<ConfigurationInfo>() {

            public void render(ConfigurationInfo t, StringBuilder sb) {
               sb.append(t.getCampaignVersion());
            }
    };
    
    
    private DropDownDefinition<SurveyInfo> surveyInfoDropDownDefinition = 
        new DropDownDefinition<SurveyInfo>() {

            public void render(SurveyInfo t, StringBuilder sb) {
                sb.append(t.getSurveyName());
            }
    };
    
    private ColumnDefinition<PromptInfo> promptInfoColumnDefinition = 
        new ColumnDefinition<PromptInfo>() {

            public Widget render(PromptInfo t) {
                // TODO Auto-generated method stub
                return null;
            }
    };

    // Grab the correct drop down definition
    public DropDownDefinition<CampaignInfo> getCampaignInfoDefinition() {
        return campaignInfoDropDownDefintion;
    }
    public DropDownDefinition<ConfigurationInfo> getConfigurationInfoDefinition() {
        return configurationInfoDropDownDefinition;
    }
    public DropDownDefinition<SurveyInfo> getSurveyInfoDefinition() {
        return surveyInfoDropDownDefinition;
    }
    public ColumnDefinition<PromptInfo> getDataPointDefinition() {
        return promptInfoColumnDefinition;
    }
}
