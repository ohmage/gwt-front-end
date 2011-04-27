package edu.ucla.cens.mobilize.client.dataaccess.requestparams;

import edu.ucla.cens.mobilize.client.common.Privacy;

public class DataPointFilterParams extends RequestParams {
  public Privacy privacyState = Privacy.UNDEFINED;
  public String participantId = "";
  public String surveyId = "";
}
