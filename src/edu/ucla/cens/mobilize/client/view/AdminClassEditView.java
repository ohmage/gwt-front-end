package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.ui.AdminClassAddUserPopup;
import edu.ucla.cens.mobilize.client.ui.AdminMenu;

public class AdminClassEditView extends Composite {

  private static AdminClassEditViewUiBinder uiBinder = GWT
      .create(AdminClassEditViewUiBinder.class);

  interface AdminClassEditViewStyles extends CssResource {
    String memberListCheckBoxCell();
    String memberListUsernameCell();
    String memberListRoleCell();
    String memberPrivilegedRow();
    String memberPrivilegedRoleCell();
    String memberRemoved();
  }
  
  private static class Columns {
    static final int CHECKBOX = 0;
    static final int USERNAME = 1;
    static final int ROLE = 2;
    static final int columnCount = 4;
  }

  @UiField AdminClassEditViewStyles style;

  @UiField AdminMenu adminMenu;
  @UiField Anchor backLink;
  @UiField Label sectionHeaderTitle;
  @UiField HTMLPanel centerContainer;
  @UiField InlineLabel classUrnInvalidMsg;
  @UiField TextBox classUrnTextBox;
  @UiField InlineLabel classUrnExample;
  @UiField Label classUrnLabel;
  @UiField InlineLabel classNameInvalidMsg;
  @UiField TextBox classNameTextBox;
  @UiField InlineLabel classNameExample;
  @UiField InlineLabel descriptionInvalidMsg;
  @UiField TextArea descriptionTextArea;
  @UiField HTMLPanel memberPanel;
  @UiField Grid memberListHeaderRow;
  @UiField Button membersPrivilegedButton;
  @UiField Button membersRestrictedButton;
  @UiField Button membersRemoveButton;
  @UiField CheckBox selectAllMembersCheckBox;
  @UiField HTMLPanel memberListGridContainer;
  @UiField Grid memberListGrid;
  @UiField Button addMembersButton;
  @UiField InlineLabel memberCount;
  @UiField InlineLabel membersNotSavedWarning;
  @UiField Button saveButton;
  @UiField Button cancelButton;
  @UiField Button deleteClassButton;

  private AdminClassAddUserPopup addMembersWidget;
  private DialogBox addMembersDialog;
  private boolean isEdit = false;
  
  interface AdminClassEditViewUiBinder extends
      UiBinder<Widget, AdminClassEditView> {
  }

  public AdminClassEditView() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
    this.adminMenu.selectManageClasses();
  }
  
  private void initComponents() {
    this.memberListHeaderRow.getCellFormatter().addStyleName(0, Columns.CHECKBOX, style.memberListCheckBoxCell());
    this.memberListHeaderRow.getCellFormatter().addStyleName(0, Columns.USERNAME, style.memberListUsernameCell());
    this.memberListHeaderRow.getCellFormatter().addStyleName(0, Columns.ROLE, style.memberListRoleCell());
    
    this.addMembersWidget = new AdminClassAddUserPopup();
    // create dialog even though it's not needed yet so presenter can add event handlers when view is set
    this.addMembersDialog = new DialogBox(false, true); // autohide=false, modal=true
    this.addMembersDialog.add(this.addMembersWidget);
    this.addMembersDialog.setText("Select users to add to class");
    
    // update size of member list container when window size changes
    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        updateMemberListHeight();
      }
    });
  }

  private void showMembersNotSavedWarning() {
    this.membersNotSavedWarning.setVisible(true);
  }
  
  public void selectAllMembers() {
    for (int row = 0; row < getMemberCount(); row++) {
      CheckBox cb = (CheckBox)this.memberListGrid.getWidget(row, Columns.CHECKBOX);
      cb.setValue(true);
    }
  }
  
  public void unselectAllMembers() {
    for (int row = 0; row < getMemberCount(); row++) {
      CheckBox cb = (CheckBox)this.memberListGrid.getWidget(row, Columns.CHECKBOX);
      cb.setValue(false);
    }
  }
  
  private boolean memberIsSelected(int row) {
    CheckBox cb = (CheckBox)this.memberListGrid.getWidget(row, Columns.CHECKBOX);
    return cb.getValue();
  }
  
  public void markSelectedMembersPrivileged() {
    this.showMembersNotSavedWarning();
    int memberCount = this.memberListGrid.getRowCount();
    for (int row = 0; row < memberCount; row++) {
      if (memberIsSelected(row)) {
        markMemberPrivileged(row);
      }
    }
  }
  
  public void markSelectedMembersRestricted() {
    this.showMembersNotSavedWarning();
    int memberCount = this.memberListGrid.getRowCount();
    for (int row = 0; row < memberCount; row++) {
      if (memberIsSelected(row)) {
        markMemberRestricted(row);
      }
    }
  }
  
  public void markSelectedMembersRemoved() {
    this.showMembersNotSavedWarning();
    int memberCount = this.memberListGrid.getRowCount();
    for (int row = 0; row < memberCount; row++) {
      if (memberIsSelected(row)) {
        markMemberRemoved(row);
      }
    }
  }

  private void clearMemberRoleStyle(int row) {
    this.memberListGrid.getRowFormatter().removeStyleName(row, style.memberPrivilegedRow());
    this.memberListGrid.getCellFormatter().removeStyleName(row, Columns.ROLE, style.memberPrivilegedRoleCell());
    this.memberListGrid.getCellFormatter().removeStyleName(row, Columns.ROLE, style.memberRemoved());
    this.memberListGrid.getCellFormatter().removeStyleName(row, Columns.USERNAME, style.memberRemoved());
  }
  
  public void markMemberPrivileged(int row) {
    clearMemberRoleStyle(row);
    this.memberListGrid.getRowFormatter().addStyleName(row, style.memberPrivilegedRow());
    this.memberListGrid.getCellFormatter().addStyleName(row, Columns.ROLE, style.memberPrivilegedRoleCell());
    this.memberListGrid.setText(row, Columns.ROLE, RoleClass.PRIVILEGED.toUserFriendlyString());
  }
  
  public void markMemberRestricted(int row) {
    clearMemberRoleStyle(row);
    this.memberListGrid.setText(row, Columns.ROLE, RoleClass.RESTRICTED.toUserFriendlyString());
  }
  
  public void markMemberRemoved(int row) {
    clearMemberRoleStyle(row);
    this.memberListGrid.getCellFormatter().addStyleName(row, Columns.ROLE, style.memberRemoved());
    this.memberListGrid.getCellFormatter().addStyleName(row, Columns.USERNAME, style.memberRemoved());
    this.memberListGrid.setText(row, Columns.ROLE, RoleClass.REMOVED.toUserFriendlyString());
  }
  
  private void updateMemberListHeight() {
    int centerHeight = centerContainer.getElement().getClientHeight();
    int memberListContainerHeight = Math.max(centerHeight - 215, 50);
    this.memberListGridContainer.setHeight(memberListContainerHeight + "px");
  }
  
  
  public void setClassUrn(String classUrn) {
    this.classUrnLabel.setText(classUrn);
    this.classUrnTextBox.setText(classUrn);
  }
  
  public String getClassUrn() {
    return this.classUrnTextBox.getText();
  }

  public void markClassUrnInvalid(String msg) {
    this.classUrnInvalidMsg.setVisible(true);
    this.classUrnInvalidMsg.setText(msg);
  }
  
  public void setClassName(String className) {
    this.classNameTextBox.setText(className);
    this.sectionHeaderTitle.setText("Editing " + className);
  }
  
  public String getClassName() {
    return this.classNameTextBox.getText();
  }
  
  public void markClassNameInvalid(String msg) {
    this.classNameInvalidMsg.setVisible(true);
    this.classNameInvalidMsg.setText(msg);
  }

  public void setDescription(String description) {
    this.descriptionTextArea.setText(description);
  }

  public String getDescription() {
    return this.descriptionTextArea.getText();
  }
  
  public void markDescriptionInvalid(String msg) {
    this.descriptionInvalidMsg.setVisible(true);
    this.descriptionInvalidMsg.setText(msg);
  }
  
  private RoleClass getMemberRole(int row) {
    RoleClass role = null;
    if (row < this.memberListGrid.getRowCount()) {
      role = RoleClass.fromUserFriendlyString(this.memberListGrid.getText(row, Columns.ROLE));
    }
    return role;
  }
  
  public void setMemberList(Map<String, RoleClass> usernameToClassRoleMap) {
    clearMemberList();
    if (usernameToClassRoleMap == null) return;
    int memberCount = usernameToClassRoleMap.size();
    this.memberListGrid.resize(memberCount, Columns.columnCount);
    List<String> usernames = new ArrayList<String>(usernameToClassRoleMap.keySet());
    Collections.sort(usernames);
    for (int row = 0; row < memberCount; row++) {
      String username = usernames.get(row);
      RoleClass role = usernameToClassRoleMap.get(username);
      CheckBox cb = new CheckBox();
      cb.setFormValue(username);
      this.memberListGrid.setWidget(row, Columns.CHECKBOX, cb);
      this.memberListGrid.setText(row, Columns.USERNAME, username);
      
      this.memberListGrid.getCellFormatter().setStyleName(row, Columns.CHECKBOX, style.memberListCheckBoxCell());
      this.memberListGrid.getCellFormatter().setStyleName(row, Columns.USERNAME, style.memberListUsernameCell());
      this.memberListGrid.getCellFormatter().setStyleName(row, Columns.ROLE, style.memberListRoleCell());
      
      if (role.equals(RoleClass.PRIVILEGED)) {
        markMemberPrivileged(row);
      } else if (role.equals(RoleClass.RESTRICTED)) {
        markMemberRestricted(row);
      } else if (role.equals(RoleClass.REMOVED)) {
        markMemberRemoved(row);
      }
    }
    this.memberCount.setText("(" + Integer.toString(memberCount) + ")" );
    updateMemberListHeight();
  }
  
  /**
   * @return Map of usernames to class roles. Note that users marked to be removed
   * are not included in this list.
   */
  public Map<String, RoleClass> getMemberList() {
    Map<String, RoleClass> usernameToRoleMap = new HashMap<String, RoleClass>();
    for (int row = 0; row < this.memberListGrid.getRowCount(); row++) {
      String username = this.memberListGrid.getText(row, Columns.USERNAME);
      RoleClass role = getMemberRole(row);
      if (!role.equals(RoleClass.REMOVED)) {
        usernameToRoleMap.put(username, role);
      }
    }
    return usernameToRoleMap;
  }
  
  public List<String> getRestrictedMembers() {
    return getMembers(RoleClass.RESTRICTED);
  }
  
  public List<String> getPrivilegedMembers() {
    return getMembers(RoleClass.PRIVILEGED);
  }

  private List<String> getMembers(RoleClass roleToMatch) {
    List<String> usernames = new ArrayList<String>();
    for (int row = 0; row < this.memberListGrid.getRowCount(); row++) {
      String username = this.memberListGrid.getText(row, Columns.USERNAME);
      RoleClass role = getMemberRole(row);
      if (role.equals(roleToMatch)) {
        usernames.add(username);
      }
    }
    return usernames;
  }

  public String getMemberUsernameAt(int rowIndex) {
    return this.memberListGrid.getText(rowIndex, Columns.USERNAME);
  }
  
  public void clearMemberList() {
    this.selectAllMembersCheckBox.setValue(false);
    this.memberListGrid.resize(0, 0);
    this.memberCount.setText("0");
  }
  
  public void clearValidationErrors() {
    this.classUrnInvalidMsg.setVisible(false);
    this.classNameInvalidMsg.setVisible(false);
    this.descriptionInvalidMsg.setVisible(false);
    this.classUrnInvalidMsg.setText("");
    this.classNameInvalidMsg.setText("");
    this.descriptionInvalidMsg.setText("");
  }
  
  public void resetForm() {
    clearValidationErrors();
    this.classUrnLabel.setText("");
    this.classUrnTextBox.setText("");
    this.classNameTextBox.setText("");
    this.descriptionTextArea.setText("");
    clearMemberList();
    this.membersNotSavedWarning.setVisible(false);
  }
  
  public void showFieldsForCreate() {
    this.isEdit = false;
    this.adminMenu.selectCreateClasses();
    this.classUrnLabel.setVisible(false);
    this.classUrnTextBox.setVisible(true);
    this.classUrnExample.setVisible(true);
    this.classNameExample.setVisible(true);
    this.memberPanel.setVisible(false);
    this.sectionHeaderTitle.setText("Creating New Class");
    this.deleteClassButton.setVisible(false);
  }
  
  public void showFieldsForEdit() {
    this.isEdit = true;
    this.adminMenu.clearSelectedItem();
    this.classUrnLabel.setVisible(true);
    this.classUrnTextBox.setVisible(false);
    this.classUrnExample.setVisible(false);
    this.classNameExample.setVisible(false);
    this.memberPanel.setVisible(true);
    this.sectionHeaderTitle.setText("Edit Class");
    this.deleteClassButton.setVisible(true);
  }
  
  public boolean isEdit() {
    return this.isEdit;
  }
  
  public void showAddMembersPopup() {
    this.addMembersWidget.clearSearchString();
    this.addMembersDialog.center();
  }
  
  public void setAddMembersPopupUserList(List<String> usernames) {
    this.addMembersWidget.setUserList(usernames);
  }
  
  public void showAddMembersPopupWaitIndicator() {
    this.addMembersWidget.showWaitIndicator();
  }
  
  public void hideAddMembersPopupWaitIndicator() {
    this.addMembersWidget.hideWaitIndicator();
  }
  
  public void hideAddMembersPopup() {
    if (this.addMembersDialog != null) {
      this.addMembersDialog.hide();
    }
    this.showMembersNotSavedWarning();
  }
  
  public HasValueChangeHandlers<Boolean> getSelectAllMembersCheckBox() {
    return this.selectAllMembersCheckBox;
  }
  
  public HasClickHandlers getBackLink() {
    return this.backLink;
  }
  
  public HasClickHandlers getMembersList() {
    return this.memberListGrid;
  }
  
  public HasClickHandlers getAddMembersButton() {
    return this.addMembersButton;
  }
  
  public HasClickHandlers getAddMembersPopupUserSearchButton() {
    return this.addMembersWidget.getUserSearchButton();
  }
  
  public HasClickHandlers getAddMembersPopupAddSelectedUsersButton() {
    return this.addMembersWidget.getAddSelectedUsersButton();
  }
  
  public HasClickHandlers getAddMembersPopupCancelButton() {
    return this.addMembersWidget.getCancelButton();
  }
  
  public HasClickHandlers getMembersPrivilegedButton() {
    return this.membersPrivilegedButton;
  }

  public HasClickHandlers getMembersRestrictedButton() {
    return this.membersRestrictedButton;
  }
  
  public HasClickHandlers getMembersRemoveButton() {
    return this.membersRemoveButton;
  }
  
  public HasClickHandlers getSaveButton() {
    return this.saveButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
  
  public HasClickHandlers getDeleteClassButton() {
    return this.deleteClassButton;
  }
  
  public String getAddMembersPopupSearchString() {
    return this.addMembersWidget.getSearchString();
  }
  
  /**
   * @return Map of usernames to class roles
   */
  public Map<String, RoleClass> getAddMembersPopupSelectedUsersAndRoles() {
    return this.addMembersWidget.getSelectedUsernamesAndRoles();
  }  

  /**
   * @return Member list from the main form (not from the popup)
   */
  public Map<String, RoleClass> getMembersAndRoles() {
    Map<String, RoleClass> usernameToRoleMap = new HashMap<String, RoleClass>();
    for (int row = 0; row < getMemberCount(); row++) {
      String username = this.memberListGrid.getText(row, Columns.USERNAME);
      RoleClass role = RoleClass.fromUserFriendlyString(this.memberListGrid.getText(row, Columns.ROLE));
      usernameToRoleMap.put(username, role);
    }
    return usernameToRoleMap;
  }
  
  private int getMemberCount() {
    return this.memberListGrid.getRowCount();
  }

  public void clearAddMembersPopup() {
    this.addMembersWidget.clearSearchString();
    this.addMembersWidget.setUserList(null);
  }

  
}
