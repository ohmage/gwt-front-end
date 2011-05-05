package edu.ucla.cens.mobilize.client.model;

import edu.ucla.cens.mobilize.client.common.PromptType;

public class PromptResponse {
  private String promptId;
  private PromptType promptType;
  private String response;

  public String getPromptId() { return this.promptId; }
  public String getResponse() { return this.response; }
  public PromptType getPromptType() { return this.promptType; }
  
  public void setPromptId(String promptId) { this.promptId = promptId; }
  public void setResponse(String promptResponse) { this.response = promptResponse; }
  public void setPromptType(PromptType promptType) { this.promptType = promptType; }
    
}
