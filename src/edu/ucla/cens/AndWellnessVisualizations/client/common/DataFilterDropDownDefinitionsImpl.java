package edu.ucla.cens.AndWellnessVisualizations.client.common;

import java.util.ArrayList;

import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;

@SuppressWarnings("serial")
public class DataFilterDropDownDefinitionsImpl extends 
    ArrayList<DropDownDefinition<UserInfo>> {
  
  private static DataFilterDropDownDefinitionsImpl instance = null;
  
  public static DataFilterDropDownDefinitionsImpl getInstance() {
    if (instance == null) {
      instance = new DataFilterDropDownDefinitionsImpl();
    }
    
    return instance;
  }
  
  protected DataFilterDropDownDefinitionsImpl() {
      this.add(new DropDownDefinition<UserInfo>() {
          public void render(UserInfo c, StringBuilder sb) {        
              sb.append(c.getUserName());
          }
      });
  }
}
