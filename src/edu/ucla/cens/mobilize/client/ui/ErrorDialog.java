package edu.ucla.cens.mobilize.client.ui;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ErrorDialog {
  private static DialogBox _dialog;
  
  public static void show(String msg, String detail) {
    _dialog = new DialogBox();
    _dialog.setGlassEnabled(true);
    _dialog.setText(msg);
    
    VerticalPanel innerContainer = new VerticalPanel();
    innerContainer.setWidth("100%");
    
    // include details, if any
    if (detail != null && !detail.isEmpty()) {
      HTML detailHTML = new HTML();
      detailHTML.setHTML(detail);
      detailHTML.setStyleName("errorBox");
      innerContainer.add(detailHTML);
    }
    
    // add a button that closes the dialog
    Button dismissButton = new Button("OK");
    dismissButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        _dialog.hide(); 
      }
    });
    HTMLPanel dialogFooter = new HTMLPanel("<div id='buttonDiv' class='errorDialogButtonDiv'></div>");
    dialogFooter.add(dismissButton, "buttonDiv");
    dialogFooter.addStyleName("buttonDiv");
    innerContainer.add(dialogFooter);
    
    // add widgets to the dialog
    _dialog.add(innerContainer);
    
    // show it
    _dialog.center();
  }
  
  public static void show(String msg) {
    ErrorDialog.show(msg, null);
  }
  
  public static void showErrorsByCode(String msg, Map<String, String> errorCodeToDescriptionMap) {
    StringBuilder sb = new StringBuilder();
    sb.append("<ul class='errorList'>");
    for (String errorCode : errorCodeToDescriptionMap.keySet()) {
      sb.append("<li class='errorListItem'>");
      sb.append(errorCode).append(": ").append(errorCodeToDescriptionMap.get(errorCode));
      sb.append("</li>");
    }
    sb.append("</ul>");
    ErrorDialog.show(msg, sb.toString());
  }
  
  public static void showErrorList(String msg, List<String> errorMessages) {
    StringBuilder sb = new StringBuilder();
    sb.append("<ul class='errorList'>");
    for (String errorMessage : errorMessages) {
      sb.append("<li class='errorListItem'>");
      sb.append(errorMessage);
      sb.append("</li>");
    }
    sb.append("</ul>");
    ErrorDialog.show(msg, sb.toString());
  }
  
}
