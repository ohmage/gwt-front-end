package edu.ucla.cens.mobilize.client.model;

import edu.ucla.cens.mobilize.client.common.PromptType;

public class PromptResponse {
  private String promptId;
  private PromptType promptType;
  private String text; // (e.g., "How many hours did you sleep?")
  private String response;

  public String getPromptId() { return this.promptId; }
  public PromptType getPromptType() { return this.promptType; }
  public String getText() { return this.text; }
  public String getResponse() { return this.response; }
  
  public void setPromptId(String promptId) { this.promptId = promptId; }
  public void setPromptType(PromptType promptType) { this.promptType = promptType; }
  public void setText(String promptText) { this.text = promptText; }
  public void setResponse(String promptResponse) { this.response = promptResponse; }
    
}
