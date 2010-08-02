package edu.ucla.cens.AndWellnessVisualizations.client.common;

import java.util.ArrayList;
import java.util.List;

import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;

public class DataFilterDropDownDefinitionsFactory<T> {
  public static List<DropDownDefinition<UserInfo>> 
      getDataFilterDropDownDefinitions() {
    return DataFilterDropDownDefinitionsImpl.getInstance();
  }

  public static List<DropDownDefinition<UserInfo>>
      getTestDataFilterDropDownDefinitions() {
    return new ArrayList<DropDownDefinition<UserInfo>>();
  }
}
