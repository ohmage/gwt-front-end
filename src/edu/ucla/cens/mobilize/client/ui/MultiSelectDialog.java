package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.List;

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
  
  public void setItemsToChooseFrom(List<String> items) {
    listBox.clear();
    for (String item : items) {
      // NOTE: value == text here. We may want value to be id instead.
      listBox.addItem(item, item);
    }
    // give list box a minimum width
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
