package edu.ucla.cens.mobilize.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
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
import edu.ucla.cens.mobilize.client.model.CampaignShortInfo;
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
  
  // table columns 
  private enum Column { DOCUMENT_NAME, SIZE, CREATION_TIME, CREATOR, ACTIONS };
  
  public DocumentList() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }

  private void initComponents() {
    // set up table heading
    documentGrid.getRowFormatter().setStyleName(0, style.documentGridHeader());
    documentGrid.setText(0, Column.DOCUMENT_NAME.ordinal(), "Document Name");
    documentGrid.setText(0, Column.SIZE.ordinal(), "Size");
    documentGrid.setText(0, Column.CREATION_TIME.ordinal(), "Created on");
    documentGrid.setText(0, Column.CREATOR.ordinal(), "Created by");
    documentGrid.setText(0, Column.ACTIONS.ordinal(), "Actions");
    
    // css styles
    documentGrid.addStyleName(style.documentGrid());
    documentGrid.setCellSpacing(0);
    documentGrid.setCellPadding(4);
    documentGrid.getCellFormatter().setStyleName(0, 
                                                 Column.DOCUMENT_NAME.ordinal(), 
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
    this.documentGrid.setWidget(row, Column.DOCUMENT_NAME.ordinal(), documentNameDownloadLink); 
    this.documentGrid.getCellFormatter().setStyleName(row, 
                                                      Column.DOCUMENT_NAME.ordinal(), 
                                                      getDocumentNameStyle(documentInfo.getPrivacy()));

    // actions column
    this.documentGrid.setWidget(row, 
                               Column.ACTIONS.ordinal(), 
                               getActionsWidget(documentInfo.getDocumentId(),
                                                documentInfo.userCanEdit()));
    
    
  }
  
  private Widget getActionsWidget(int documentUUID, boolean canEdit) {
    Panel panel = new FlowPanel();
    // link to view document details
    InlineHyperlink detailsLink = 
      new InlineHyperlink("details", HistoryTokens.documentDetail(documentUUID));
    detailsLink.setStyleName(style.detailsLink());
    panel.add(detailsLink);
    // link to edit document (only visible to creator/supervisor)
    if (canEdit) {      
      InlineHyperlink editLink = 
        new InlineHyperlink("edit", HistoryTokens.documentEdit(documentUUID));
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
