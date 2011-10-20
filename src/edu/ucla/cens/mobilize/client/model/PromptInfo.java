package edu.ucla.cens.mobilize.client.model;

import edu.ucla.cens.mobilize.client.common.PromptType;

/**
 * Stores information about an individual prompt.
 * 
 * Is immutable, so the getters copy any mutable objects.
 * 
 * @author jhicks
 *
 */
public class PromptInfo {
  
    private String promptId;
    private PromptType promptType;
    private String displayLabel;
    private String displayType;
    private String unit;
    
    public PromptInfo() {}
    
    public PromptInfo(PromptInfo prompt) {
        setPromptId(prompt.getPromptId());
        setDisplayLabel(prompt.getDisplayLabel());
        setPromptType(prompt.getPromptType());
        setDisplayType(prompt.getDisplayType());
        setUnit(prompt.getUnit());
    }
    
    public void setPromptId(String promptId) { this.promptId = promptId; }
    public String getPromptId() { return promptId; }
    
    public void setDisplayLabel(String displayLabel) { this.displayLabel = displayLabel; }
    public String getDisplayLabel() { return displayLabel; }

    public void setPromptType(PromptType promptType) { this.promptType = promptType; }
    public PromptType getPromptType() { return promptType; }
    
    public void setDisplayType(String displayType) { this.displayType = displayType; }
    public String getDisplayType() { return displayType; }
    
    public void setUnit(String unit) { this.unit = unit; } 
    public String getUnit() { return unit; }

    /**
     * Translates this data container into a human readable string.
     */
    public String toString() {
        StringBuffer myString = new StringBuffer();
        myString.append("PromptInfo: ");
        myString.append("promptId: " + getPromptId());
        myString.append(", promptType: " + getPromptType());
        myString.append(", displaylabel: " + getDisplayLabel());
        myString.append(", displayType: " + getDisplayType());
        myString.append(", unit: " + getUnit()); // FIXME: isn't unit optional?
        
        return myString.toString();
    }
}
