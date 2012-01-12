package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.ui.AdminMenu;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.StringUtils;

public class AdminClassListView extends Composite {

  private static AdminClassListViewUiBinder uiBinder = GWT
      .create(AdminClassListViewUiBinder.class);

  interface AdminClassListViewStyle extends CssResource {
    String classList();
    String classListHeaderRow();
    String classListNumberColumn();
    String detailsLink();
    String editLink();
    String oddRow();
    String tooltip();
  }
  
  interface AdminClassListViewUiBinder extends
      UiBinder<Widget, AdminClassListView> {
  }

  private final String cellWidthName = "220px";
  private final String cellWidthUrn = "300px";
  private final String cellWidthNumber = "80px";
  private final int maxClassNameLength = 29;
  
  @UiField AdminClassListViewStyle style;
  @UiField AdminMenu adminMenu;
  @UiField Grid classListHeader;
  @UiField HTMLPanel instructionsPanel;
  @UiField InlineHyperlink showAllClassesLink;
  @UiField Grid classListGrid;
  @UiField Button searchClassNameButton;
  @UiField TextBox searchClassNameTextBox;
  @UiField Button searchUsernameButton;
  @UiField TextBox searchUsernameTextBox;

  private static class Columns {
    static final int CLASS_NAME = 0;
    static final int CLASS_URN = 1;
    static final int MEMBER_COUNT = 2;
    static final int CAMPAIGN_COUNT = 3;
    static final int ACTIONS = 4;
    static final int columnCount = 5;
  }
  
  public AdminClassListView() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }
  
  private void initComponents() {
    // set link target for showing all classes
    this.showAllClassesLink.setTargetHistoryToken(HistoryTokens.adminClassList("*", null, null));
    this.classListHeader.addStyleName(style.classList());
    this.classListHeader.resize(1, Columns.columnCount);
    this.classListHeader.setText(0, Columns.CLASS_NAME, "Class Name");
    this.classListHeader.setText(0, Columns.CLASS_URN, "Urn");
    this.classListHeader.setText(0, Columns.MEMBER_COUNT, "Members");
    this.classListHeader.setText(0, Columns.CAMPAIGN_COUNT, "Campaigns");
    this.classListHeader.setText(0, Columns.ACTIONS, "Actions");
    // set column widths
    this.classListHeader.getColumnFormatter().setWidth(Columns.CLASS_NAME, this.cellWidthName);
    this.classListHeader.getColumnFormatter().setWidth(Columns.CLASS_URN, this.cellWidthUrn);
    this.classListHeader.getColumnFormatter().setWidth(Columns.CAMPAIGN_COUNT, this.cellWidthNumber);
    this.classListHeader.getColumnFormatter().setWidth(Columns.MEMBER_COUNT, this.cellWidthNumber);
    this.classListHeader.getRowFormatter().addStyleName(0, style.classListHeaderRow());
    this.classListHeader.setCellSpacing(0);
    this.classListHeader.setCellPadding(5);
    // set class grid columns to match header columns above
    this.classListGrid.resizeColumns(Columns.columnCount);
    this.classListGrid.setCellSpacing(0);
    this.classListGrid.setCellPadding(5);
    this.classListGrid.getColumnFormatter().setWidth(Columns.CLASS_NAME, this.cellWidthName);
    this.classListGrid.getColumnFormatter().setWidth(Columns.CLASS_URN, this.cellWidthUrn);
    this.classListGrid.getColumnFormatter().setWidth(Columns.CAMPAIGN_COUNT, this.cellWidthNumber);
    this.classListGrid.getColumnFormatter().setWidth(Columns.MEMBER_COUNT, this.cellWidthNumber);
    // update left nav 
    this.adminMenu.selectManageClasses();
  }

  // Returns a hyperlink with class name as text that links to the class detail page.
  // If the class name is longer than maxClassNameLength, the name is truncated,
  //  an ellipse is added, and the link is given a tooltip that shows the full
  //  class name on mouse hover.
  // (Note a js tooltip is used because the ordinary "title" tooltip takes too long to appear)
  private InlineHyperlink getClassNameLink(String classUrn, String className) {
    InlineHyperlink classNameLink = null;
    if (className.length() > maxClassNameLength) {
      classNameLink = new InlineHyperlink(StringUtils.shorten(className, maxClassNameLength),
                                          HistoryTokens.adminClassDetail(classUrn));
      
      final PopupPanel popup = new PopupPanel();
      HTML html = new HTML(className);
      html.setStyleName(style.tooltip());
      popup.setWidget(html);
      final InlineHyperlink finalLink = classNameLink;
      classNameLink.addDomHandler(new MouseOverHandler() {
        @Override
        public void onMouseOver(MouseOverEvent event) {
          popup.showRelativeTo(finalLink);
        }
      }, MouseOverEvent.getType());

      classNameLink.addDomHandler(new MouseOutHandler() {
        @Override
        public void onMouseOut(MouseOutEvent event) {
          popup.hide();
        }
      }, MouseOutEvent.getType());
      
      // hide popup on click, too, so it doesn't carry over to detail page
      classNameLink.addDomHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          popup.hide();
        }
      }, ClickEvent.getType());

    } else {
      classNameLink = new InlineHyperlink(className, HistoryTokens.adminClassDetail(classUrn));
    }
    return classNameLink;
  }
  
  public void addClass(String classUrn, String className, int memberCount, int campaignCount) {
    int lastRow = this.classListGrid.getRowCount();
    this.classListGrid.resizeRows(lastRow + 1);
    InlineHyperlink classNameLink = getClassNameLink(classUrn, className);
    this.classListGrid.setWidget(lastRow, Columns.CLASS_NAME, classNameLink);
    this.classListGrid.setText(lastRow, Columns.CLASS_URN, classUrn);
    this.classListGrid.setText(lastRow, Columns.MEMBER_COUNT, Integer.toString(memberCount));
    this.classListGrid.setText(lastRow, Columns.CAMPAIGN_COUNT, Integer.toString(campaignCount));
    this.classListGrid.setWidget(lastRow, Columns.ACTIONS, getActionsWidget(classUrn));
    if (lastRow % 2 == 0) {
      this.classListGrid.getRowFormatter().setStyleName(lastRow, style.oddRow());
    }
    
    // right align numbers
    this.classListGrid.getCellFormatter().setStyleName(lastRow, Columns.MEMBER_COUNT, style.classListNumberColumn());
    this.classListGrid.getCellFormatter().setStyleName(lastRow, Columns.CAMPAIGN_COUNT, style.classListNumberColumn());
    
  }
  
  public void setCampaignCount(String classUrn, int campaignCount) {
    for (int row = 0; row < this.classListGrid.getRowCount(); row++) {
      if (this.classListGrid.getText(row, Columns.CLASS_URN).equals(classUrn)) {
        this.classListGrid.setText(row, Columns.CAMPAIGN_COUNT, Integer.toString(campaignCount));
        break;
      }
    }
  }
  
  private Widget getActionsWidget(String classUrn) {
    Panel panel = new FlowPanel();
    InlineHyperlink detailsLink = new InlineHyperlink("view", HistoryTokens.adminClassDetail(classUrn));
    detailsLink.addStyleName(style.detailsLink());
    panel.add(detailsLink);
    InlineHyperlink editLink = new InlineHyperlink("edit", HistoryTokens.adminClassEdit(classUrn));
    editLink.addStyleName(style.editLink());
    panel.add(editLink);
    return panel.asWidget();
  }
  
  public void clearClassList() {
    this.classListGrid.resizeRows(0); 
  }
  
  public void clearSearchStrings() {
    this.searchClassNameTextBox.setText("");
    this.searchUsernameTextBox.setText("");
  }

  public HasClickHandlers getSearchClassNameButton() {
    return this.searchClassNameButton;
  }
  
  public HasClickHandlers getSearchUsernameButton() {
    return this.searchUsernameButton;
  } 
  
  public HasKeyDownHandlers getClassNameTextBox() {
    return this.searchClassNameTextBox;
  }
  
  public HasKeyDownHandlers getUsernameTextBox() {
    return this.searchUsernameTextBox;
  }
  
  public String getClassNameSearchString() {
    return this.searchClassNameTextBox.getText();
  }

  public void setClassNameSearchString(String className) {
    this.searchClassNameTextBox.setText(className);
  }
  
  public String getMemberUsernameSearchString() {
    return this.searchUsernameTextBox.getText();
  }
  
  public void setMemberUsernameSearchString(String username) {
    this.searchUsernameTextBox.setText(username);
  }
  
  /**
   * Shows a single error in a popup
   * @param msg
   * @param detail
   */
  public void showError(String msg, String detail) {
    ErrorDialog.show(msg, detail);
  }
  
  public void showInstructions() {
    this.instructionsPanel.setVisible(true);
    this.classListGrid.setVisible(false);
  }
  
  public void showClassList() {
    this.instructionsPanel.setVisible(false);
    this.classListGrid.setVisible(true);
  }
  
}
