package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserShortInfo;


public interface ClassView extends IsWidget {

  // presenter management
  public interface Presenter {
    void setView(ClassView view);
  }
  void setPresenter(Presenter presenter);
  
  void showListSubview();
  void showDetailSubview();
  void showEditSubview();
  void setList(List<ClassInfo> classes);
  void setDetail(ClassInfo classDetail);
  // members details should only be visible to privileged users
  void setDetailClassMembers(List<UserShortInfo> members, Map<String, RoleClass> usernameToRoleMap); 
  void clearClassMembers();
  void setEdit(ClassInfo classDetail);
  void showEditFormAddMembersDialog(List<String> userLoginsToChooseFrom);
  
  // show messages to user
  void showError(String msg);
  void showMsg(String msg);
  void hideMsg();
  
  HasClickHandlers getEditFormSubmitButton();
  HasClickHandlers getEditFormCancelButton();
  HasClickHandlers getEditFormAddMembersButton();
  void clearEditForm();
  String getClassId();
  String getDescription();
  List<String> getMembers();
  
}
