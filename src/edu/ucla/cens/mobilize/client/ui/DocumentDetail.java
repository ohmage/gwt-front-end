package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
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
  @UiField InlineHyperlink editDocumentLink;
  @UiField InlineHyperlink backLinkTop;
  @UiField InlineHyperlink backLinkBottom;
  @UiField InlineLabel creatorLabel;
  @UiField InlineLabel creationDateLabel;
  @UiField InlineLabel sizeLabel;
  @UiField InlineLabel documentNameLabel;
  @UiField InlineLabel descriptionLabel;
  @UiField InlineLabel privacyLabel;
  @UiField VerticalPanel campaignsVerticalPanel;
  @UiField VerticalPanel classesVerticalPanel;

  private DateTimeFormat dateFormat = DateTimeFormat.getMediumDateFormat();
  
  public DocumentDetail() {
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
  }
  
  @SuppressWarnings("deprecation")
  private void initComponents() {
    
    this.backLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
    
    this.backLinkBottom.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
    
  }
  
  public void setDocumentDetail(DocumentInfo documentInfo, boolean canEdit) {
    if (documentInfo != null) {
      // creation details
      this.creatorLabel.setText(documentInfo.getCreator());
      this.creationDateLabel.setText(this.dateFormat.format(documentInfo.getCreationTimestamp()));
      this.sizeLabel.setText(Float.toString(documentInfo.getSize()));
      
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
      // TODO: should tell sho can see it
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
        this.editDocumentLink.setVisible(true);
        this.editDocumentLink.setTargetHistoryToken(HistoryTokens.documentEdit(documentInfo.getDocumentId()));
      } else {
        this.editDocumentLink.setVisible(false);
      }

      // hidden form with target set to _blank does a post request to fetch
      // the file and displays the result in a new window. 
      final FormPanel downloadForm = new FormPanel("_blank"); // target="_blank" to open new window
      downloadForm.setAction("http://localhost:8000/MobilizeWeb/getfile"); // FIXME
      downloadForm.setMethod(FormPanel.METHOD_POST);
      final Hidden fmt = new Hidden();
      fmt.setName("fmt"); // if fmt=download, set content-disposition header
      downloadForm.add(fmt);
      container.add(downloadForm, "hiddenFormContainer");
      // TODO: username, auth_token, etc also need to go in form fields
      

    }
  }
  
  

}
