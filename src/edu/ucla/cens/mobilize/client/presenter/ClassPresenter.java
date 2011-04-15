package edu.ucla.cens.mobilize.client.presenter;

import java.util.List;
import java.util.Map;

import edu.ucla.cens.mobilize.client.view.ClassView;

public class ClassPresenter implements ClassView.Presenter, Presenter {
  private ClassView view;

  public void setView(ClassView classView) {
    this.view = classView;
  }
  
  @Override
  public void go(Map<String, List<String>> params) {
  }


}
