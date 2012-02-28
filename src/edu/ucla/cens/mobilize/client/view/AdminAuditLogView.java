package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;

import edu.ucla.cens.mobilize.client.AwConstants.AwUri;
import edu.ucla.cens.mobilize.client.model.AuditLogEntry;
import edu.ucla.cens.mobilize.client.resources.cellwidgets.CellTableResources;
import edu.ucla.cens.mobilize.client.ui.AdminMenu;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.ui.WaitIndicator;
import edu.ucla.cens.mobilize.client.utils.DateUtils;

public class AdminAuditLogView extends Composite {

  private static AdminAuditLogViewUiBinder uiBinder = GWT
      .create(AdminAuditLogViewUiBinder.class);

  interface AdminAuditLogViewUiBinder extends
      UiBinder<Widget, AdminAuditLogView> {
  }
  
  public interface AdminAuditLogViewStyles extends CssResource {
    String emptyLogMessage();
  }
  
  @UiField AdminAuditLogViewStyles style;
  @UiField AdminMenu adminMenu;
  @UiField DateBox dateBox;
  @UiField CheckBox onlyFailuresCheckBox;
  @UiField ListBox uriListBox;
  @UiField Button goButton;
  @UiField(provided = true) CellTable<AuditLogEntry> auditLogCellTable;
  @UiField(provided = true) SimplePager pager;

  private DateTimeFormat dateFormatLog = DateUtils.getTimestampFormat();
  private DateBox.DefaultFormat dateFormatDateBox = new DateBox.DefaultFormat(DateUtils.getDateBoxDisplayFormat());
  private ListDataProvider<AuditLogEntry> auditLogData = new ListDataProvider<AuditLogEntry>();
  
  public AdminAuditLogView() {
    initCellTable(); // must be before initWidget
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }

  private void initComponents() {
    adminMenu.selectAuditLog();
    this.dateBox.setFormat(dateFormatDateBox);
    
    // fill uri drop down
    this.uriListBox.addItem("All", "");
    List<String> uris = new ArrayList<String>();
    for (AwUri uri : AwUri.values()) {
      uris.add(uri.toString());
    }
    Collections.sort(uris);
    for (String uri : uris) {
      this.uriListBox.addItem(uri);
    }
    
    // set message that will be shown when there are no log entries
    HTML html = new HTML("No log entries for the selected date.");
    html.addStyleName(style.emptyLogMessage());
    this.auditLogCellTable.setEmptyTableWidget(html);
  }
  
  public void showError(String msg, String detail) {
    ErrorDialog.show(msg, detail);
  }
  
  public void showLog(List<AuditLogEntry> logEntries) {
    // NOTE: clear and add items to list instead of using setList() b/c otherwise
    // sorting doesn't work as expected
    // http://code.google.com/p/google-web-toolkit/issues/detail?id=6686
    this.auditLogData.getList().clear();
    if (logEntries != null) this.auditLogData.getList().addAll(logEntries);
  }
  
  public Date getDate() {
    return this.dateBox.getValue();
  }
  
  public void setDate(Date date) {
    this.dateBox.setValue(date);
  }
  
  public boolean getOnlyFailuresFlag() {
    return this.onlyFailuresCheckBox.getValue();
  }
  
  public void setOnlyFailuresFlag(boolean isChecked) {
    this.onlyFailuresCheckBox.setValue(isChecked);
  }

  /**
   * @return Uri or null if none
   */
  public String getUri() {
    int index = this.uriListBox.getSelectedIndex();
    return index >= 0 ? this.uriListBox.getValue(index) : null;
  }
  
  public void setUri(String uri) {
    this.uriListBox.setSelectedIndex(-1);
    for (int i = 0; i < this.uriListBox.getItemCount(); i++) {
      if (this.uriListBox.getItemText(i).equals(uri)) {
        this.uriListBox.setSelectedIndex(i);
        break;
      }
    }
  }
  
  public HasClickHandlers getGoButton() {
    return this.goButton;
  }
  
  private void initCellTable() {
    int pageSize = 1000;
    CellTable.Resources resources = GWT.create(CellTableResources.class);
    this.auditLogCellTable = new CellTable<AuditLogEntry>(pageSize, resources);
    this.auditLogCellTable.setWidth("100%");
    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
    this.pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
    this.pager.setDisplay(this.auditLogCellTable);    
    ListHandler<AuditLogEntry> sortHandler = new ListHandler<AuditLogEntry>(this.auditLogData.getList());
    this.auditLogCellTable.addColumnSortHandler(sortHandler);
    
    // timestamp column
    Column<AuditLogEntry, Date> timestampColumn = new Column<AuditLogEntry, Date>(new DateCell(this.dateFormatLog)) {
      @Override
      public Date getValue(AuditLogEntry logEntry) {
        return logEntry.getTimestamp();
      }
    };
    timestampColumn.setSortable(true);
    sortHandler.setComparator(timestampColumn, new Comparator<AuditLogEntry>() {
      @Override
      public int compare(AuditLogEntry o1, AuditLogEntry o2) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
      }
    });
    
    Column<AuditLogEntry, String> responseStatusColumn = 
      new Column<AuditLogEntry, String>(new TextCell()) {
        @Override
        public String getValue(AuditLogEntry logEntry) {
          return logEntry.getResponseStatus().toUserFriendlyString();
        }
    };
    responseStatusColumn.setSortable(true);
    sortHandler.setComparator(responseStatusColumn, new Comparator<AuditLogEntry>() {
      @Override
      public int compare(AuditLogEntry o1, AuditLogEntry o2) {
        return o1.getResponseStatus().compareTo(o2.getResponseStatus());
      }
    });
    
    Column<AuditLogEntry, Number> timeToFillRequestColumn = 
      new Column<AuditLogEntry, Number>(new NumberCell()) {
        @Override
        public Double getValue(AuditLogEntry logEntry) {
          return logEntry.getTimeToFillRequest();
        }
     };
     timeToFillRequestColumn.setSortable(true);
     sortHandler.setComparator(timeToFillRequestColumn, new Comparator<AuditLogEntry>() {
      @Override
      public int compare(AuditLogEntry o1, AuditLogEntry o2) {
        if (o1.getTimeToFillRequest() < o2.getTimeToFillRequest()) return -1;
        if (o1.getTimeToFillRequest() > o2.getTimeToFillRequest()) return 1;
        return 0;
      }
     });
     
     Column<AuditLogEntry, String> uriColumn = 
       new Column<AuditLogEntry, String>(new TextCell()) {
        @Override
        public String getValue(AuditLogEntry logEntry) {
          AwUri uri = logEntry.getUri();
          return uri != null ? logEntry.getUri().toString() : "";
        }
     };
     uriColumn.setSortable(true);
     sortHandler.setComparator(uriColumn, new Comparator<AuditLogEntry>() {
      @Override
      public int compare(AuditLogEntry arg0, AuditLogEntry arg1) {
        return arg0.getUri().compareTo(arg1.getUri());
      }
     });

     // don't outline cells when user clicks on them
     //this.auditLogCellTable.setSelectionModel(new NoSelectionModel<AuditLogEntry>());

     this.auditLogCellTable.addColumn(timestampColumn, "Timestamp");
     this.auditLogCellTable.addColumn(uriColumn, "Api Endpoint");
     this.auditLogCellTable.addColumn(responseStatusColumn, "Response status");
     this.auditLogCellTable.addColumn(timeToFillRequestColumn, "Time to fill request (ms)");
     
     ColumnSortList columnSortList = this.auditLogCellTable.getColumnSortList();
     columnSortList.push(timeToFillRequestColumn);
     columnSortList.push(timeToFillRequestColumn);
     columnSortList.push(uriColumn);
     columnSortList.push(timestampColumn);

     this.auditLogData.addDataDisplay(this.auditLogCellTable);
     
  }
  
  public void showWaitIndicator() {
    WaitIndicator.show();
  }
  
  public void hideWaitIndicator() {
    WaitIndicator.hide();
  }
  
}
