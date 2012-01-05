package edu.ucla.cens.mobilize.client.ui;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;

public interface ResponseDisplayWidget {
  String getResponseKey();
  boolean isSelected();
  void setSelected(boolean isSelected);
  void setPrivacy(Privacy privacy);
  void setResponse(SurveyResponse response);
  void expand();
  void collapse();
}
