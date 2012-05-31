package org.ohmage.mobilize.client.view;

import com.google.gwt.user.client.ui.IsWidget;

public interface AdminView extends IsWidget {
  public interface Presenter {
    void setView(AdminView view);
  }
    
}
