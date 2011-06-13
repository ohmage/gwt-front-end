package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;

public class ErrorDialog {
  private static DialogBox _dialog;
  
  public static void show(String msg) {
    _dialog = new DialogBox();
    _dialog.setGlassEnabled(true);
    _dialog.setText(msg);
    Button dismissButton = new Button("OK");
    dismissButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        _dialog.hide(); 
      }
    });
    _dialog.add(dismissButton);
    _dialog.center();
  }
  
}
