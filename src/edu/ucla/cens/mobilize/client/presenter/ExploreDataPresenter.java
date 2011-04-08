package edu.ucla.cens.mobilize.client.presenter;

import java.util.List;
import java.util.Map;

import edu.ucla.cens.mobilize.client.view.ExploreDataView;

public class ExploreDataPresenter implements ExploreDataView.Presenter, Presenter {
  
  ExploreDataView view;


  @Override
  public void go(Map<String, List<String>> params) {
    // TODO Auto-generated method stub
  }
  
  @Override
  public void setView(ExploreDataView view) {
    this.view = view;
    this.view.setPresenter(this);
  }
  
  @Override
  public void onCampaignFilterChanged() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onInstanceFilterChanged() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onSurveyFilterChanged() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onDateFilterChanged() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onVariableSelectionChanged() {
    // TODO Auto-generated method stub
    
  }




}
