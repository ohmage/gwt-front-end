package edu.ucla.cens.mobilize.client.dataaccess.formpanel;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class AwFormPanel {
  
  private static Logger _logger = Logger.getLogger(AwFormPanel.class.getName());
  
  public static void post(String requestUrl, Map<String, String> params, boolean openNewWindow) {
    _logger.fine("Posting form panel request to " + requestUrl + " with params: " + MapUtils.translateToParameters(params));
    final FormPanel formPanel = openNewWindow ? new FormPanel("_blank") : new FormPanel(); 
    formPanel.setAction(requestUrl);
    formPanel.setMethod(FormPanel.METHOD_POST);
    FlowPanel innerContainer = new FlowPanel();
    for (String paramName : params.keySet()) {
      Hidden field = new Hidden();
      field.setName(paramName);
      field.setValue(params.get(paramName));
      innerContainer.add(field);
    }
    formPanel.add(innerContainer);
    RootLayoutPanel.get().add(formPanel);
    formPanel.submit();
    formPanel.removeFromParent(); // doesn't wait for result
  }
}
