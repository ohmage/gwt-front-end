package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.event.DocumentDownloadHandler;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;

public class DocumentDetail extends Composite {

  public interface DocumentDetailStyle extends CssResource {
    String privacyPrivate();
    String privacyShared();
  }
  
  private static DocumentDetailUiBinder uiBinder = GWT
      .create(DocumentDetailUiBinder.class);

  interface DocumentDetailUiBinder extends UiBinder<Widget, DocumentDetail> {
  }

  @UiField DocumentDetailStyle style;
  @UiField HTMLPanel container;
  @UiField Anchor backLinkTop;
  @UiField InlineLabel creatorLabel; 
  @UiField InlineLabel lastModifiedDateLabel;
  @UiField InlineLabel sizeLabel;
  @UiField InlineLabel documentNameLabel;
  @UiField InlineLabel descriptionLabel;
  @UiField InlineLabel privacyLabel;
  @UiField VerticalPanel campaignsVerticalPanel;
  @UiField VerticalPanel classesVerticalPanel;
  @UiField InlineHyperlink actionLinkEditDocument;
  @UiField Anchor actionLinkDownloadDocument;
  @UiField Button downloadButton;

  private DateTimeFormat dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);

  // set this delegate to define how download links are handled
  private DocumentDownloadHandler documentDownloadHandler;
  
  // uuid of currently displayed document is needed for download handlers
  private String documentId;
  
  public DocumentDetail() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }
  
  private void initComponents() {
    
    this.backLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
    
    
    this.actionLinkDownloadDocument.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // download currently displayed document
        String documentId = getDocumentId();
        if (documentId != null && documentDownloadHandler != null ) {
          documentDownloadHandler.onDownloadClick(documentId);
        }
      }
    });
    
    this.downloadButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // download currently displayed document
        String documentId = getDocumentId();
        if (documentId != null && documentDownloadHandler != null) {
          documentDownloadHandler.onDownloadClick(documentId);
        }
      }
    });
    
  }
  
  private String getDocumentId() {
    return this.documentId; // gets set in setDocumentDetail()
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
  
  public void setDocumentDetail(DocumentInfo documentInfo, boolean canEdit) {
    if (documentInfo != null) {
      // save document id so it can be used in download link/button click handlers
      this.documentId = documentInfo.getDocumentId();
      
      // creation details
      this.creatorLabel.setText(documentInfo.getCreator());
      this.lastModifiedDateLabel.setText(this.dateFormat.format(documentInfo.getLastModifiedTimestamp()));
      String sizeString = getSizeAsStr(documentInfo.getSizeInKB());
      this.sizeLabel.setText(sizeString + " (" + NumberFormat.getDecimalFormat().format(documentInfo.getSizeInBytes()) + " bytes)");
      
      // copy info from data obj into fields
      this.documentNameLabel.setText(documentInfo.getDocumentName());
      this.descriptionLabel.setText(documentInfo.getDescription());

      // campaign list
      this.campaignsVerticalPanel.clear();
      for (String campaignName : documentInfo.getCampaigns()) {
        this.campaignsVerticalPanel.add(new InlineLabel(campaignName));
      }
      if (this.campaignsVerticalPanel.getWidgetCount() == 0) {
    	  this.campaignsVerticalPanel.add(new InlineLabel("(there are no campaigns associated with this document)"));
      }
      
      // class list
      this.classesVerticalPanel.clear();
      for (String className : documentInfo.getClasses()) {
        this.classesVerticalPanel.add(new InlineLabel(className));
      }
      if (this.classesVerticalPanel.getWidgetCount() == 0) {
    	  this.classesVerticalPanel.add(new InlineLabel("(there are no classes associated with this document)"));
      }

      Privacy privacy = documentInfo.getPrivacy();
      privacyLabel.setText(privacy.toUserFriendlyString());
      // privacy style is set dynamically
      String privacyLabelStyle = null;
      switch (privacy) {
      case PRIVATE:
        privacyLabelStyle = style.privacyPrivate();
        break;
      case SHARED:
        privacyLabelStyle = style.privacyShared();
        break;
      default: 
        privacyLabelStyle = "";
        break;
      }
      privacyLabel.setStyleName(privacyLabelStyle);
      
      if (canEdit) {
        this.actionLinkEditDocument.setVisible(true);
        this.actionLinkEditDocument.setTargetHistoryToken(HistoryTokens.documentEdit(documentInfo.getDocumentId()));
      } else {
        this.actionLinkEditDocument.setVisible(false);
      }
      
    }
  }

  
  public void setDocumentDownloadHandler(DocumentDownloadHandler handler) {
    this.documentDownloadHandler = handler;
  }

}
