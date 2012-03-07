package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.RoleClass;


public class AdminClassAddUserPopup extends Composite {

  private static AdminClassAddUserPopupUiBinder uiBinder = GWT
      .create(AdminClassAddUserPopupUiBinder.class);

  interface AdminClassAddUserPopupUiBinder extends
      UiBinder<Widget, AdminClassAddUserPopup> {
  }
  
  interface AdminClassAddUserPopupStyles extends CssResource {
    String selected();
    String waiting();
  }
  
  private static class Columns {
    static final int CHECKBOX = 0;
    static final int USERNAME = 1;
    static final int ROLE_PRIVILEGED = 2;
    static final int ROLE_RESTRICTED = 3;
    static final int columnCount = 4;
  }

  @UiField AdminClassAddUserPopupStyles style;
  @UiField HTMLPanel userListContainer;
  @UiField TextBox usernameTextBox;
  @UiField Button usernameSearchButton;
  @UiField Anchor selectAllLink;
  @UiField Anchor resetLink;
  @UiField Grid userListGrid;
  @UiField Button addSelectedUsersButton;
  @UiField Button cancelButton;

  private List<String> allUsernames = new ArrayList<String>();
  private FlowPanel waitIndicator;
  
  public AdminClassAddUserPopup() {
    initWidget(uiBinder.createAndBindUi(this));
    waitIndicator = new FlowPanel();
    waitIndicator.setStyleName(style.waiting());
    bind();
  }
  
  private void bind() {
    this.userListGrid.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Cell clickedCell = userListGrid.getCellForEvent(event);
        int clickedColumn = clickedCell.getCellIndex();
        int clickedRow = clickedCell.getRowIndex();
        CheckBox cb = (CheckBox)userListGrid.getWidget(clickedRow, Columns.CHECKBOX);
        switch (clickedColumn) {
          case Columns.CHECKBOX: // toggle selected state 
            if (cb.getValue()) selectRow(clickedRow);
            else unselectRow(clickedRow);
            break;
          case Columns.ROLE_PRIVILEGED: 
          case Columns.ROLE_RESTRICTED: // select if not already selected, otherwise no effect 
            if (cb.getValue() == false) selectRow(clickedRow);
            break;
          default:
            break;
        }        
      }
    });
    
    this.usernameSearchButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        filterUsernames();
      }
    });
    
    this.usernameTextBox.addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          filterUsernames();
        }
      }
    });
    
    this.selectAllLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        for (int row = 0; row < userListGrid.getRowCount(); row++) {
          selectRow(row);
        }
      }
    });
    
    this.resetLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        usernameTextBox.setText("");
        filterUsernames();
      }
    });
  }

  private void filterUsernames() {
    String searchString = usernameTextBox.getValue();
    if (searchString.isEmpty()) {
      updateUserListGrid(allUsernames);
    } else {
      List<String> matchingUsernames = new ArrayList<String>();
      for (String username : allUsernames) {
        if (username.contains(searchString)) {
          matchingUsernames.add(username);
        }
      }
      updateUserListGrid(matchingUsernames);
    }
  }

  // Check checkbox, make sure a default role is selected, and update row style.
  private void selectRow(int rowIndex) {
    CheckBox cb = (CheckBox)userListGrid.getWidget(rowIndex, Columns.CHECKBOX);
    RadioButton radioPrivileged = (RadioButton)userListGrid.getWidget(rowIndex, Columns.ROLE_PRIVILEGED);
    RadioButton radioRestricted = (RadioButton)userListGrid.getWidget(rowIndex, Columns.ROLE_RESTRICTED);
    cb.setValue(true);
    if (radioPrivileged.getValue() == false) radioRestricted.setValue(true);
    userListGrid.getRowFormatter().addStyleName(rowIndex, style.selected());
  }
  
  // Uncheck checkbox, unselect all rows, and update row style.
  private void unselectRow(int rowIndex) {
    CheckBox cb = (CheckBox)userListGrid.getWidget(rowIndex, Columns.CHECKBOX);
    RadioButton radioPrivileged = (RadioButton)userListGrid.getWidget(rowIndex, Columns.ROLE_PRIVILEGED);
    RadioButton radioRestricted = (RadioButton)userListGrid.getWidget(rowIndex, Columns.ROLE_RESTRICTED);
    cb.setValue(false);
    radioPrivileged.setValue(false);
    radioRestricted.setValue(false);
    userListGrid.getRowFormatter().removeStyleName(rowIndex, style.selected());
  }
  
  public void setUserList(List<String> usernames) {
    if (usernames == null) usernames = new ArrayList<String>(); // show empty list on null
    this.allUsernames = new ArrayList<String>(usernames);
    updateUserListGrid(usernames);
  }
  
  private void updateUserListGrid(List<String> usernames) {
    this.userListGrid.resize(usernames.size(), Columns.columnCount);
    for (int row = 0; row < usernames.size(); row++) {
      String username = usernames.get(row);
      this.userListGrid.setWidget(row, Columns.CHECKBOX, new CheckBox());
      this.userListGrid.setText(row, Columns.USERNAME, username);
      this.userListGrid.setWidget(row, Columns.ROLE_PRIVILEGED, new RadioButton("popup_role_" + username, "Privileged"));
      this.userListGrid.setWidget(row, Columns.ROLE_RESTRICTED, new RadioButton("popup_role_" + username, "Restricted"));
      unselectRow(row); // make sure there's no leftover highlighting from last time dialog was open
    }
  }
  
  public HasClickHandlers getUserSearchButton() {
    return this.usernameSearchButton;
  }
  
  public HasClickHandlers getAddSelectedUsersButton() {
    return this.addSelectedUsersButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
  
  public String getSearchString() {
    return this.usernameTextBox.getText();
  }
  
  public void setSearchString(String username) {
    this.usernameTextBox.setText(username);
  }
  
  public void clearSearchString() {
    this.usernameTextBox.setText("");
  }
  
  /**
   * Returns map of usernames to class roles
   * @return
   */
  public Map<String, RoleClass> getSelectedUsernamesAndRoles() {
    Map<String, RoleClass> usernameToRoleMap = new HashMap<String, RoleClass>();
    for (int row = 0; row < this.userListGrid.getRowCount(); row++) {
      CheckBox cb = (CheckBox)this.userListGrid.getWidget(row, Columns.CHECKBOX);
      if (cb.getValue()) { 
        String username = this.userListGrid.getText(row, Columns.USERNAME);
        RadioButton rb = (RadioButton)this.userListGrid.getWidget(row, Columns.ROLE_PRIVILEGED);
        RoleClass role = (rb.getValue()) ? RoleClass.PRIVILEGED : RoleClass.RESTRICTED; // default to restricted
        usernameToRoleMap.put(username, role);
      }
    }
    return usernameToRoleMap;
  }

  public void showWaitIndicator() {
    this.userListContainer.add(waitIndicator);
  }
  
  public void hideWaitIndicator() {
    this.userListContainer.remove(waitIndicator);
  }
}
