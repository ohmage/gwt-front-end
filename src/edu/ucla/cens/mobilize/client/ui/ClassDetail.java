package edu.ucla.cens.mobilize.client.ui;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import com.google.gwt.user.client.ui.HTMLPanel;
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
    String missingValue();
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
  @UiField HTMLPanel legend;
  @UiField Grid membersTable;
  
  // convenience class for column indices of member detail table
  private class Column {
    private static final int USERNAME = 0;
    private static final int PERSONAL_ID = 1; // e.g., user's school id
    private static final int FIRST_NAME = 2;
    private static final int LAST_NAME = 3;
    private static final int ORGANIZATION = 4;
    
    private static final int count = 6; // number of columns (count of above)
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
  
  /**
   * Displays all info about a class except the list of members.
   * @param classDetail
   */
  public void setClassDetail(ClassInfo classDetail) {
    this.className.setText(classDetail.getClassName());
    this.classUrn.setText(classDetail.getClassId());
    this.classDescription.setText(classDetail.getDescription());
  }
  
  public void clearClassMembers() {
    this.membersTable.resizeRows(0);
  }
  
  /**
   * Displays columns of usernames. Doesn't show details or highlight privileged members.
   * Use this method when rendering class detail page for non-privileged members
   * (who should not be able to see personal details for other members.) 
   * Use setClassMemberDetails instead if you want the extra info.
   */
  public void showClassMemberUsernames(final List<String> usernames) {
    clearClassMembers();
    legend.setVisible(false); // legend not needed since privileged members are not highlighted
    int numClassMembers = usernames.size();
    int numColumns = 5;
    int numRows = numClassMembers / numColumns + 1; // integer division, +1 for remainder
    membersTable.resize(numRows, numColumns);
    List<String> sortedUsernames = new ArrayList<String>(usernames);
    Collections.sort(sortedUsernames);
    int row = 0; 
    int col = 0;
    for (String username : sortedUsernames) {
      membersTable.setText(row, col, username);
      if (++row == numRows) { row = 0; col++; }
    }    
  }
  
  /**
   * Displays a table of usernames and personal details for members of a class,
   * with an asterisk next to the name of privileged users.
   * If you only want to show usernames and not personal info (e.g., if the logged
   * in user is not privileged in this class and should not be able to see other
   * members' details) use setClassMemberUsernames instead.
   */
  public void showClassMemberDetails(List<UserShortInfo> members, Map<String, RoleClass> usernameToRoleMap) {
    // create a hash of usernames to infos so you can see which infos are missing
    HashMap<String, UserShortInfo> usernameToInfoMap = new HashMap<String, UserShortInfo>();
    for (UserShortInfo member : members) {
      usernameToInfoMap.put(member.getUsername(), member);
    }
    
    // make sure every user in the class has an info, or create an empty one if they don't
    for (String username : usernameToRoleMap.keySet()) {
      if (!usernameToInfoMap.containsKey(username)) {
        UserShortInfo emptyInfo = new UserShortInfo();
        emptyInfo.setUsername(username);
        usernameToInfoMap.put(username, emptyInfo);
      }
    }
    
    // sort the list of users by username
    List<UserShortInfo> sortedMembers = new ArrayList<UserShortInfo>(usernameToInfoMap.values());
    Collections.sort(sortedMembers, new UsernameComparator());
    
    // copy list into table
    initClassMemberDetailTable();
    this.membersTable.resizeRows(sortedMembers.size() + 1); // + 1 for header
    int row = 1; // 0th row is header
    for (UserShortInfo member : sortedMembers) {
      String username = member.getUsername();
      RoleClass role = usernameToRoleMap.containsKey(username) ? usernameToRoleMap.get(username) : RoleClass.UNRECOGNIZED;
      addClassMemberDetail(row++, member, role);
    }
  }

  
  private void initClassMemberDetailTable() {
    clearClassMembers();
    legend.setVisible(true); // legend identifies style used for privileged members
    membersTable.resize(1, Column.count);
    membersTable.getRowFormatter().setStyleName(0, style.membersTableHeader());
    membersTable.setText(0, Column.USERNAME, "Username");
    membersTable.setText(0, Column.PERSONAL_ID, "ID");
    membersTable.setText(0, Column.FIRST_NAME, "First name");
    membersTable.setText(0, Column.LAST_NAME, "Last name");
    membersTable.setText(0, Column.ORGANIZATION, "Organization");
  }
  
  // Sets default value (and style) if text is null or empty string. Use this
  // for displaying personal details that might be missing from the db
  private void setMemberDetailTableText(int row, int column, String text) {
    if (text == null || text.isEmpty()) {
      this.membersTable.setText(row, column, "---");
      this.membersTable.getCellFormatter().addStyleName(row, column, style.missingValue());
    } else {
      this.membersTable.setText(row, column, text);
      this.membersTable.getCellFormatter().removeStyleName(row, column, style.missingValue());
    }
  }
  
  private void addClassMemberDetail(int row, UserShortInfo userInfo, RoleClass role) {
    // display username with css style to indicate whether user is privileged
    this.membersTable.setText(row, Column.USERNAME, userInfo.getUsername());
    if (RoleClass.PRIVILEGED.equals(role)) {
      this.membersTable.getCellFormatter().setStyleName(row, Column.USERNAME, style.rolePrivileged());
      this.membersTable.getRowFormatter().setStyleName(row, style.rolePrivilegedRow());
    }
    
    // fill in personal info or default value if it's missing
    setMemberDetailTableText(row, Column.PERSONAL_ID, userInfo.getPersonalId());
    setMemberDetailTableText(row, Column.FIRST_NAME, userInfo.getFirstName());
    setMemberDetailTableText(row, Column.LAST_NAME, userInfo.getLastName());
    setMemberDetailTableText(row, Column.ORGANIZATION, userInfo.getOrganization());
  }

  protected class UsernameComparator implements Comparator<UserShortInfo> {
    @Override
    public int compare(UserShortInfo o1, UserShortInfo o2) {
      return o1.getUsername().compareTo(o2.getUsername());
    }
  }
  
}
