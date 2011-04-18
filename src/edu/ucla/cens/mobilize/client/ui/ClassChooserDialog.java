package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ClassChooserDialog {

  private static ClassChooserDialogUiBinder uiBinder = GWT
      .create(ClassChooserDialogUiBinder.class);

  @UiField DialogBox dialog;
  @UiField ListBox classesToChooseFromListBox;
  @UiField Button addButton;
  @UiField Button cancelButton;
  
  interface ClassChooserDialogUiBinder extends
      UiBinder<Widget, ClassChooserDialog> {
  }

  public ClassChooserDialog() {
    uiBinder.createAndBindUi(this);
    ClassChooserDialogUiBinder uiBinder = GWT.create(ClassChooserDialogUiBinder.class);
    uiBinder.createAndBindUi(this);
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
  
  public void setClassesToChooseFrom(List<String> classes) {
    classesToChooseFromListBox.clear();
    for (String className : classes) {
      // NOTE: value == text here. We may want value to be id instead.
      classesToChooseFromListBox.addItem(className, className);
    }
  }
  
  public void setSubmitHandler(ClickHandler handler) {
    this.addButton.addClickHandler(handler);
  }

  public List<String> getSelectedClasses() {
    List<String> selected = new ArrayList<String>();
    for (int i = 0; i < classesToChooseFromListBox.getItemCount(); i++) {
      if (classesToChooseFromListBox.isItemSelected(i)) {
        selected.add(classesToChooseFromListBox.getValue(i));
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
