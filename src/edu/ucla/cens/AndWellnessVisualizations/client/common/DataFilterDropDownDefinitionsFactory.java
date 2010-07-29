package edu.ucla.cens.AndWellnessVisualizations.client.common;

import java.util.ArrayList;
import java.util.List;

import edu.ucla.cens.AndWellnessVisualizations.client.model.UserInfo;

public class DataFilterDropDownDefinitionsFactory<T> {
  public static List<ColumnDefinition<UserInfo>> 
      getDataFilterColumnDefinitions() {
    return DataFilterDropDownDefinitionsImpl.getInstance();
  }

  public static List<ColumnDefinition<UserInfo>>
      getTestDataFilterColumnDefinitions() {
    return new ArrayList<ColumnDefinition<UserInfo>>();
  }
}
