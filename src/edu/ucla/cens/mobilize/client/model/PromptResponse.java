package edu.ucla.cens.mobilize.client.model;

// wraps prompt info and data in one class to make display easier
public class PromptResponse {
  PromptInfo info;
  DataPointAwData data;
  
  public PromptResponse(PromptInfo info, DataPointAwData data) {
    this.info = info;
    this.data = data;
  }
  
  public String getText() { return this.info.getDisplayLabel(); }
  public String getValue() { return this.data.getValue(); }
  public String getPromptType() { return this.info.getPromptType(); }
}
