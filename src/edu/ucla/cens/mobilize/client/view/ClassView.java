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
  void setList(Map<String, String> classIdToNameMap);
  void setDetail(ClassInfo classDetail);
  // members details should only be visible to privileged users
  void setDetailClassMemberDetails(List<UserShortInfo> members, Map<String, RoleClass> usernameToRoleMap);
  void setDetailClassMemberUsernames(List<String> usernames);
  void clearClassMembers();
  
  // show messages to user
  void showError(String msg);
  void showError(String msg, String detail);
  void showMsg(String msg);
  void hideMsg();
  
}
