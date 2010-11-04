package edu.ucla.cens.AndWellnessVisualizations.client.model;

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
    private String promptType;
    private String displayLabel;
    private String displayType;
    private String unit;
    
    public PromptInfo() {}
    
    public PromptInfo(PromptInfo prompt) {
        PromptInfo copy = new PromptInfo();
        
        copy.setPromptId(prompt.getPromptId());
        copy.setDisplayLabel(prompt.getDisplayLabel());
        copy.setPromptType(prompt.getPromptType());
        copy.setDisplayType(prompt.getDisplayType());
        copy.setUnit(prompt.getUnit());
    }
    
    public void setPromptId(String promptId) { this.promptId = promptId; }
    public String getPromptId() { return promptId; }
    
    public void setDisplayLabel(String displayLabel) { this.displayLabel = displayLabel; }
    public String getDisplayLabel() { return displayLabel; }
    
    public void setPromptType(String promptType) { this.promptType = promptType; }
    public String getPromptType() { return promptType; }
    
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
        myString.append(", unit: " + getUnit());
        
        return myString.toString();
    }
}
