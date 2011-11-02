package edu.ucla.cens.mobilize.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.RoleClass;

public class AdminAddUsersToClass extends Composite {

  private static AdminAddUsersToClassUiBinder uiBinder = GWT
      .create(AdminAddUsersToClassUiBinder.class);

  interface AdminAddUsersToClassUiBinder extends
      UiBinder<Widget, AdminAddUsersToClass> {
  }
  
  @UiField ListBox classListBox;
  @UiField ListBox roleListBox;
  @UiField HTML usernames;
  @UiField Button addUsersButton;
  @UiField Button cancelButton;
  
  public AdminAddUsersToClass() {
    initWidget(uiBinder.createAndBindUi(this));
    this.roleListBox.addItem(RoleClass.RESTRICTED.toUserFriendlyString(), RoleClass.RESTRICTED.toServerString());
    this.roleListBox.addItem(RoleClass.PRIVILEGED.toUserFriendlyString(), RoleClass.PRIVILEGED.toServerString());
  }

  public String getClassUrn() {
    int selectedIndex = this.classListBox.getSelectedIndex();
    return selectedIndex > -1 ? this.classListBox.getValue(selectedIndex) : null;
  }
  
  public RoleClass getRole() {
    return RoleClass.fromServerString(this.roleListBox.getValue(this.roleListBox.getSelectedIndex()));
  }
  
  public void setClassList(List<String> classUrns) {
    this.classListBox.clear();
    for (String classUrn : classUrns) {
      this.classListBox.addItem(classUrn, classUrn);
    }
  }
  
  public void setUsernames(List<String> usernames) {
    this.usernames.setHTML("");
    StringBuilder sb = new StringBuilder();
    sb.append("<ul>");
    for (String username: usernames) {
      sb.append("<li>" + SafeHtmlUtils.htmlEscape(username) + "</li>");
    }
    sb.append("</ul>");
    this.usernames.setHTML(sb.toString());
  }
  
  public HasClickHandlers getAddUsersButton() {
    return this.addUsersButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
}
