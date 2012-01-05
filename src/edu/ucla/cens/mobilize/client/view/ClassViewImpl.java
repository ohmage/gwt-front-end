package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserShortInfo;
import edu.ucla.cens.mobilize.client.ui.ClassDetail;
import edu.ucla.cens.mobilize.client.ui.ClassList;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;


public class ClassViewImpl extends Composite implements ClassView {

  private static ClassViewUiBinder uiBinder = GWT
  .create(ClassViewUiBinder.class);
  
  @UiTemplate("ClassView.ui.xml")
  interface ClassViewUiBinder extends UiBinder<Widget, ClassViewImpl> {
  }
  
  @UiField ClassList classList;
  @UiField ClassDetail classDetail;

  @UiField HTMLPanel msgBox;
  @UiField InlineLabel msgLabel;
  @UiField Anchor closeMsg;
  
  ClassView.Presenter presenter;
  
  public ClassViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    hideMsg();     
    setList(null);
    bind();
  }
  
  private void bind() {
  }
  
  private void hideAllWidgets() {
    classList.setVisible(false);
    classDetail.setVisible(false);
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }


  @Override
  public void showListSubview() {
    hideAllWidgets();
    classList.setVisible(true);
  }

  @Override
  public void showDetailSubview() {
    hideAllWidgets();
    this.classDetail.setVisible(true);
  }

  @Override
  public void setList(Map<String, String> classIdToNameMap) {
    this.classList.setClasses(classIdToNameMap);
  }

  @Override
  public void setDetail(ClassInfo classDetail) {
    this.classDetail.setClassDetail(classDetail);
  }
  
  @Override
  public void showError(String msg) {
    ErrorDialog.show(msg);
  }
  
  @Override
  public void showError(String msg, String detail) {
    ErrorDialog.show(msg, detail);
  }
  
  @Override
  public void showMsg(String msg) {
    msgLabel.setText(msg);
    msgBox.setVisible(true);
  }

  @Override
  public void hideMsg() {
    msgLabel.setText("");
    msgBox.setVisible(false);
  }

  @Override
  public void setDetailClassMemberDetails(List<UserShortInfo> members, Map<String, RoleClass> usernameToRoleMap) {
    this.classDetail.showClassMemberDetails(members, usernameToRoleMap);
  }
  
  @Override
  public void setDetailClassMemberUsernames(List<String> usernames) {
    this.classDetail.showClassMemberUsernames(usernames);
  }

  @Override
  public void clearClassMembers() {
    this.classDetail.clearClassMembers();
  }


}
