package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class MultiSelectDialog extends Composite {

  private static MultiSelectDialogUiBinder uiBinder = GWT
      .create(MultiSelectDialogUiBinder.class);

  @UiField DialogBox dialog;
  @UiField ListBox listBox;
  @UiField Button addButton;
  @UiField Button cancelButton;

  boolean isEmpty = true;
  
  interface MultiSelectDialogUiBinder extends
      UiBinder<Widget, MultiSelectDialog> {
  }

  public MultiSelectDialog() {
    initWidget(uiBinder.createAndBindUi(this));
    dialog.setGlassEnabled(true);
    dialog.hide();
    bind();
  }

  private void bind() {
    cancelButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialog.hide();
      }
    });
  }
  
  // use this version if display text and value (id) are the same
  public void setItems(List<String> items) {
    List<String> sortedItems = new ArrayList<String>(items);
    Collections.sort(sortedItems);
    listBox.clear();
    for (String item : sortedItems) {
      listBox.addItem(item, item); // value and display text are the same
    }
    giveListBoxMinimumWidth();
  }
  
  // use this version if you want to display a user friendly string
  // but get back an id as the selected item value
  public void setItems(Map<String, String> itemIdToDisplayNameMap) {
    List<String> valuesAndKeys = new ArrayList<String>();
    for (String key : itemIdToDisplayNameMap.keySet()) {
      String value = itemIdToDisplayNameMap.get(key);
      valuesAndKeys.add(value + "###" + key);
    }
    Collections.sort(valuesAndKeys); // sort by value first, then key
    for (String valueKeyPair : valuesAndKeys) {
      String[] arr = valueKeyPair.split("###"); // 0=value, 1=key
      // value becomes display string, key becomes listItem "value"
      listBox.addItem(arr[0], arr[1]); 
    }
    giveListBoxMinimumWidth();
  }
  
  public void giveListBoxMinimumWidth() {
    if (listBox.getElement().getClientWidth() < 200) {
      listBox.setWidth("200px");
    }    
  }
  
  public void setCaption(String caption) {
    this.dialog.setText(caption);    
  }
  
  public void setSubmitHandler(ClickHandler handler) {
    this.addButton.addClickHandler(handler);
  }

  public List<String> getSelectedItems() {
    List<String> selected = new ArrayList<String>();
    for (int i = 0; i < listBox.getItemCount(); i++) {
      if (listBox.isItemSelected(i)) {
        selected.add(listBox.getValue(i));
      }
    }
    return selected;
  }
  
  public void show() {
    dialog.center();
  }
  
  public void hide() {
    dialog.hide();
  }
  
}
