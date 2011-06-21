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
import com.google.gwt.user.client.Window;
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
import edu.ucla.cens.mobilize.client.utils.AwUrlBasedResourceUtils;

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
  //@UiField InlineLabel creatorLabel; // not available from api yet
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
    
  }
  
  public void setDocumentDetail(DocumentInfo documentInfo, boolean canEdit) {
    if (documentInfo != null) {
      // creation details
      //this.creatorLabel.setText(documentInfo.getCreator());
      this.lastModifiedDateLabel.setText(this.dateFormat.format(documentInfo.getLastModifiedTimestamp()));
      String sizeString = NumberFormat.getFormat("######.00").format(documentInfo.getSizeInKB());
      this.sizeLabel.setText(sizeString + " KB");
      
      // copy info from data obj into fields
      this.documentNameLabel.setText(documentInfo.getDocumentName());
      this.descriptionLabel.setText(documentInfo.getDescription());

      // campaign list
      this.campaignsVerticalPanel.clear();
      for (String campaignName : documentInfo.getCampaigns()) {
        this.campaignsVerticalPanel.add(new InlineLabel(campaignName));
      }
      
      // class list
      this.classesVerticalPanel.clear();
      for (String className : documentInfo.getClasses()) {
        this.classesVerticalPanel.add(new InlineLabel(className));
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
      
      final String documentId = documentInfo.getDocumentId();
      this.actionLinkDownloadDocument.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (documentDownloadHandler != null) {
            documentDownloadHandler.onDownloadClick(documentId);
          }
        }
      });
      
      this.downloadButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          if (documentDownloadHandler != null) {
            documentDownloadHandler.onDownloadClick(documentId);
          }
        }
      });

    }
  }
  
  public void setDocumentDownloadHandler(DocumentDownloadHandler handler) {
    this.documentDownloadHandler = handler;
  }

}
