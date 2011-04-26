package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.model.ClassInfo;


public interface ClassView extends IsWidget {

  // presenter management
  public interface Presenter {
    void setView(ClassView view);
    void onAddUsersClick();
    void onAddUsersSubmit();
    void onDeleteUserClick();
    void onDeleteUserConfirm();
    void onFilterChange();
  }
  void setPresenter(Presenter presenter);
  
  void showList(List<ClassInfo> classes);
  void showDetail(ClassInfo classDetail);
  //void showCreateForm(String authToken, String serverLocation);
  void showEditForm(ClassInfo classDetail);
  
  // show messages to user
  void showError(String msg);
  void showMsg(String msg);
  void hideMsg();
  
}
