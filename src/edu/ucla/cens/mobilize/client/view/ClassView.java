package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.user.client.ui.IsWidget;


public interface ClassView extends IsWidget {

  // presenter management
  public interface Presenter {
  }
  void setPresenter(Presenter presenter);
}
