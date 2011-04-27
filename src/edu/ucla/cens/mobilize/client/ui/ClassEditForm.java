package edu.ucla.cens.mobilize.client.ui;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
  @UiField FlexTable supervisorsFlexTable;
  @UiField FlexTable membersFlexTable;
  @UiField Button cancelButton;
  @UiField Button saveButton;
  
  public ClassEditForm() {
    initWidget(uiBinder.createAndBindUi(this));
    this.supervisorsFlexTable.setCellSpacing(0);
    this.membersFlexTable.setCellSpacing(0);
  }
  
  public void setClassDetail(ClassInfo classDetail) {
    this.header.setText("Editing " + classDetail.getClassName());
    this.className.setText(classDetail.getClassName());
    this.classUrn.setText(classDetail.getClassId());
    this.descriptionTextArea.setText(classDetail.getDescription());
    for (String supervisorId : classDetail.getSupervisors().keySet()) {
      addUserToFlexTable(supervisorsFlexTable,
                         supervisorId,
                         classDetail.getSupervisors().get(supervisorId));
    }
    for (String memberId : classDetail.getMembers().keySet()) {
      addUserToFlexTable(membersFlexTable,
                         memberId,
                         classDetail.getMembers().get(memberId));
    }
  }

  
  private void addUserToFlexTable(final FlexTable flexTable, String userId, String userName) {
    flexTable.setVisible(true);
    
    // check for duplicates
    boolean isAlreadyInTable = false;
    int firstEmptyRowIndex = flexTable.getRowCount();
    for (int i = 0; i < firstEmptyRowIndex; i++) {
      // gotcha: this only checks against name, not unique id
      if (flexTable.getText(i, 1).equals(userId)) {
        isAlreadyInTable = true;
        break;
      }
    }
    
    // if user is not already in table, add new row with 3 columns 
    // 0 = user name, 1 = user login, 2 = "x" button that deletes row when clicked
    if (!isAlreadyInTable) {
      final int thisRow = firstEmptyRowIndex;
      flexTable.setText(thisRow, 0, userName);
      flexTable.setText(thisRow, 1, userId);
      Button deleteButton = new Button("X");
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          flexTable.removeRow(thisRow);
          flexTable.setVisible(flexTable.getRowCount() > 0);
        }
      });
      flexTable.setWidget(thisRow, 2, deleteButton);
    }
  }
  
}
