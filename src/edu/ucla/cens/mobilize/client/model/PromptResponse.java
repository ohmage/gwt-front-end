package edu.ucla.cens.mobilize.client.model;

import edu.ucla.cens.mobilize.client.dataaccess.awdataobjects.DataPointAwData;

// wraps prompt info and data in one class to make display easier
public class PromptResponse {
  //PromptInfo info;
  //DataPointAwData data;
  private String text;
  private String value;
  private String type;
  
  // info can be null. data cannot
  public PromptResponse(PromptInfo info, DataPointAwData data) {
    //this.info = info;
    //this.data = data;
    
    // text is full question if available, data point display label otherwise
    this.text = (info != null) ? info.getDisplayLabel() : data.getLabel();
    this.value = data.getValue();
    this.type = data.getType();
  }
  
  public String getText() { return this.text; }
  public String getValue() { return this.value; }
  public String getPromptType() { return this.type; }
}
