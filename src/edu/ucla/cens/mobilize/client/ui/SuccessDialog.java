package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;

public class SuccessDialog {
  private static DialogBox _dialog;
  
  public static void show(String msg) {
    if (_dialog != null) _dialog.hide(); // don't allow layered dialogs
    _dialog = new DialogBox();
    _dialog.setGlassEnabled(false);
    _dialog.setText(msg);
    _dialog.setAutoHideEnabled(true);
    _dialog.setAnimationEnabled(true);
    _dialog.setStyleName("successMsg");
    _dialog.center();

    // close the dialog after a few secs
    Timer t = new Timer() {
      public void run() {
        _dialog.hide();
      }
    };

    t.schedule(1000);
  }
}
