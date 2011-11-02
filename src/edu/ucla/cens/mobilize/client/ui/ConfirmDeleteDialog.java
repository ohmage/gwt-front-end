package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;

public class ConfirmDeleteDialog {
  private static DialogBox _dialog;
  
  public static void show(String msg, final ClickHandler onConfirmDelete) {
    if (_dialog != null) _dialog.hide(); // don't allow layered dialogs
    _dialog = new DialogBox();
    _dialog.setGlassEnabled(true);
    _dialog.setModal(true);
    _dialog.setText(msg);
    // if user clicks Delete, execute the click handler
    Button deleteButton = new Button("Delete");
    deleteButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (onConfirmDelete != null) onConfirmDelete.onClick(event);
        _dialog.hide();
      }
    });
    // if user clicks handle, dismiss dialog without doing anything
    Button cancelButton = new Button("Cancel");
    cancelButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        _dialog.hide();
      }
    });
    FlowPanel panel = new FlowPanel(); 
    panel.add(deleteButton);
    panel.add(cancelButton);
    _dialog.add(panel);
    _dialog.center(); // show it
  }
}
