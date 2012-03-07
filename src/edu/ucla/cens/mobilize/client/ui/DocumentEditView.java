package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.AppConfig;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;

public class DocumentEditView extends Composite {

  private static DocumentEditUiBinder uiBinder = GWT
      .create(DocumentEditUiBinder.class);

  @UiTemplate("DocumentEdit.ui.xml")
  interface DocumentEditUiBinder extends UiBinder<Widget, DocumentEditView> {
  }

  @UiField Label headerLabel;
  @UiField Label creatorLabel;
  @UiField Label creationDateLabel;
  @UiField Label sizeLabel;
  
  @UiField FormPanel formPanel;  
  @UiField MessageWidget messageWidget;
  @UiField Hidden authTokenHiddenField;
  @UiField Hidden clientHiddenField;
  @UiField Hidden documentIdHiddenField;
  @UiField HTMLPanel fileSizeWarningPanel;
  @UiField InlineLabel fileSizeWarningLabel;
  @UiField HTMLPanel fileUploadPanel;
  @UiField FileUpload fileUploadInput;
  @UiField TextBox documentNameTextBox;
  @UiField TextArea descriptionTextArea;
  @UiField ListBox privacyListBox;
  @UiField ListWidget campaignsListWidget;
  @UiField Button campaignsAddButton;
  @UiField Hidden campaignsToAddHiddenField;
  @UiField Hidden campaignsToRemoveHiddenField;
  @UiField ListWidget classesListWidget;
  @UiField Button classesAddButton;
  @UiField Hidden classesToAddHiddenField;
  @UiField Hidden classesToRemoveHiddenField;

  @UiField Button saveButton;
  @UiField Button cancelButton;
  @UiField HTMLPanel deletePanel;
  @UiField Button deleteButton;

  private List<String> originalCampaignUrns = new ArrayList<String>();
  private List<String> originalClassUrns = new ArrayList<String>();
  private boolean formIsInitialized = false;
  
  public DocumentEditView() {
    initWidget(uiBinder.createAndBindUi(this));
    
    privacyListBox.addItem(Privacy.PRIVATE.toUserFriendlyString(), Privacy.PRIVATE.toServerString());
    privacyListBox.addItem(Privacy.SHARED.toUserFriendlyString(), Privacy.SHARED.toServerString());
    
    // update max file size warning label if server has parameter
    if (AppConfig.getDocumentUploadMaxSize() > 0) {
      String sizeStr = getPrettySizeStr((float)AppConfig.getDocumentUploadMaxSize() / 1000);
      fileSizeWarningLabel.setText("Note: Maximum file size allowed is " + sizeStr + "");
    }
  }
  
  private String getPrettySizeStr(float size_in_kb) {
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
  
  // serialize list into format expected by server in form submission
  private String serializeList(Collection<String> items) {
    return (items != null) ? CollectionUtils.join(items, ",") : "";
  }
  
  private Collection<String> getClassesToAdd() {
    // visible list minus items in original list
    return CollectionUtils.setDiff(classesListWidget.getItems(), originalClassUrns);
  }

  private Collection<String> getClassesToRemove() {
    // original list minus items in visible list
    return CollectionUtils.setDiff(originalClassUrns, classesListWidget.getItems());    
  }

  private Collection<String> getCampaignsToAdd() {
    return CollectionUtils.setDiff(campaignsListWidget.getItems(), originalCampaignUrns);
  }
  
  private Collection<String> getCampaignsToRemove() {
    return CollectionUtils.setDiff(originalCampaignUrns, campaignsListWidget.getItems());
  }
  
  public String getDocumentId() {
    return documentIdHiddenField.getValue();
  }
  
  public String getDocumentName() {
    return documentNameTextBox.getText();
  }
  
  public List<String> getSelectedCampaigns() {
    return campaignsListWidget.getItems();
  }
  
  public List<String> getSelectedClasses() {
    return classesListWidget.getItems();
  }
  
  public String getFileName() {
    return fileUploadInput.getFilename();
  }
  
  public void initializeForm(String authToken, String serverLocation) {
    this.authTokenHiddenField.setValue(authToken);
    this.clientHiddenField.setValue(AwConstants.apiClientString);
    this.formPanel.setAction(serverLocation);
    this.formPanel.setEncoding(FormPanel.ENCODING_MULTIPART); // needed for file upload 
    this.formPanel.setMethod(FormPanel.METHOD_POST);
    this.saveButton.setEnabled(true);
    this.formIsInitialized = true;    
  }
  
  public boolean formIsInitialized() {
    return this.formIsInitialized;
  }
  
  private void prepareFormForSubmit() {    
    // copy values that have changed (or all values in case of create) into hidden fields  
    campaignsToAddHiddenField.setValue(serializeCollectionAsReaders(getCampaignsToAdd()));
    campaignsToRemoveHiddenField.setValue(serializeList(getCampaignsToRemove()));
    classesToAddHiddenField.setValue(serializeCollectionAsReaders(getClassesToAdd()));
    classesToRemoveHiddenField.setValue(serializeList(getClassesToRemove()));
    // FIXME: the add methods above serialize all as readers right now - should extend gui
    //   to let user add with any role
    
    // disable empty fields to avoid api errors
    if (campaignsToAddHiddenField.getValue().isEmpty()) campaignsToAddHiddenField.getElement().removeAttribute("name");
    if (campaignsToRemoveHiddenField.getValue().isEmpty()) campaignsToRemoveHiddenField.getElement().removeAttribute("name");
    if (classesToAddHiddenField.getValue().isEmpty()) classesToAddHiddenField.getElement().removeAttribute("name");
    if (classesToRemoveHiddenField.getValue().isEmpty()) classesToRemoveHiddenField.getElement().removeAttribute("name");
  }
  
  public void setHiddenFieldsForCreate() {
    documentIdHiddenField.getElement().removeAttribute("name"); // disabled for create
    campaignsToAddHiddenField.setName("document_campaign_role_list");
    campaignsToRemoveHiddenField.getElement().removeAttribute("name"); // disabled
    classesToAddHiddenField.setName("document_class_role_list");
    classesToRemoveHiddenField.getElement().removeAttribute("name"); // disabled
    fileUploadInput.setName("document"); // only makes sense on create
  }
  
  public void setHiddenFieldsForEdit() {
    documentIdHiddenField.setName("document_id");
    campaignsToAddHiddenField.setName("campaign_role_list_add");
    campaignsToRemoveHiddenField.setName("campaign_list_remove");
    classesToAddHiddenField.setName("class_role_list_add");
    classesToRemoveHiddenField.setName("class_list_remove");
    fileUploadInput.getElement().removeAttribute("name"); // disabled
  }
  
  // TODO: Just makes everyone a reader for now. Gui should be extended to 
  // allow user to choose role when they add class/campaign
  private String serializeCollectionAsReaders(Collection<String> items) {
    if (items == null || items.isEmpty()) return "";
    StringBuilder sb = new StringBuilder();
    for (String item : items) {
      sb.append(",").append(item).append(";").append("reader");
    }
    String retval = sb.toString().substring(1);
    return retval;
  }
  
  public void submitForm() {
    prepareFormForSubmit();
    formPanel.submit();
  }
  
  public void clearFormFields() {
    formPanel.reset();
    campaignsListWidget.clear();
    classesListWidget.clear();
    originalCampaignUrns.clear();
    originalClassUrns.clear();
    documentIdHiddenField.setValue("");
    campaignsToAddHiddenField.setValue("");
    campaignsToRemoveHiddenField.setValue("");
    classesToAddHiddenField.setValue("");
    classesToRemoveHiddenField.setValue("");
  }
  
  public void setDeletePanelVisible(boolean isVisible) {
    deletePanel.setVisible(isVisible);    
  }
  
  public void setUploadPanelVisible(boolean isVisible) {
    fileSizeWarningPanel.setVisible(isVisible);
    fileUploadPanel.setVisible(isVisible);
    fileUploadInput.setEnabled(isVisible);
  }
  
  public void setHeader(String headerText) {
    headerLabel.setText(headerText);
  }
  
  public void setDocument(DocumentInfo documentInfo) {
    clearFormFields();
    if (documentInfo == null) return;
    originalCampaignUrns.clear();
    originalCampaignUrns.addAll(documentInfo.getCampaigns());
    originalClassUrns.clear();
    originalClassUrns.addAll(documentInfo.getClasses());
    
    headerLabel.setText("Editing " + documentInfo.getDocumentName());
    documentNameTextBox.setText(documentInfo.getDocumentName());
    documentIdHiddenField.setValue(documentInfo.getDocumentId());
    descriptionTextArea.setText(documentInfo.getDescription());
    String privacyString = documentInfo.getPrivacy().toUserFriendlyString();
    
    for (int i = 0; i < privacyListBox.getItemCount(); i++) {
      // assumes user friendly strings are unique
      if (privacyListBox.getItemText(i).equals(privacyString)) {
        privacyListBox.setSelectedIndex(i);
        break;
      }
    }
    for (String campaignUrn : documentInfo.getCampaigns()) {
      // FIXME: show campaign name instead of urn
      campaignsListWidget.addItem(campaignUrn);
    }
    for (String classUrn : documentInfo.getClasses()) {
      classesListWidget.addItem(classUrn);
    }
  }
  
  public void setDocumentName(String documentName) {
    documentNameTextBox.setText(documentName);
  }
  
  public void showCampaignChoices(final Map<String, String> campaignUrnToCampaignNameMap) {
    if (campaignUrnToCampaignNameMap == null) return;
    final MultiSelectDialog campaignChooserDialog = new MultiSelectDialog();
    campaignChooserDialog.setCaption("Add campaignes to the campaign");
    campaignChooserDialog.setItems(campaignUrnToCampaignNameMap);    
    campaignChooserDialog.setSubmitHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        List<String> selected = campaignChooserDialog.getSelectedItems();
        for (String campaignUrn : selected) {
          campaignsListWidget.addItem(campaignUrnToCampaignNameMap.get(campaignUrn), campaignUrn);
        }
        campaignChooserDialog.hide();
      }
    });
    campaignChooserDialog.show();
  }
  
  public void showClassChoices(final Map<String, String> classUrnToClassNameMap) {
    if (classUrnToClassNameMap == null) return;
    final MultiSelectDialog classChooserDialog = new MultiSelectDialog();
    classChooserDialog.setCaption("Add classes to the campaign");
    classChooserDialog.setItems(classUrnToClassNameMap);    
    classChooserDialog.setSubmitHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        List<String> selected = classChooserDialog.getSelectedItems();
        for (String classUrn : selected) {
          classesListWidget.addItem(classUrnToClassNameMap.get(classUrn), classUrn);
        }
        classChooserDialog.hide();
      }
    });
    classChooserDialog.show();
  }
  
  public void showConfirmDelete(final ClickHandler onConfirmDelete) {
    final DialogBox dialog = new DialogBox();
    dialog.setGlassEnabled(true);
    dialog.setText("Are you sure you want to delete this document?");
    dialog.setModal(true);
    Button deleteButton = new Button("Delete");
    deleteButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (onConfirmDelete != null) onConfirmDelete.onClick(event);
        dialog.hide();
      }
    });
    Button cancelButton = new Button("Cancel");
    cancelButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialog.hide();
      }
    });
    FlowPanel panel = new FlowPanel(); 
    panel.add(deleteButton);
    panel.add(cancelButton);
    dialog.add(panel);
    dialog.center();
  }

  // TODO: make this a generic gui element somewhere?
  public void showError(String msg) {
    final DialogBox errorDialog = new DialogBox();
    errorDialog.setGlassEnabled(true);
    errorDialog.setText(msg);
    Button dismissButton = new Button("OK");
    dismissButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        errorDialog.hide(); 
      }
    });
    errorDialog.add(dismissButton);
    errorDialog.center();
  }

  public void disableSaveButton() {
    this.saveButton.setEnabled(false);
  }
  
  public void enableSaveButton() {
    this.saveButton.setEnabled(true);
  }
  
  public void showWaitIndicator() {
    WaitIndicator.show();
  }
  
  public void hideWaitIndicator() {
    WaitIndicator.hide();
  }
  
  /******* METHODS THAT RETURN GUI ELEMENTS FOR EVENT HANDLING *****/
  /* Presenter uses these to wire up event handling to buttons, etc */
  
  public HasClickHandlers getCampaignsAddButton() {
    return campaignsAddButton;
  }
  
  public HasClickHandlers getClassesAddButton() {
    return classesAddButton;
  }
  
  public HasClickHandlers getSaveButton() {
    return saveButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return cancelButton;
  }
  
  public HasClickHandlers getDeleteButton() {
    return deleteButton;
  }

  public void addSubmitCompleteHandler(SubmitCompleteHandler onSubmitComplete) {
    formPanel.addSubmitCompleteHandler(onSubmitComplete);    
  }
  
  public HasChangeHandlers getFileUploadInput() {
    return fileUploadInput;
  }
  
  /******* END OF METHODS THAT RETURN GUI ELEMENTS FOR EVENT HANDLING *****/

}
