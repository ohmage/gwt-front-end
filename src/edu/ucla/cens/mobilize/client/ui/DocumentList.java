package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.event.DocumentDownloadHandler;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;

public class DocumentList extends Composite {
  
  // expose css styles from the uibinder template
  public interface DocumentListStyle extends CssResource {
    String documentGrid();
    String documentGridHeader();
    String documentGridNameColumn();
    String documentGridSizeColumn();
    String documentName();
    String documentSize();
    String privacyPrivate();
    String privacyShared();
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
  
  // set this delegate to define how download links are handled
  private DocumentDownloadHandler documentDownloadHandler;
  
  // convenience class for column indices
  private class Column { 
    private static final int DOCUMENT_NAME = 0;
    private static final int SIZE          = 1;
    private static final int CREATOR       = 2;
    private static final int PRIVACY       = 3;
    private static final int MODIFIED_TIME = 4;
    private static final int ACTIONS       = 5;
    
    private static final int count         = 6; // number of columns above
  }
  
  public DocumentList() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }

  private void initComponents() {
    // set up table heading
    documentGrid.resize(1, Column.count);
    documentGrid.getRowFormatter().setStyleName(0, style.documentGridHeader());
    documentGrid.setText(0, Column.DOCUMENT_NAME, "Document name");
    documentGrid.setText(0, Column.SIZE, "Size");
    documentGrid.setText(0, Column.PRIVACY, "Privacy");
    documentGrid.setText(0, Column.MODIFIED_TIME, "Last modified");
    documentGrid.setText(0, Column.CREATOR, "Created by");
    documentGrid.setText(0, Column.ACTIONS, "Actions");
    
    // css styles
    documentGrid.addStyleName(style.documentGrid());
    documentGrid.setCellSpacing(0);
    documentGrid.setCellPadding(4);
    documentGrid.getCellFormatter().setStyleName(0, 
                                                 Column.DOCUMENT_NAME, 
                                                 style.documentGridNameColumn());
    documentGrid.getCellFormatter().setStyleName(0, 
                                                 Column.SIZE, 
                                                 style.documentGridSizeColumn());
  }

  public void setDocuments(List<DocumentInfo> documents) {
    List<DocumentInfo> sortedDocuments = new ArrayList(documents);
    Collections.sort(sortedDocuments, new DocumentNameComparator());
    this.documentGrid.resizeRows(sortedDocuments.size() + 1); // one extra row for header
    int row = 1; // 0th row is header
    for (DocumentInfo documentInfo : sortedDocuments) {
      addDocument(row++, documentInfo);
    }
  }
  
  public void setDocumentDownloadHandler(DocumentDownloadHandler handler) {
    this.documentDownloadHandler = handler;
  }
  
  private String getSizeAsStr(float size_in_kb) {
	  String postfix = "KB";
	  if (size_in_kb < 1000) {
		  postfix = "KB";
	  } else if (size_in_kb < 1000000) {
		  size_in_kb /= 1000;
		  postfix = "MB";
	  } else {
		  size_in_kb /= 1000000;
		  postfix = "GB";
	  }
	  return NumberFormat.getFormat("#####0.00").format(size_in_kb) + " " + postfix;
  }
  
  private void addDocument(int row, DocumentInfo documentInfo) {
    // stripe odd rows
    if (row % 2 != 0) {
      this.documentGrid.getRowFormatter().addStyleName(row, style.oddRow());
    }
    
    // TODO: get file extension from document name and show appropriate icon?
    
    // document name links to detail page
    Hyperlink documentNameLink = 
      new Hyperlink(documentInfo.getDocumentName(), 
                    HistoryTokens.documentDetail(documentInfo.getDocumentId()));
    this.documentGrid.setWidget(row, Column.DOCUMENT_NAME, documentNameLink);
    this.documentGrid.getCellFormatter().setStyleName(row, 
                                                      Column.DOCUMENT_NAME, 
                                                      style.documentName());

    // size
    this.documentGrid.setText(row, 
                              Column.SIZE, 
                              getSizeAsStr(documentInfo.getSizeInKB()));
    this.documentGrid.getCellFormatter().setStyleName(row, Column.SIZE, style.documentSize());
    
    // privacy 
    this.documentGrid.setText(row, Column.PRIVACY, documentInfo.getPrivacy().toString());
    this.documentGrid.getCellFormatter().setStyleName(row, 
                                                      Column.PRIVACY, 
                                                      getPrivacyStyle(documentInfo.getPrivacy()));    
    
    // created on
    this.documentGrid.setText(row,
                              Column.MODIFIED_TIME,
                              dateFormat.format(documentInfo.getLastModifiedTimestamp()));    
    
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
  
  private Widget getActionsWidget(final String documentId, boolean canEdit) {
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
    
    InlineLabel downloadLink = new InlineLabel("download");
    downloadLink.setStyleName(style.downloadLink());
    downloadLink.addStyleName("link");
    downloadLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (documentDownloadHandler != null) {
          documentDownloadHandler.onDownloadClick(documentId);
        }
      }
    });
    panel.add(downloadLink);
    
    return panel.asWidget();
  }
  
  private String getPrivacyStyle(Privacy privacy) {
    // lock if private, page or file type icon otherwise
    String retval = null;
    if (privacy.equals(Privacy.PRIVATE)) {
      retval = style.privacyPrivate();
    } else if (privacy.equals(Privacy.SHARED)) {
      retval = style.privacyShared();
    } else {
      retval = "";
    }
    return retval;
  }
  
  protected class DocumentNameComparator implements Comparator<DocumentInfo> {
    @Override
    public int compare(DocumentInfo arg0, DocumentInfo arg1) {
      return arg0.getDocumentName().compareTo(arg1.getDocumentName());
    }
  }
}
