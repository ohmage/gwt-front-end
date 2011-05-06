package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.model.ClassInfo;


public interface ClassView extends IsWidget {

  // presenter management
  public interface Presenter {
    void setView(ClassView view);
  }
  void setPresenter(Presenter presenter);
  
  void showList(List<ClassInfo> classes);
  void showDetail(ClassInfo classDetail);
  void showEditForm(ClassInfo classDetail);
  
  // show messages to user
  void showError(String msg);
  void showMsg(String msg);
  void hideMsg();
  
  HasClickHandlers getEditFormSubmitButton();
  HasClickHandlers getEditFormCancelButton();
  void clearEditForm();
  String getClassId();
  String getDescription();
  List<String> getMembers();
  List<String> getPrivilegedMembers();
  
}
