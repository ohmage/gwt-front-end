package edu.ucla.cens.mobilize.client.model;


import edu.ucla.cens.mobilize.client.common.PromptType;

public class PromptResponse {
  private String promptId;
  private PromptType promptType;
  private String text; // (e.g., "How many hours did you sleep?")
  private int index; // for sorting
  private String responseRaw;
  private String responsePrepared;
  
  public String getPromptId() { return this.promptId; }
  public PromptType getPromptType() { return this.promptType; }
  public String getText() { return this.text; }
  public int getIndex() { return this.index; }
  public String getResponseRaw() { return this.responseRaw; }
  
  /**
   * For photo responses, the raw response would be the image id and this method 
   *   would return the image url.
   * For single or multiple choice responses, the raw response would contain the 
   *   choice value and this method would return the string the user saw when 
   *   originally making the choice.
   * @return Prompt response prepared for display. 
   */
  public String getResponsePrepared() { return this.responsePrepared; }
  
  public void setPromptId(String promptId) { this.promptId = promptId; }
  public void setPromptType(PromptType promptType) { this.promptType = promptType; }
  public void setText(String promptText) { this.text = promptText; }
  public void setResponse(String promptResponse) { this.responseRaw = promptResponse; }
  public void setIndex(int index) { this.index = index; }
  
  public void setResponsePreparedForDisplay(String responsePrepared) { 
    this.responsePrepared = responsePrepared; 
  }
}
