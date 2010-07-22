package edu.ucla.cens.AndWellnessVisualizations.client.common;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ContactsColumnDefinitionsImpl extends 
    ArrayList<ColumnDefinition<ContactDetails>> {
  
  private static ContactsColumnDefinitionsImpl instance = null;
  
  public static ContactsColumnDefinitionsImpl getInstance() {
    if (instance == null) {
      instance = new ContactsColumnDefinitionsImpl();
    }
    
    return instance;
  }
  
  protected ContactsColumnDefinitionsImpl() {
    this.add(new ColumnDefinition<ContactDetails>() {
      public void render(ContactDetails c, StringBuilder sb) {
        sb.append("<input type='checkbox'/>");
      }

      public boolean isSelectable() {
        return true;
      }
    });

    this.add(new ColumnDefinition<ContactDetails>() {
      public void render(ContactDetails c, StringBuilder sb) {        
        sb.append("<div id='" + c.getDisplayName() + "'>" + c.getDisplayName() + "</div>");
      }

      public boolean isClickable() {
        return true;
      }
    });
  }
}
