package edu.ucla.cens.mobilize.client.ui;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.model.ClassInfo;

public class ClassEditForm extends Composite {

  private static ClassEditFormUiBinder uiBinder = GWT
      .create(ClassEditFormUiBinder.class);

  interface ClassEditFormUiBinder extends UiBinder<Widget, ClassEditForm> {
  }

  @UiField Label header;
  @UiField InlineLabel className;
  @UiField InlineLabel classUrn;
  @UiField TextArea descriptionTextArea;
  @UiField FlexTable privilegedMembersFlexTable;
  @UiField Button addPrivilegedMembersButton;
  @UiField FlexTable membersFlexTable;
  @UiField Button addMembersButton;
  @UiField Button cancelButton;
  @UiField Button saveButton;
  
  private final static int USER_LOGIN_COL = 0;
  private final static int USER_DELETE_COL = 1;
  
  public ClassEditForm() {
    initWidget(uiBinder.createAndBindUi(this));
    this.privilegedMembersFlexTable.setCellSpacing(0);
    this.membersFlexTable.setCellSpacing(0);
  }
  
  public void setClassDetail(ClassInfo classDetail) {
    this.header.setText("Editing " + classDetail.getClassName());
    this.className.setText(classDetail.getClassName());
    this.classUrn.setText(classDetail.getClassId());
    this.descriptionTextArea.setText(classDetail.getDescription());
    privilegedMembersFlexTable.removeAllRows();
    for (String privilegedMemberLogin : classDetail.getPrivilegedMemberLogins()) {
      addUserToFlexTable(privilegedMembersFlexTable, privilegedMemberLogin);
    }
    membersFlexTable.removeAllRows();
    for (String memberLogin : classDetail.getMemberLogins()) {
      addUserToFlexTable(membersFlexTable, memberLogin);
    }
  }
  
  public void clearForm() {
    this.header.setText("");
    this.className.setText("");
    this.classUrn.setText("");
    this.descriptionTextArea.setText("");
    this.membersFlexTable.removeAllRows();
    this.privilegedMembersFlexTable.removeAllRows();
  }
  
  private void addUserToFlexTable(final FlexTable flexTable, String userLogin) {
    flexTable.setVisible(true);
    
    // check for duplicates
    boolean isAlreadyInTable = false;
    int firstEmptyRowIndex = flexTable.getRowCount();
    for (int i = 0; i < firstEmptyRowIndex; i++) {
      // gotcha: this only checks against name, not unique id
      if (flexTable.getText(i, 0).equals(userLogin)) {
        isAlreadyInTable = true;
        break;
      }
    }
    
    // if user is not already in table, add new row with 3 columns 
    // 0 = user login, 1 = "x" button that deletes row when clicked
    if (!isAlreadyInTable) {
      final int thisRow = firstEmptyRowIndex;
      //flexTable.setText(thisRow, USER_NAME_COL, userName); // we only have id for now
      flexTable.setText(thisRow, USER_LOGIN_COL, userLogin);
      Button deleteButton = new Button("X");
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          flexTable.removeRow(thisRow);
          flexTable.setVisible(flexTable.getRowCount() > 0);
        }
      });
      flexTable.setWidget(thisRow, USER_DELETE_COL, deleteButton);
    }
  }
  
  public HasClickHandlers getAddPrivilegedMembersButton() {
    return this.addPrivilegedMembersButton;
  }
  
  public void showPrivilegedMemberChoices(List<String> userLogins) {
    if (userLogins == null) return;
    final MultiSelectDialog userChooserDialog = new MultiSelectDialog();
    userChooserDialog.setCaption("Select users to add as privileged members.");
    userChooserDialog.setItems(userLogins);    
    userChooserDialog.setSubmitHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        List<String> selected = userChooserDialog.getSelectedItems();
        for (String userLogin : selected) {
          addUserToFlexTable(privilegedMembersFlexTable, userLogin);
        }
        userChooserDialog.hide();
      }
    });
    userChooserDialog.show();
  }
  
  public HasClickHandlers getAddMembersButton() {
    return this.addMembersButton;
  }
  
  public void showMemberChoices(List<String> userLogins) {
    if (userLogins == null) return;
    final MultiSelectDialog userChooserDialog = new MultiSelectDialog();
    userChooserDialog.setCaption("Select users to add as restricted members.");
    userChooserDialog.setItems(userLogins);    
    userChooserDialog.setSubmitHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        List<String> selected = userChooserDialog.getSelectedItems();
        for (String userLogin : selected) {
          addUserToFlexTable(membersFlexTable, userLogin);
        }
        userChooserDialog.hide();
      }
    });
    userChooserDialog.show();
  }  
  public HasClickHandlers getSubmitButton() {
    return this.saveButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
  
  public String getClassId() {
    return this.classUrn.getText();
  }
  
  public String getDescription() {
    return this.descriptionTextArea.getText();
  }
  
  public List<String> getRestrictedMembers() {
    List<String> members = new ArrayList<String>();
    for (int i = 0; i < this.membersFlexTable.getRowCount(); i++) {
      members.add(this.membersFlexTable.getText(i, USER_LOGIN_COL));
    }
    return members;
  }
  
  public List<String> getPrivilegedMembers() {
    List<String> privilegedMembers = new ArrayList<String>();
    for (int i = 0; i < this.privilegedMembersFlexTable.getRowCount(); i++) {
      privilegedMembers.add(this.privilegedMembersFlexTable.getText(i, USER_LOGIN_COL));
    }
    return privilegedMembers;
  }  
  
  

}
