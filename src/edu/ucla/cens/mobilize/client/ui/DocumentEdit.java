package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.DocumentInfo;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;

public class DocumentEdit extends Composite {

  private static DocumentEditUiBinder uiBinder = GWT
      .create(DocumentEditUiBinder.class);

  interface DocumentEditUiBinder extends UiBinder<Widget, DocumentEdit> {
  }

  @UiField Label headerLabel;
  @UiField Label creatorLabel;
  @UiField Label creationDateLabel;
  @UiField Label sizeLabel;
  
  @UiField FormPanel formPanel;  
  @UiField MessageWidget messageWidget;
  @UiField Hidden authTokenHiddenField;
  @UiField Hidden documentIdHiddenField;
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
  @UiField Button deleteButton;

  List<String> originalCampaignUrns = new ArrayList<String>();
  List<String> originalClassUrns = new ArrayList<String>();
  
  public DocumentEdit() {
    initWidget(uiBinder.createAndBindUi(this));
    
    privacyListBox.addItem(Privacy.PRIVATE.toUserFriendlyString(), Privacy.PRIVATE.toServerString());
    privacyListBox.addItem(Privacy.SHARED.toUserFriendlyString(), Privacy.SHARED.toServerString());
    
    campaignsListWidget.addItem("test1", "test1");
    campaignsListWidget.addItem("test1", "test2");
    campaignsListWidget.addItem("test3", "test3");
  }

  private void prepareFormForSubmit() {
    campaignsToAddHiddenField.setValue(serializeList(getCampaignsToAdd()));
    campaignsToRemoveHiddenField.setValue(serializeList(getCampaignsToRemove()));
    classesToAddHiddenField.setValue(serializeList(getClassesToAdd()));
    classesToRemoveHiddenField.setValue(serializeList(getClassesToRemove()));
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
  
  public void clearForm() {
    formPanel.reset();
    campaignsListWidget.clear();
    classesListWidget.clear();
  }
  
  public void setDocument(DocumentInfo documentInfo) {
    clearForm();
    if (documentInfo == null) return;
    
    originalCampaignUrns.addAll(documentInfo.getCampaigns());
    originalCampaignUrns.addAll(documentInfo.getClasses());
    
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

  /*
  public void setDocumentName(String documentName) {
  }
  
  public void setDescription(String description) {
  }
  
  public void setPrivacy(Privacy privacy) {
  }
  
  public void addCampaign(String campaignName, String campaignUrn) {
  }
  
  public void setSelectedCampaigns() {
    assert this.originalCampaigns != null : "Save list of original campaigns before selecting campaigns.";
  }
  
  public void setSelectedClasses() {
    assert this.originalClasses != null : "Save list of original classes before selecting classes.";
  }
  
  public void addClass(String classUrn) {
  }*/ 

  
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

}
