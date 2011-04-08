package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

public interface ExploreDataView extends IsWidget {
  
  public interface Presenter {
    void setView(ExploreDataView view);
    void onCampaignFilterChanged();
    void onInstanceFilterChanged();
    void onSurveyFilterChanged();
    void onDateFilterChanged();
    void onVariableSelectionChanged();
  }
  
  public void setPresenter(Presenter presenter);
  /*
  public String getCampaign();
  public String getInstance();
  public String getSurvey();
  public Date getStartDate();
  public Date getEndDate();
  public String getSelectedVariable();

  public void setCampaign(String value);
  public void setInstance(String value);
  public void setSurvey(String value);
  public void setStartDate(Date value);
  public void setEndDate(Date value);
  public void setSelectedVariable(String variableName);
  */
  public void setVariableList(ArrayList<String> plottableVariables);
  public void setPlot(Image plot);
  
}
