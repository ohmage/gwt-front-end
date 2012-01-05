package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.common.RoleCampaign;
import edu.ucla.cens.mobilize.client.utils.CollectionUtils;

public class CampaignEditFormView extends Composite {

  private static CampaignEditViewUiBinder uiBinder = GWT
      .create(CampaignEditViewUiBinder.class);

  @UiTemplate("CampaignEditFormView.ui.xml")
  interface CampaignEditViewUiBinder extends UiBinder<Widget, CampaignEditFormView> {
  }

  @UiField Label header;
  @UiField Hidden authTokenHiddenField;
  @UiField Hidden clientHiddenField;
  @UiField InlineLabel campaignName;
  @UiField InlineLabel campaignUrn;
  @UiField Hidden campaignUrnHiddenField;
  @UiField TextArea campaignDescriptionTextArea;
  @UiField Button addClassesButton;
  @UiField FlexTable classesFlexTable;
  @UiField Hidden classHiddenField; // holds serialized class list
  @UiField Hidden classAddHiddenField; // holds serialized list of classes to add
  @UiField Hidden classDelHiddenField; // holds serialized list of classes to delete
  @UiField HTMLPanel authorsPanel;
  @UiField Button addAuthorsButton;
  @UiField Label authorsPlaceholderText;
  @UiField FlexTable authorsFlexTable;
  @UiField Hidden authorsToAddHiddenField; // list of authors with roles
  @UiField Hidden authorsToRemoveHiddenField; // list of authors with roles
  @UiField FileUpload fileInput;
  @UiField ListBox runningStateListBox;
  @UiField ListBox privacyListBox;
  @UiField Button saveButton;
  @UiField Button cancelButton;
  @UiField Button deleteButton;
  @UiField HTMLPanel deletePanel;
  @UiField HTMLPanel fileInputContainer; // so file input can be removed/reattached

  @UiField MessageWidget messageWidget;
  @UiField FormPanel formPanel;  

  private final int CLASS_NAME_COL = 0;
  private final int CLASS_DELETE_COL = 1;
  private final int CLASS_URN_COL = 2;
  private final int AUTHOR_LOGIN_COL = 0;
  private final int AUTHOR_DELETE_COL = 1;

  private boolean formIsInitialized = false;
  private List<String> originalAuthors;
  private List<String> originalClasses;
  
  public CampaignEditFormView() {
    initWidget(uiBinder.createAndBindUi(this));

    // default privacy/running states (can also be set by presenter)
    setRunningStateChoices(Arrays.asList(new RunningState[] {RunningState.STOPPED, RunningState.RUNNING }));
    setPrivacyChoices(Arrays.asList(new Privacy[] { Privacy.PRIVATE, Privacy.SHARED }));
    
    // invisible when empty so add button lines up with label
    classesFlexTable.setVisible(false);
    classesFlexTable.setCellSpacing(0);
    classesFlexTable.setBorderWidth(0);
    
    // invisible when empty so add button lines up with label
    authorsFlexTable.setVisible(false);
    authorsFlexTable.setCellSpacing(0);
    authorsFlexTable.setBorderWidth(0);
  }
  
  public void prepareFormForSubmit(boolean isCreate) {
	  // NOTE(06/15/2011): create api does not support adding authors
	  // NOTE(11/02/2011): edit api does not have class_urn_list api, but add/remove instead
	  if (isCreate == false) { // this is an edit
        authorsToAddHiddenField.setName("user_role_list_add");
        authorsToRemoveHiddenField.setName("user_role_list_remove");
        authorsToAddHiddenField.setValue(getAuthorsToAddSerialized());
        authorsToRemoveHiddenField.setValue(getAuthorsToRemoveSerialized());
        classAddHiddenField.setValue(getClassesToAddSerialized());
        classDelHiddenField.setValue(getClassesToRemoveSerialized());
        clearOriginalClasses();	//FIXME: is it safe to clear the old list here before we know everything submitted?
    } else { // campaign creation
    	classHiddenField.setValue(getClassUrnsSerialized());
    }
    
    campaignUrnHiddenField.setValue(this.campaignUrn.getText());
    
    // Disable author hidden fields when empty to prevent api errors. (Fields would
    // be empty if this is a create or if there were no changes to author list.)
    if (authorsToAddHiddenField.getValue().isEmpty()) authorsToAddHiddenField.getElement().removeAttribute("name");
    if (authorsToRemoveHiddenField.getValue().isEmpty()) authorsToRemoveHiddenField.getElement().removeAttribute("name");  
  }

  public void setAuthorsPanelVisible(boolean isVisible) {
    //if isVisible == true, display author drop-down list, else display placeholder text
    this.authorsPlaceholderText.setVisible(!isVisible);
    this.authorsPanel.setVisible(isVisible);
  }

  private void addClass(String classUrn, String className) {
    if (classUrn == null) return;
    else this.classesFlexTable.setVisible(true);
    
    // check for duplicates
    boolean isAlreadyInTable = false;
    int firstEmptyRowIndex = this.classesFlexTable.getRowCount();
    for (int i = 0; i < firstEmptyRowIndex; i++) {
      if (this.classesFlexTable.getText(i, CLASS_NAME_COL).equals(className)) {
        isAlreadyInTable = true;
        break;
      }
    }
    
    // if class is not already in table, add new row with two columns 
    // 0 = user friendly class name 
    // 1 = "x" button that deletes the row when clicked,
    // 2 = invisible class urn (this is what gets serialized and submitted)
    if (!isAlreadyInTable) {
		//first, disable all current delete "X" buttons, b/c rows are LIFO
	    for (int i = 0; i < this.classesFlexTable.getRowCount(); i++) {
	      try {
	        Button b = (Button) this.classesFlexTable.getWidget(i, CLASS_DELETE_COL);
	        b.setVisible(false);
	        b.setEnabled(false);
	      } catch (IndexOutOfBoundsException e) {
	        //TODO: Handle error
	      }
	    }
      
      final int thisRow = firstEmptyRowIndex;
      classesFlexTable.setText(thisRow, CLASS_NAME_COL, className);
      Button deleteButton = new Button("X");
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          classesFlexTable.removeRow(thisRow);
          
          //enable only last delete button
          try {
            Button b = (Button) classesFlexTable.getWidget(classesFlexTable.getRowCount()-1, CLASS_DELETE_COL);
            b.setVisible(true);
            b.setEnabled(true);
          } catch (IndexOutOfBoundsException e) {
            //TODO: Handle error
          }
          
          classesFlexTable.setVisible(classesFlexTable.getRowCount() > 0);
        }
      });
      classesFlexTable.setWidget(thisRow, CLASS_DELETE_COL, deleteButton);
      classesFlexTable.setText(thisRow, CLASS_URN_COL, classUrn);
      classesFlexTable.getCellFormatter().setVisible(thisRow, CLASS_URN_COL, false);
    }
  }

  public List<String> getClassUrns() {
    List<String> urns = new ArrayList<String>();
    for (int i = 0; i < this.classesFlexTable.getRowCount(); i++) {
      urns.add(this.classesFlexTable.getText(i, CLASS_URN_COL));
    }
    return urns;
  }
  
  private String getClassUrnsSerialized() {
    return CollectionUtils.join(getClassUrns(), ",");
  }
  
  public void setAuthorPlaceholderText(String authorLogin) {
	  if (authorLogin == null) return;
	  this.authorsPlaceholderText.setText(authorLogin);
	  this.authorsPlaceholderText.setVisible(true);
  }
  
  public void addAuthor(String authorLogin) {
    if (authorLogin == null) return;
    else this.authorsFlexTable.setVisible(true);
    
    // check for duplicates
    boolean isAlreadyInTable = false;
    int firstEmptyRowIndex = this.authorsFlexTable.getRowCount();
    for (int i = 0; i < firstEmptyRowIndex; i++) {
      if (this.authorsFlexTable.getText(i, AUTHOR_LOGIN_COL).equals(authorLogin)) {
        isAlreadyInTable = true;
        break;
      }
    }
    
    // if author is not already in table, add new row with two columns 
    // 0 = author identifier, 1 = "x" button that deletes the row when clicked
    if (!isAlreadyInTable) {
      //first, disable all current delete "X" buttons, b/c rows are LIFO
      for (int i = 0; i < this.authorsFlexTable.getRowCount(); i++) {
        try {
          Button b = (Button) this.authorsFlexTable.getWidget(i, AUTHOR_DELETE_COL);
          b.setVisible(false);
          b.setEnabled(false);
        } catch (IndexOutOfBoundsException e) {
          //TODO: Handle error
        }
      }
    
      final int thisRow = firstEmptyRowIndex;
      authorsFlexTable.setText(thisRow, AUTHOR_LOGIN_COL, authorLogin);
      Button deleteButton = new Button("X");
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          authorsFlexTable.removeRow(thisRow);
          
          //enable only last delete button
          try {
            Button b = (Button) authorsFlexTable.getWidget(authorsFlexTable.getRowCount()-1, AUTHOR_DELETE_COL);
            b.setVisible(true);
            b.setEnabled(true);
          } catch (IndexOutOfBoundsException e) {
            //TODO: Handle error
          }
          
          authorsFlexTable.setVisible(authorsFlexTable.getRowCount() > 0);
        }
      });
      authorsFlexTable.setWidget(thisRow, AUTHOR_DELETE_COL, deleteButton);
    }
  }
  
  public List<String> getSelectedAuthors() {
    ArrayList<String> authors = new ArrayList<String>();
    for (int i = 0; i < this.authorsFlexTable.getRowCount(); i++) {
      // assumes text displayed to the user is also author id
      authors.add(this.authorsFlexTable.getText(i, AUTHOR_LOGIN_COL));
    }
    return authors;
  }
  
  // Author list will be diffed against this when constructing server
  // query because author adds and author removes are different params.
  // Presenter must set this when campaign is first loaded for editing.
  public void storeOriginalAuthors(List<String> authorLogins) {
    this.originalAuthors = authorLogins;
  }
  
  private Collection<String> getAuthorsToAdd() {
    List<String> selectedAuthors = getSelectedAuthors();
    return CollectionUtils.setDiff(selectedAuthors, this.originalAuthors);
  }
  
  private String getAuthorsToAddSerialized() {
    Collection<String> authorsToAdd = getAuthorsToAdd();
    Collection<String> authorsToAddWithRoles = new ArrayList<String>();
    for (String authorLogin : authorsToAdd) {
      authorsToAddWithRoles.add(authorLogin + ";" + RoleCampaign.AUTHOR.toServerString());
    }
    return CollectionUtils.join(authorsToAddWithRoles, ",");
  }
  
  private Collection<String> getAuthorsToRemove() {
    List<String> selectedAuthors = getSelectedAuthors();
    return CollectionUtils.setDiff(this.originalAuthors, selectedAuthors);
  }
  
  private String getAuthorsToRemoveSerialized() {
    Collection<String> authorsToRemove = getAuthorsToRemove();
    Collection<String> authorsToRemoveWithRoles = new ArrayList<String>();
    for (String authorLogin : authorsToRemove) {
      authorsToRemoveWithRoles.add(authorLogin + ";" + RoleCampaign.AUTHOR.toServerString());
    }
    return CollectionUtils.join(authorsToRemoveWithRoles, ",");
  }
  
  // Classes list needs to be diffed when doing a campaign update because
  // class add/remove are separate parameters sent to Campaign Update API 2.8+
  public void storeOriginalClasses(List<String> classUrnList) {
	  this.originalClasses = classUrnList;
  }
  private void clearOriginalClasses() {
	  this.originalClasses.clear();
  }
  private Collection<String> getClassesToAdd() {
    return CollectionUtils.setDiff(getClassUrns(), this.originalClasses);
  }
  private Collection<String> getClassesToRemove() {
    return CollectionUtils.setDiff(this.originalClasses, getClassUrns());
  }
  private String getClassesToAddSerialized() {
    return CollectionUtils.join(getClassesToAdd(), ",");
  }
  private String getClassesToRemoveSerialized() {
    return CollectionUtils.join(getClassesToRemove(), ",");
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

  public String getCampaignUrn() {
    return this.campaignUrn.getText();
  }

  public void setCampaignUrn(String urn) {
    this.campaignUrn.setText(urn);
  }
  
  public void setCampaignName(String campaignName) {
    this.campaignName.setText(campaignName);
  }

  public void setDescription(String description) {
    this.campaignDescriptionTextArea.setText(description);
  }

  public void setPrivacy(Privacy privacy) {
    String privacyServerString = privacy.toServerString();
    for (int i = 0; i < this.privacyListBox.getItemCount(); i++) {
      if (this.privacyListBox.getValue(i).equals(privacyServerString)) {
        this.privacyListBox.setSelectedIndex(i);
        break;
      }
    }
  }

  public void setRunningState(RunningState runningState) {
    String runningStateLower = runningState.toString().toLowerCase();
    for (int i = 0; i < this.runningStateListBox.getItemCount(); i++) {
      if (this.runningStateListBox.getItemText(i).toLowerCase().equals(runningStateLower)) {
        this.runningStateListBox.setSelectedIndex(i);
        break;
      }
    }
  }

  public HasClickHandlers getAddClassesButton() {
    return this.addClassesButton;
  }

  public HasClickHandlers getAddAuthorsButton() {
    return this.addAuthorsButton;
  }

  public void setSelectedClasses(Map<String, String> classUrnToClassNameMap) {
    this.classesFlexTable.removeAllRows();
    for (String classUrn : classUrnToClassNameMap.keySet()) {
      addClass(classUrn, classUrnToClassNameMap.get(classUrn));
    }
  }

  public void setSelectedAuthors(List<String> userLogins) {
    assert this.originalAuthors != null : "Save list of original authors before selecting authors.";
    this.authorsFlexTable.removeAllRows();
    for (String authorLogin : userLogins) {
      addAuthor(authorLogin);
    }
  }

  /**
   * Opens a dialog prompting the user to select class names from a list. On
   * close, the selected classes are added to the campaign class list.
   * @param classUrnToClassNameMap Classes the current user is allowed to pick from
   */
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
          addClass(classUrn, classUrnToClassNameMap.get(classUrn));
        }
        classChooserDialog.hide();
      }
    });
    classChooserDialog.show();
  }

  /**
   * Opens a dialog prompting user to select login names from a list. On 
   * close, the selected logins are added to the authors list.
   * @param userLogins Login names of users that could be selected as authors
   */
  public void showAuthorChoices(List<String> userLogins) {
    if (userLogins == null) return;
    final MultiSelectDialog authorChooserDialog = new MultiSelectDialog();
    authorChooserDialog.setCaption("Add co-authors to the campaign");
    authorChooserDialog.setItems(userLogins);
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

  /**
   * When user clicks delete button, a dialog pops up asking if 
   * they are sure. Clicking delete executes the handler passed 
   * to this function. Cancel closes the dialog with no effect.
   * @param onConfirmDelete Method to call on confirmation
   */
  public void showConfirmDelete(final ClickHandler onConfirmDelete) {
    final DialogBox dialog = new DialogBox();
    dialog.setGlassEnabled(true);
    dialog.setText("Are you sure you want to delete this campaign?");
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
  
  public void addSubmitHandler(SubmitHandler onSubmit) {
    this.formPanel.addSubmitHandler(onSubmit);
  }

  public void addSubmitCompleteHandler(SubmitCompleteHandler onSubmitComplete) {
    this.formPanel.addSubmitCompleteHandler(onSubmitComplete);
  }


  public void submitForm() {
    this.formPanel.submit();
  }
  
  public void clearFormFields() {
    // clear all the ordinary form input elements
    this.formPanel.reset();
    // clear display-only fields that were auto-filled from xml
    this.campaignName.setText("");
    this.campaignUrn.setText("");
    // clear class and author lists 
    this.classesFlexTable.removeAllRows();
    this.authorsFlexTable.removeAllRows();
    // class and author tables are invisible when empty so their add
    // buttons render on the same line as the label
    this.classesFlexTable.setVisible(false);
    this.authorsFlexTable.setVisible(false);
  }

  public void setPrivacyChoices(List<Privacy> privacyChoices) {
    this.privacyListBox.clear();
    for (Privacy privacyChoice : privacyChoices) {
      this.privacyListBox.addItem(privacyChoice.toUserFriendlyString(), privacyChoice.toServerString());
    }
  }
 
  public void setRunningStateChoices(List<RunningState> runningStateChoices) {
    this.runningStateListBox.clear();
    for (RunningState runningState : runningStateChoices) {
      this.runningStateListBox.addItem(runningState.toUserFriendlyString(), runningState.toServerString());
    }
  }

  public void setHeader(String headerText) {
    this.header.setText(headerText);
  }

  public void setDeletePanelVisible(boolean isVisible) {
    this.deletePanel.setVisible(isVisible);
  }

  public String getXmlFilename() {
    return this.fileInput.getFilename();
  }
  
  public void showValidationErrors(String errorMessage, List<String> errors) {
    ErrorDialog.showErrorList(errorMessage, errors);
  }
  
  public void clearValidationErrors() {
    this.messageWidget.hide();
  }
  
  public void disableSubmitButton() {
    this.saveButton.setEnabled(false);
  }
  
  public void enableSubmitButton() {
    this.saveButton.setEnabled(true);
  }
  

}
