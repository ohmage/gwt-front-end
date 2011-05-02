package edu.ucla.cens.mobilize.client.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.AndWellnessConstants;
import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;

public class CampaignEditForm extends Composite {

  private static CampaignEditFormUiBinder uiBinder = GWT
      .create(CampaignEditFormUiBinder.class);

  interface CampaignEditFormUiBinder extends UiBinder<Widget, CampaignEditForm> {
  }

  @UiField Label header;
  @UiField Hidden authTokenHiddenField; 
  @UiField InlineLabel campaignName;
  @UiField InlineLabel campaignUrn;
  @UiField Hidden campaignUrnHiddenField;
  @UiField TextArea campaignDescriptionTextArea;
  @UiField Button addClassesButton;
  @UiField FlexTable classesFlexTable;
  @UiField Hidden classHiddenField; // holds serialized class list
  @UiField Button addAuthorsButton;
  @UiField FlexTable authorsFlexTable;
  @UiField Hidden authorHiddenField; // holds serialized author list
  @UiField FileUpload chooseFileButton;
  @UiField ListBox runningStateListBox;
  @UiField ListBox privacyListBox;
  @UiField Button saveButton;
  @UiField Button cancelButton;
  @UiField Button deleteButton;
  @UiField HTMLPanel deletePanel;
  @UiField HTMLPanel fileInputContainer; // so file input can be removed/reattached
  
  @UiField FormPanel formPanel;

  boolean isNewCampaign;
  boolean formIsInitialized = false;
  
  // dialog that lets user select groups of classes
  MultiSelectDialog classChooserDialog;
  Map<String, String> classesToChooseFrom;
  
  // dialog that lets user select groups of authors
  MultiSelectDialog authorChooserDialog;
  List<String> authorsToChooseFrom = new ArrayList<String>();
  
  public CampaignEditForm() {
    initWidget(uiBinder.createAndBindUi(this));
    
    classChooserDialog = new MultiSelectDialog();
    classChooserDialog.setCaption("Add classes to the campaign");
    authorChooserDialog = new MultiSelectDialog();
    authorChooserDialog.setCaption("Add co-authors to the campaign");

    // populate list boxes
    // TODO: get allowed privacy states from campaign config    
    runningStateListBox.addItem("Stopped", "stopped"); // 0 = stopped
    runningStateListBox.addItem("Running", "running"); // 1 = running
    privacyListBox.addItem("Private", "private"); // 0 = private
    privacyListBox.addItem("Shared", "shared"); // 1 = shared/public
    // if ... addItem("INVISIBLE")

    String url = isNewCampaign ? AndWellnessConstants.getCampaignCreateUrl() : 
                                 AndWellnessConstants.getCampaignUpdateUrl();
    formPanel.setAction(url);
    formPanel.setEncoding(FormPanel.ENCODING_MULTIPART); // needed for file upload 
    formPanel.setMethod(FormPanel.METHOD_POST); 
    formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event) {
        clearFormFields();
        String result = event.getResults();
        // NOTE: gwt formPanel results can be null if sending to a different domain
        // TODO: handle failure (should be managed by presenter)
        
        // redirect to campaign list so user can see results
        History.newItem(HistoryTokens.campaignList()); 
      }
    });
    
    // invisible when empty so add button lines up with label
    classesFlexTable.setVisible(false);
    classesFlexTable.setCellSpacing(0);
    classesFlexTable.setBorderWidth(0);
    
    // invisible when empty so add button lines up with label
    authorsFlexTable.setVisible(false);
    authorsFlexTable.setCellSpacing(0);
    authorsFlexTable.setBorderWidth(0);
    
    bind();
  }
  
  private void bind() {

    // Add Classes button opens a dialog showing a list of classes.
    // Selected names are saved to the class list on dialog submit.
    this.addClassesButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (classesToChooseFrom != null) {
          classChooserDialog.setItems(classesToChooseFrom);
        }
        classChooserDialog.setSubmitHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            List<String> selected = classChooserDialog.getSelectedItems();
            for (String classId : selected) {
              addClass(classId);
            }
            classChooserDialog.hide();
          }
        });
        classChooserDialog.show();
      }
    });
    
    this.addAuthorsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        authorChooserDialog.setItems(authorsToChooseFrom);
        authorChooserDialog.setSubmitHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            List<String> selected = authorChooserDialog.getSelectedItems();
            for (String author : selected) {
              addAuthor(author);
            }
            authorChooserDialog.hide();
          }
        });
        authorChooserDialog.show();
      }
    });
    
    // Clicking save sends updates to the server. 
    this.saveButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        assert formIsInitialized : "You must call initializeForm(authToken, serverLocation) before submitting";
        // server expects a comma-delimited lists of classes and authors
        classHiddenField.setValue(getCampaignClassesSerialized());
        authorHiddenField.setValue(getCampaignAuthorsSerialized());
        // TODO: validate form. 
        // - if this is a create, there must be an xml file
        // - for both edit and create, there must be at least one class
        formPanel.submit();
      }
    });
    
    // Cancel button clears form fields and returns to previous page
    this.cancelButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        clearFormFields();
        History.back();
      }
    });
  }
  
  // must be called before submitting
  public void initializeForm(String authToken, String serverLocation) {
    this.authTokenHiddenField.setValue(authToken);
    this.formPanel.setAction(serverLocation);
    this.formPanel.setEncoding(FormPanel.ENCODING_MULTIPART); // needed for file upload 
    this.formPanel.setMethod(FormPanel.METHOD_POST);
    this.formIsInitialized = true;
  }

  private void clearFormFields() {
    // clear all the ordinary form input elements
    this.formPanel.reset();
    // clear display-only fields that were auto-filled from xml
    this.campaignName.setText("");
    this.campaignUrn.setText("");
    // clear class and author lists 
    this.classesFlexTable.clear();
    this.authorsFlexTable.clear();
    // class and author tables are invisible when empty so their add
    // buttons render on the same line as the label
    this.classesFlexTable.setVisible(false);
    this.authorsFlexTable.setVisible(false);
  }
  
  public String getCampaignId() {
    return this.campaignUrn.getText();
  }
  
  public void setCampaign(CampaignDetailedInfo campaign) {
    if (campaign != null) {
      
      // some fields are only editable when creating new campaign
      setIsNewCampaignFlag(false);
      header.setText("Editing " + campaign.getCampaignName());
      
      // general info
      this.campaignName.setText(campaign.getCampaignName());
      this.campaignUrn.setText(campaign.getCampaignId());
      this.campaignUrnHiddenField.setValue(campaign.getCampaignId());
      this.campaignDescriptionTextArea.setText(campaign.getDescription());
      
      // classes
      this.setCampaignClasses(campaign.getClasses());
      
      // authors
      this.setCampaignAuthors(campaign.getAuthors());
      
      // privacy
      if (campaign.getPrivacy().equals(Privacy.PRIVATE)) {
        privacyListBox.setSelectedIndex(0);
      } else if (campaign.getPrivacy().equals(Privacy.SHARED)) {
        privacyListBox.setSelectedIndex(1);
      } else if (campaign.getPrivacy().equals(Privacy.INVISIBLE)) {
        privacyListBox.setSelectedIndex(2);
      }
    
      // whether campaign is running
      if (campaign.getRunningState().equals(RunningState.STOPPED)) {
        this.runningStateListBox.setSelectedIndex(0);
      } else if (campaign.getRunningState().equals(RunningState.RUNNING)) {
        this.runningStateListBox.setSelectedIndex(1);
      }
      
    }
  }
  
  // affects display and saving since some fields can only
  // be edited when creating a new campaign and form needs to
  // be submitted to a different url for create than for update
  public void setIsNewCampaignFlag(boolean isNew) {
    this.isNewCampaign = isNew;
    if (isNew) {
      clearFormFields();
      this.deletePanel.setVisible(false);
      this.header.setText("Creating New Campaign");
    } else { // editing existing campaign
      this.deletePanel.setVisible(true);
    }
  }
  
  public HasClickHandlers getSaveButton() {
    return this.saveButton;
  }
  
  public HasClickHandlers getCancelButton() {
    return this.cancelButton;
  }
  
  public HasClickHandlers getDeleteButton() {
    return this.deleteButton;
  }
  
  public String getDescription() {
    return this.campaignDescriptionTextArea.getText();
  }

  // FIXME: return type Privacy 
  public String  getPrivacy() {
    int selected = this.privacyListBox.getSelectedIndex();
    return this.privacyListBox.getValue(selected);
  }

  // FIXME: return type RunningState
  public String getRunningState() {
    int selected = this.runningStateListBox.getSelectedIndex();
    return this.runningStateListBox.getValue(selected);
  }
  
  /****************** ADD/REMOVE CLASSES ******************/
  
  public String getCampaignClassesSerialized() {
    List<String> list = getCampaignClasses();
    StringBuilder sb = new StringBuilder();
    if (list.size() > 0) {
      sb.append(list.get(0)); // no comma before first urn 
      for (int i = 1; i < list.size(); i++) {
        sb.append(",").append(list.get(i)); 
      }
    }
    return sb.toString();
  }
  
  public List<String> getCampaignClasses() {
    ArrayList<String> classes = new ArrayList<String>();
    for (int i = 0; i < this.classesFlexTable.getRowCount(); i++) {
      // assumes text displayed to the user is also participant id
      classes.add(this.classesFlexTable.getText(i, 0));
    }
    return classes;
  }
  
  public void setCampaignClasses(List<String> classes) {
    this.classesFlexTable.clear(); 
    this.classesFlexTable.removeAllRows();
    
    // invisible when no participants so add button will line up with label
    this.classesFlexTable.setVisible(classes.size() > 0);
    for (int i = 0; i < classes.size(); i++) {
      addClass(classes.get(i));
    }
    
  }

  private void addClass(final String campaignClass) {
    if (campaignClass == null) return;
    else this.classesFlexTable.setVisible(true);
    
    // check for duplicates
    boolean isAlreadyInTable = false;
    int firstEmptyRowIndex = this.classesFlexTable.getRowCount();
    for (int i = 0; i < firstEmptyRowIndex; i++) {
      if (this.classesFlexTable.getText(i, 0).equals(campaignClass)) {
        isAlreadyInTable = true;
        break;
      }
    }
    
    // if class is not already in table, add new row with two columns 
    // 0 = class identifier, 1 = "x" button that deletes the row when clicked
    if (!isAlreadyInTable) {
      final int thisRow = firstEmptyRowIndex;
      classesFlexTable.setText(thisRow, 0, campaignClass);
      Button deleteButton = new Button("X");
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          classesFlexTable.removeRow(thisRow);
          classesFlexTable.setVisible(classesFlexTable.getRowCount() > 0);
        }
      });
      classesFlexTable.setWidget(thisRow, 1, deleteButton);
    }
  }
  
  // when adding classes, author selects from this list
  public void setClassListToChooseFrom(Map<String, String> classes) {
    this.classesToChooseFrom = classes != null ? classes : new HashMap<String, String>();
  }

  /****************** ADD/REMOVE AUTHORS ******************/
  
  
  public String getCampaignAuthorsSerialized() {
    List<String> list = getCampaignAuthors();
    StringBuilder sb = new StringBuilder();
    if (list.size() > 0) {
      sb.append(list.get(0));
      for (int i = 1; i < list.size(); i++) {
        sb.append(",").append(list.get(i));
      }
    }
    return sb.toString();
  }
  
  public List<String> getCampaignAuthors() {
    ArrayList<String> authors = new ArrayList<String>();
    for (int i = 0; i < this.authorsFlexTable.getRowCount(); i++) {
      // assumes text displayed to the user is also author id
      authors.add(this.authorsFlexTable.getText(i, 0));
    }
    return authors;
  }
  
  public void setCampaignAuthors(List<String> authors) {
    this.authorsFlexTable.clear(); 
    this.authorsFlexTable.removeAllRows();
    
    // invisible when author list is empty so add button will line up with label
    this.authorsFlexTable.setVisible(authors.size() > 0);
    for (int i = 0; i < authors.size(); i++) {
      addAuthor(authors.get(i));
    }
    
  }

  private void addAuthor(final String author) {
    if (author == null) return;
    else this.authorsFlexTable.setVisible(true);
    
    // check for duplicates
    boolean isAlreadyInTable = false;
    int firstEmptyRowIndex = this.authorsFlexTable.getRowCount();
    for (int i = 0; i < firstEmptyRowIndex; i++) {
      if (this.authorsFlexTable.getText(i, 0).equals(author)) {
        isAlreadyInTable = true;
        break;
      }
    }
    
    // if author is not already in table, add new row with two columns 
    // 0 = author identifier, 1 = "x" button that deletes the row when clicked
    if (!isAlreadyInTable) {
      final int thisRow = firstEmptyRowIndex;
      authorsFlexTable.setText(thisRow, 0, author);
      Button deleteButton = new Button("X");
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          authorsFlexTable.removeRow(thisRow);
          authorsFlexTable.setVisible(authorsFlexTable.getRowCount() > 0);
        }
      });
      authorsFlexTable.setWidget(thisRow, 1, deleteButton);
    }
  }
  
  // when adding Authors, author selects from this list
  public void setAuthorListToChooseFrom(List<String> authors) {
    this.authorsToChooseFrom = authors != null ? authors : new ArrayList<String>();
  }
  
  
}
