package org.ohmage.mobilize.client.ui;

import org.ohmage.mobilize.client.common.Privacy;
import org.ohmage.mobilize.client.model.SurveyResponse;

public interface ResponseDisplayWidget {
  String getResponseKey();
  boolean isSelected();
  void setSelected(boolean isSelected);
  void setPrivacy(Privacy privacy);
  void setResponse(SurveyResponse response);
  void expand();
  void collapse();
}
