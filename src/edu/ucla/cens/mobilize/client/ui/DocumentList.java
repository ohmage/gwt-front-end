package edu.ucla.cens.mobilize.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;

public class DocumentList extends Composite {

  // expose css styles from the uibinder template
  public interface DocumentListStyle extends CssResource {
    String documentGrid();
    String documentGridHeader();
    String documentGridNameColumn();
    String documentNamePrivate();
    String documentNameShared();
    String detailsLink();
    String editLink();
    String downloadLink();
    String oddRow();
  }
  
  private static DocumentListUiBinder uiBinder = GWT
      .create(DocumentListUiBinder.class);

  interface DocumentListUiBinder extends UiBinder<Widget, DocumentList> {
  }

  @UiField Grid documentGrid;
  @UiField DocumentListStyle style;

  private DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
  
  // convenience class for column indices
  private class Column { 
    private static final int DOCUMENT_NAME = 0;
    private static final int SIZE          = 1;
    private static final int CREATION_TIME = 2;
    private static final int CREATOR       = 3;
    private static final int ACTIONS       = 4;
    private static final int count         = 5; // num columns above
  }
  
  public DocumentList() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }

  private void initComponents() {
    // set up table heading
    documentGrid.resize(1, Column.count);
    documentGrid.getRowFormatter().setStyleName(0, style.documentGridHeader());
    documentGrid.setText(0, Column.DOCUMENT_NAME, "Document Name");
    documentGrid.setText(0, Column.SIZE, "Size");
    documentGrid.setText(0, Column.CREATION_TIME, "Created on");
    documentGrid.setText(0, Column.CREATOR, "Created by");
    documentGrid.setText(0, Column.ACTIONS, "Actions");
    
    // css styles
    documentGrid.addStyleName(style.documentGrid());
    documentGrid.setCellSpacing(0);
    documentGrid.setCellPadding(4);
    documentGrid.getCellFormatter().setStyleName(0, 
                                                 Column.DOCUMENT_NAME, 
                                                 style.documentGridNameColumn());
  }

  public void setDocuments(List<DocumentInfo> documents) {
    this.documentGrid.resizeRows(documents.size() + 1); // one extra row for header
    int row = 1; // 0th row is header
    for (DocumentInfo documentInfo : documents) {
      addDocument(row++, documentInfo);
    }
  }
  
  private void addDocument(int row, DocumentInfo documentInfo) {
    // stripe odd rows
    if (row % 2 != 0) {
      this.documentGrid.getRowFormatter().addStyleName(row, style.oddRow());
    }
    
    // TODO: get file extension from document name and show appropriate icon?
    
    // document name is a download link
    Hyperlink documentNameDownloadLink = 
      new Hyperlink(documentInfo.getDocumentName(), 
                    HistoryTokens.documentDetail(documentInfo.getDocumentId()));
    this.documentGrid.setWidget(row, Column.DOCUMENT_NAME, documentNameDownloadLink); 
    this.documentGrid.getCellFormatter().setStyleName(row, 
                                                      Column.DOCUMENT_NAME, 
                                                      getDocumentNameStyle(documentInfo.getPrivacy()));

    // size
    this.documentGrid.setText(row, 
                              Column.SIZE, 
                              Float.toString(documentInfo.getSize()) + "Mb");
    
    // created on
    this.documentGrid.setText(row,
                              Column.CREATION_TIME,
                              dateFormat.format(documentInfo.getCreationTimestamp()));    
    
    // created by
    this.documentGrid.setText(row,
                              Column.CREATOR,
                              documentInfo.getCreator());
    
    // actions column
    this.documentGrid.setWidget(row, 
                               Column.ACTIONS, 
                               getActionsWidget(documentInfo.getDocumentId(),
                                                documentInfo.userCanEdit()));
    
    
  }
  
  private Widget getActionsWidget(String documentId, boolean canEdit) {
    Panel panel = new FlowPanel();
    // link to view document details
    InlineHyperlink detailsLink = 
      new InlineHyperlink("details", HistoryTokens.documentDetail(documentId));
    detailsLink.setStyleName(style.detailsLink());
    panel.add(detailsLink);
    // link to edit document (only visible to creator/supervisor)
    if (canEdit) {      
      InlineHyperlink editLink = 
        new InlineHyperlink("edit", HistoryTokens.documentEdit(documentId));
      editLink.setStyleName(style.editLink());
      panel.add(editLink);
    }
    
    // FIXME: action link to download document?
    
    return panel.asWidget();
  }
  
  private String getDocumentNameStyle(Privacy privacy) {
    // lock if private, page or file type icon otherwise
    String retval = null;
    if (privacy.equals(Privacy.PRIVATE)) {
      retval = style.documentNamePrivate();
    } else if (privacy.equals(Privacy.SHARED)) {
      retval = style.documentNameShared();
    } else {
      retval = "";
    }
    return retval;
  }
}
