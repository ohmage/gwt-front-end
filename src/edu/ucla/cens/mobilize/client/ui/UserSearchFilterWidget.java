package edu.ucla.cens.mobilize.client.ui;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UserSearchFilterWidget extends Composite {

  private static UserSearchFilterWidgetUiBinder uiBinder = GWT
      .create(UserSearchFilterWidgetUiBinder.class);

  interface UserSearchFilterWidgetUiBinder extends
      UiBinder<Widget, UserSearchFilterWidget> {
  }

  @UiField TextBox firstNameTextBox;
  @UiField TextBox lastNameTextBox;
  @UiField TextBox emailTextBox;
  @UiField TextBox organizationTextBox;
  @UiField ListBox enabledListBox;
  @UiField ListBox canCreateListBox;
  @UiField ListBox adminListBox;

  
  private List<ListBox> ternaryListBoxes;
  private List<TextBox> textBoxes;
  
  public UserSearchFilterWidget() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }
  
  private void initComponents() {    
    this.ternaryListBoxes = Arrays.asList(this.enabledListBox, 
                                          this.canCreateListBox, 
                                          this.adminListBox);
    for (ListBox listBox : this.ternaryListBoxes) {
      listBox.addItem("Any Value");
      listBox.addItem("Yes");
      listBox.addItem("No");
    }
    this.textBoxes = Arrays.asList(this.firstNameTextBox, 
                                   this.lastNameTextBox,
                                   this.emailTextBox,
                                   this.organizationTextBox);
  }
  
  public void clearFields() {
    for (ListBox listBox : this.ternaryListBoxes) {
      setListBoxValue(listBox, null);
    }
    for (TextBox textBox : this.textBoxes) {
      textBox.setValue("");
    }
  }
  
  /**
   * @param yesOrNull True selects "yes," false selects "no," and null selects "any value"
   */
  private void setListBoxValue(ListBox listBox, Boolean yesOrNull) {
    if (yesOrNull == null) {
      listBox.setSelectedIndex(0); // any value
    } else if (yesOrNull) {
      listBox.setSelectedIndex(1); // yes
    } else {
      listBox.setSelectedIndex(2); // no
    }
  }
  
  /**
   * @return True, false, or null where null means include both states
   */
  public Boolean getIsEnabled() {
    Boolean isEnabled = null;
    String str = this.enabledListBox.getValue(this.enabledListBox.getSelectedIndex());
    if (str.equals("Yes")) {
      isEnabled = true;
    } else if (str.equals("No")) {
      isEnabled = false;
    }
    return isEnabled;
  }
  
  /**
   * @param isEnabled Use Boolean for yes/no, null for "any value"
   */
  public void setIsEnabled(Boolean isEnabled) {
    setListBoxValue(this.enabledListBox, isEnabled);
  }
  
  /**
   * @return True, false, or null where null means include both states
   */
  public Boolean getIsAdmin() {
    Boolean isEnabled = null;
    String str = this.adminListBox.getValue(this.adminListBox.getSelectedIndex());
    if (str.equals("Yes")) {
      isEnabled = true;
    } else if (str.equals("No")) {
      isEnabled = false;
    }
    return isEnabled;
  }
  
  /**
   * @param isAdmin Use Boolean for yes/no, null for "any value"
   */
  public void setIsAdmin(Boolean isAdmin) {
    setListBoxValue(this.adminListBox, isAdmin);
  }
  
  /**
   * @return True, false, or null where null means include both states
   */
  public Boolean getCanCreate() {
    Boolean isEnabled = null;
    String str = this.canCreateListBox.getValue(this.canCreateListBox.getSelectedIndex());
    if (str.equals("Yes")) {
      isEnabled = true;
    } else if (str.equals("No")) {
      isEnabled = false;
    }
    return isEnabled;
  }
  
  /**
   * @param canCreate Use Boolean for yes/no, null for "any value"
   */
  public void setCanCreate(Boolean canCreate) {
    setListBoxValue(this.canCreateListBox, canCreate);
  }
  
  public String getFirstNameSearchString() {
    return this.firstNameTextBox.getText();
  }
  
  public void setFirstNameSearchString(String firstNameSearchString) {
    this.firstNameTextBox.setText(firstNameSearchString);
  }
  
  public String getLastNameSearchString() {
    return this.lastNameTextBox.getText();
  }
  
  public void setLastNameSearchString(String lastNameSearchString) {
    this.lastNameTextBox.setText(lastNameSearchString);
  }

  public String getEmailSearchString() {
    return this.emailTextBox.getText();
  }
  
  public void setEmailSearchString(String emailSearchString) {
    this.emailTextBox.setText(emailSearchString);
  }
  
  public String getOrganizationSearchString() {
    return this.organizationTextBox.getText();
  }
  
  public void setOrganizationSearchString(String organizationSearchString) {
    this.organizationTextBox.setText(organizationSearchString);
  }
}
