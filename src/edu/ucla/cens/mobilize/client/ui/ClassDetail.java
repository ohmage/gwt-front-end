package edu.ucla.cens.mobilize.client.ui;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.model.UserShortInfo;

public class ClassDetail extends Composite {

  private static ClassDetailUiBinder uiBinder = GWT
      .create(ClassDetailUiBinder.class);

  interface ClassDetailStyle extends CssResource {
    String membersTableHeader();
    String rolePrivileged();
    String rolePrivilegedRow();
  }
  
  interface ClassDetailUiBinder extends UiBinder<Widget, ClassDetail> {
  }

  @UiField ClassDetailStyle style;
  @UiField Anchor backLink;
  @UiField InlineLabel className;
  @UiField InlineLabel classUrn;
  @UiField InlineLabel classDescription;
  @UiField Grid membersTable;
  
  // convenience class for column indices
  private class Column {
    private static final int USERNAME = 0;
    private static final int FIRST_NAME = 1;
    private static final int LAST_NAME = 2;
    private static final int ORGANIZATION = 3;
    private static final int EMAIL = 4;
    
    private static final int count = 5; // sum of above
  }
  
  public ClassDetail() {
    initWidget(uiBinder.createAndBindUi(this));
    bind();
  }
  
  private void bind() {
    backLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
  }
  
  public void setClassDetail(ClassInfo classDetail) {
    this.className.setText(classDetail.getClassName());
    this.classUrn.setText(classDetail.getClassId());
    this.classDescription.setText(classDetail.getDescription());    
  }
  
  public void clearClassMembers() {
    this.membersTable.resizeRows(0);
  }

  // use this OR setClassMemberDetails
  public void setClassMemberNames(List<String> usernames) {
    throw new RuntimeException("setClassMemberNames unimplemented");
  }

  // use this OR setClassMemberNamesAndRoles
  public void setClassMemberDetails(List<UserShortInfo> members, Map<String, RoleClass> usernameToRoleMap) {
    initClassMemberDetailTable();
    this.membersTable.resizeRows(members.size() + 1); // + 1 for header
    List<UserShortInfo> sortedMembers = new ArrayList<UserShortInfo>(members);
    Collections.sort(sortedMembers, new UsernameComparator());
    int row = 1; // 0th row is header
    for (UserShortInfo member : members) {
      String username = member.getUsername();
      RoleClass role = usernameToRoleMap.containsKey(username) ? usernameToRoleMap.get(username) : RoleClass.UNRECOGNIZED;
      addClassMember(row++, member, role);
    }
  }

  
  private void initClassMemberDetailTable() {
    membersTable.resize(1, Column.count);
    membersTable.getRowFormatter().setStyleName(0, style.membersTableHeader());
    membersTable.setText(0, Column.USERNAME, "Username");
    membersTable.setText(0, Column.FIRST_NAME, "First name");
    membersTable.setText(0, Column.LAST_NAME, "Last name");
    membersTable.setText(0, Column.ORGANIZATION, "Organization");
    membersTable.setText(0, Column.EMAIL, "Email");
  }
  
  private void addClassMember(int row, UserShortInfo userInfo, RoleClass role) {
    this.membersTable.setText(row, Column.USERNAME, userInfo.getUsername());
    this.membersTable.setText(row, Column.FIRST_NAME, userInfo.getFirstName());
    this.membersTable.setText(row, Column.LAST_NAME, userInfo.getLastName());
    this.membersTable.setText(row, Column.ORGANIZATION, userInfo.getOrganization());
    this.membersTable.setText(row, Column.EMAIL, userInfo.getEmail());
    if (RoleClass.PRIVILEGED.equals(role)) {
      this.membersTable.getCellFormatter().setStyleName(row, Column.USERNAME, style.rolePrivileged());
      this.membersTable.getRowFormatter().setStyleName(row, style.rolePrivilegedRow());
    }
  }
  
  protected class UsernameComparator implements Comparator<UserShortInfo> {
    @Override
    public int compare(UserShortInfo o1, UserShortInfo o2) {
      return o1.getUsername().compareTo(o2.getUsername());
    }
  }
}
