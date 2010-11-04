package edu.ucla.cens.AndWellnessVisualizations.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores information about a single survey from a specific campaign.
 * 
 * Immutable object, all getters copy when return mutable objects.
 * 
 * @author jhicks
 *
 */
public class SurveyInfo {
    private String surveyName;
    private String surveyTitle;
    private String surveyDescription;
    private List<PromptInfo> promptList;
    
    public SurveyInfo() {
        promptList = new ArrayList<PromptInfo>();
    }
    
    /**
     * Copy constructor, creates a copy of the passed in SurveyInfo.
     * 
     * @param survey The survey information to copy.
     */
    public SurveyInfo(SurveyInfo survey) {
        SurveyInfo copy = new SurveyInfo();
        
        copy.setSurveyName(survey.getSurveyName());
        copy.setPromptList(survey.getPromptList());
    }
    
    // Setters/getters, all getters copy for mtuable objects
    public void setSurveyName(String surveyName) { this.surveyName = surveyName; }
    public String getSurveyName() { return surveyName; }
    
    public void setSurveyTitle(String surveyTitle) { this.surveyTitle = surveyTitle; }
    public String getSurveyTitle() { return surveyTitle; }
    
    public void setSurveyDescription(String surveyDescription) { this.surveyDescription = surveyDescription; }
    public String getSurveyDescription() { return surveyDescription; }
    
    public void addPrompt(PromptInfo prompt) { promptList.add(prompt); }
    public void setPromptList(List<PromptInfo> promptList) { this.promptList = promptList; }
    public List<PromptInfo> getPromptList() {
        List<PromptInfo> copy = new ArrayList<PromptInfo>();
        for (PromptInfo prompt:promptList) {
            copy.add(new PromptInfo(prompt));
        }
        return copy;
    }
    
    /**
     * Translates this data container into a human readable string.
     */
    public String toString() {
        StringBuffer myString = new StringBuffer();
        myString.append("SurveyInfo: ");
        myString.append("surveyName: " + getSurveyName());
        myString.append(", surveyTitle: " + getSurveyTitle());
        myString.append(", surveyDesc: " + getSurveyDescription());
        myString.append(", promptList: " + getPromptList());
        
        return myString.toString();
    }
}
