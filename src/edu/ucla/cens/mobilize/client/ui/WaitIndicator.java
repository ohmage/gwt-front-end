package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class WaitIndicator {
  private static PopupPanel panel;
  private static Image loadingImage;
  
  public static void show() {
    panel = new PopupPanel();
    loadingImage = new Image();
    loadingImage.setUrl("images/loading.gif");
    panel.setGlassEnabled(true);
    panel.setModal(true);
    panel.add(loadingImage);
    panel.center();
  }
  
  public static void hide() {
    if (panel != null) panel.hide();
    loadingImage = null;
    panel = null;
  }
}
