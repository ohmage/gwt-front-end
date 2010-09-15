package edu.ucla.cens.AndWellnessVisualizations.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface FrontendResources extends ClientBundle {
  public static final FrontendResources INSTANCE = GWT.create(FrontendResources.class);

  @Source("calendarvisualizationview.css")
  @CssResource.NotStrict
  public CssResource calendarVisualizationViewCss();
}