package edu.ucla.cens.mobilize.client.ui;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;

public interface ResponseDisplayWidget {
  int getResponseKey();
  boolean isSelected();
  boolean setSelected(boolean isSelected);
  void setPrivacyState(Privacy privacy);
  void setResponse(SurveyResponse response);
}
