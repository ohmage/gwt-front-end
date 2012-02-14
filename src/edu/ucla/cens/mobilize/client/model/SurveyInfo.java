package edu.ucla.cens.mobilize.client.model;

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
     * Copy constructor
     * @param survey The survey information to copy.
     */
    public SurveyInfo(SurveyInfo survey) {
        setSurveyName(survey.getSurveyName());
        setPromptList(survey.getPromptList());
    }
    
    // Setters/getters, all getters copy for mtuable objects
    public void setSurveyName(String surveyName) { 
      this.surveyName = surveyName; 
    }
    
    public String getSurveyName() { 
      return surveyName; 
    }
    
    public void setSurveyTitle(String surveyTitle) { this.surveyTitle = surveyTitle; }
    public String getSurveyTitle() { return surveyTitle; }
    
    public void setSurveyDescription(String surveyDescription) { this.surveyDescription = surveyDescription; }
    public String getSurveyDescription() { return surveyDescription; }
    
    public void addPrompt(PromptInfo prompt) {
      if (prompt != null) promptList.add(prompt); 
    }
    
    public void setPromptList(List<PromptInfo> promptList) { 
      this.promptList = promptList; 
    }
    
    public List<PromptInfo> getPromptList() {
        List<PromptInfo> copy = new ArrayList<PromptInfo>();
        for (PromptInfo prompt:promptList) {
            copy.add(new PromptInfo(prompt));
        }
        return copy;
    }
    
    /**
     * Returns a copy of the PromptInfo object with id promptId or null 
     * if none found
     */
    public PromptInfo getPrompt(String promptId) {
      PromptInfo retval = null;
      for (PromptInfo pinfo : this.promptList) {
        if (pinfo.getPromptId().equals(promptId)) {
          retval = new PromptInfo(pinfo);
          break;
        }
      }
      return retval;
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
