package edu.ucla.cens.mobilize.client.presenter;

import java.util.Map;

import edu.ucla.cens.mobilize.client.view.ExploreDataView;

public class ExploreDataPresenter implements Presenter {
  
  ExploreDataView view;

  @Override
  public void go(Map<String, String> params) {
    
  }
    
  public void setView(ExploreDataView view) {
    this.view = view;
  }
  




}
