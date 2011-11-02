package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * This widget lists user-friendly names of items with an "X" next to each that,
 * when clicked, removes the item from the list. Items are uniquely identified
 * by an invisible "value" column, which can be retrieved with getItems(). Adding a 
 * duplicate item to the list (where uniqueness is enforced by the value column)
 * has no effect.
 * 
 * Fires value changed event when an item is added or deleted, where the value
 * give is "myitemvalue" for added item or "-myitemvalue" for a deleted item.
 * 
 * @author shlurbee
 */
public class ListWidget extends Composite implements HasValueChangeHandlers<String> {
  
  private Panel panel = new SimplePanel();
  private FlexTable flexTable = new FlexTable();

  private static final int USER_FRIENDLY_NAME_COLUMN = 0;
  private static final int DELETE_BUTTON_COLUMN = 1;
  private static final int VALUE_COLUMN = 2; // invisible
  
  public ListWidget() {
    panel.addStyleName("listWidget");
    panel.add(flexTable);
    
    // flex table is invisible when empty to keep it from affecting flow
    flexTable.setVisible(false);
    flexTable.setCellSpacing(0);
    flexTable.setBorderWidth(0);
    
    initWidget(panel);

  }
  
  // use this version when userFriendlyName == value
  public void addItem(String item) {
    addItem(item, item); 
  }
    
  public void addItem(String userFriendlyName, final String value) {
    if (userFriendlyName == null || value == null) return;
    else this.flexTable.setVisible(true);
    
    // check for duplicates
    boolean isAlreadyInTable = false;
    int firstEmptyRowIndex = this.flexTable.getRowCount();
    for (int i = 0; i < firstEmptyRowIndex; i++) {
      if (this.flexTable.getText(i, VALUE_COLUMN).equals(value)) {
        isAlreadyInTable = true;
        break;
      }
    }
    
    // if item is not already in table, add new row with two visible columns 
    // 0 = user-friendly name of item (this is what get displayed) 
    // 1 = "x" button that deletes the row when clicked,
    // 2 = invisible column to store value (this is what gets returnd by getItems)
    if (!isAlreadyInTable) {
      final int thisRow = firstEmptyRowIndex;
      flexTable.setText(thisRow, USER_FRIENDLY_NAME_COLUMN, userFriendlyName);
      Button deleteButton = new Button("X");
      final HasValueChangeHandlers<String> finalPointerToThis = this;
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          removeItem(value);
          flexTable.setVisible(flexTable.getRowCount() > 0);
          ValueChangeEvent.fire(finalPointerToThis, "-" + value);
        }
      });
      flexTable.setWidget(thisRow, DELETE_BUTTON_COLUMN, deleteButton);
      flexTable.setText(thisRow, VALUE_COLUMN, value);
      flexTable.getCellFormatter().setVisible(thisRow, VALUE_COLUMN, false);
      ValueChangeEvent.fire(this, value);
    }    
  }

  // no-op if valueToRemove is not in list
  public void removeItem(String valueToRemove) {
    for (int i = 0; i < this.flexTable.getRowCount(); i++) {
      if (this.flexTable.getText(i, VALUE_COLUMN).equals(valueToRemove)) {
        this.flexTable.removeRow(i);
        break;
      }
    }
  }
  
  public void clear() {
    this.flexTable.removeAllRows();
  }
  
  /**
   * @return List of displayed items or an empty List if none. (Should never return null.)
   */
  public List<String> getItems() {
    List<String> items = new ArrayList<String>();
    for (int i = 0; i < this.flexTable.getRowCount(); i++) {
      items.add(this.flexTable.getText(i, VALUE_COLUMN));
    }
    return items;
  }
  
  public void setStyleName(String styleName) {
    this.flexTable.setStyleName(styleName);
  }
  
  @Override
  // will pass "myvalue" if myvalue was added to list or "-myvalue" if myvalue was deleted
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
    // use Widget.addHandler method to register handler
    return addHandler(handler, ValueChangeEvent.getType());
  }
  
}
