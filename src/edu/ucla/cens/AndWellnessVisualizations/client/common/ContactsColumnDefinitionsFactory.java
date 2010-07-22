package edu.ucla.cens.AndWellnessVisualizations.client.common;

import java.util.ArrayList;
import java.util.List;

import edu.ucla.cens.AndWellnessVisualizations.model.UserInfo;

public class ContactsColumnDefinitionsFactory<T> {
  public static List<ColumnDefinition<UserInfo>> 
      getContactsColumnDefinitions() {
    return ContactsColumnDefinitionsImpl.getInstance();
  }

  public static List<ColumnDefinition<UserInfo>>
      getTestContactsColumnDefinitions() {
    return new ArrayList<ColumnDefinition<UserInfo>>();
  }
}
