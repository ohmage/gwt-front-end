package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;

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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.common.RunningState;
import edu.ucla.cens.mobilize.client.model.CampaignDetailedInfo;

public class CampaignEditForm extends Composite {

  private static CampaignEditFormUiBinder uiBinder = GWT
      .create(CampaignEditFormUiBinder.class);

  interface CampaignEditFormUiBinder extends UiBinder<Widget, CampaignEditForm> {
  }

  @UiField Label header;
  @UiField InlineLabel campaignName;
  @UiField TextArea campaignDescriptionTextArea;
  @UiField Button addParticipantsButton;
  @UiField FlexTable participantsFlexTable;
  @UiField Hidden classHiddenField;
  @UiField FileUpload chooseFileButton;
  @UiField ListBox runningStateListBox;
  @UiField ListBox privacyListBox;
  @UiField Button saveButton;
  @UiField Button cancelButton;
  @UiField Button deleteButton;
  @UiField HTMLPanel deletePanel;

  @UiField FormPanel form;

  boolean isNewCampaign;
  String campaignId;
  
  // dialog that lets user select groups of participants (i.e., classes)
  ParticipantChooserDialog dialog;
  List<String> participantsToChooseFrom = new ArrayList<String>();
  
  public CampaignEditForm() {
    initWidget(uiBinder.createAndBindUi(this));
    
    dialog = new ParticipantChooserDialog();

    // populate list boxes
    // TODO: get allowed privacy states from campaign config    
    runningStateListBox.addItem("Stopped"); // 0 = stopped
    runningStateListBox.addItem("Running"); // 1 = running
    privacyListBox.addItem("Private"); // 0 = private
    privacyListBox.addItem("Shared"); // 1 = shared/public
    // if ... addItem("INVISIBLE")

    // form element with multipart mime type, needed for file upload input to work
    form.setAction("http://localhost:8000/MobilizeWeb/upload");
    form.setEncoding(FormPanel.ENCODING_MULTIPART); 
    form.setMethod(FormPanel.METHOD_POST); 
    
    // invisible when no participants so add button lines up with label
    participantsFlexTable.setVisible(false);
    participantsFlexTable.setCellSpacing(0);
    participantsFlexTable.setBorderWidth(0);
    
    bind();
  }
  
  private void bind() {

    // Add Participants button opens a dialog showing a list of participants.
    // Selected names are saved to the participant list on dialog submit.
    this.addParticipantsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialog.setParticipantsToChooseFrom(participantsToChooseFrom);
        dialog.setSubmitHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            List<String> selected = dialog.getSelectedParticipants();
            for (String participant : selected) {
              addParticipant(participant);
            }
            dialog.hide();
          }
        });
        dialog.show();
      }
    });
    
    // Clicking save sends updates to the server. 
    this.saveButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        // server expects a comma-delimited list of classes
        classHiddenField.setValue(getCampaignParticipantsSerialized());
        // TODO: validate form. 
        // - if this is a create, there must be an xml file
        // - for both edit and create, there must be at least one participant
        form.submit();
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
  
  // TODO: some way of keeping track what's changed?

  private void clearFormFields() {
    this.campaignName.setText("");
    this.campaignDescriptionTextArea.setText("");
    this.participantsFlexTable.clear();
    // participants table is invisible when there are no participants
    // so add button renders on the same line as the label
    this.participantsFlexTable.setVisible(false);
    this.privacyListBox.setSelectedIndex(0);
    this.runningStateListBox.setSelectedIndex(0);
  }
  
  public String getCampaignId() {
    return this.campaignId;
  }
  
  public void setCampaign(CampaignDetailedInfo campaign) {
    if (campaign != null) {
      this.campaignId = campaign.getCampaignId();
      
      // some fields are only editable when creating new campaign
      setIsNewCampaignFlag(false);
      header.setText("Editing " + campaign.getCampaignName());
      
      // general info
      this.campaignName.setText(campaign.getCampaignName());
      this.campaignDescriptionTextArea.setText(campaign.getDescription());
      
      // participants
      this.setCampaignParticipants(campaign.getParticipantGroups());
      
      // privacy
      if (campaign.getPrivacy().equals(Privacy.PRIVATE)) {
        privacyListBox.setSelectedIndex(0);
      } else if (campaign.getPrivacy().equals(Privacy.PUBLIC)) {
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
  // be edited when creating a new campaign
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
  
  public String getCampaignParticipantsSerialized() {
    List<String> list = getCampaignParticipants();
    StringBuilder sb = new StringBuilder();
    if (list.size() > 0) {
      sb.append(list.get(0));
      for (int i = 1; i < list.size(); i++) {
        sb.append(",").append(list.get(i));
      }
    }
    return sb.toString();
  }
  
  public List<String> getCampaignParticipants() {
    ArrayList<String> participants = new ArrayList<String>();
    for (int i = 0; i < this.participantsFlexTable.getRowCount(); i++) {
      // assumes text displayed to the user is also participant id
      participants.add(this.participantsFlexTable.getText(i, 0));
    }
    return participants;
  }
  
  public void setCampaignParticipants(List<String> participants) {
    this.participantsFlexTable.clear(); // FIXME: does this work?
    this.participantsFlexTable.removeAllRows();
    
    // invisible when no participants so add button will line up with label
    this.participantsFlexTable.setVisible(participants.size() > 0);
    for (int i = 0; i < participants.size(); i++) {
      addParticipant(participants.get(i));
    }
    
  }

  private void addParticipant(final String participant) {
    if (participant == null) return;
    else this.participantsFlexTable.setVisible(true);
    
    // check for duplicates
    boolean isAlreadyInTable = false;
    int firstEmptyRowIndex = this.participantsFlexTable.getRowCount();
    for (int i = 0; i < firstEmptyRowIndex; i++) {
      if (this.participantsFlexTable.getText(i, 0).equals(participant)) {
        isAlreadyInTable = true;
        break;
      }
    }
    
    // if participant is not already in table, add new row with two columns 
    // 0 = participant name, 1 = "x" button that deletes the row when clicked
    if (!isAlreadyInTable) {
      final int thisRow = firstEmptyRowIndex;
      participantsFlexTable.setText(thisRow, 0, participant);
      Button deleteButton = new Button("X");
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          participantsFlexTable.removeRow(thisRow);
          participantsFlexTable.setVisible(participantsFlexTable.getRowCount() > 0);
        }
      });
      participantsFlexTable.setWidget(thisRow, 1, deleteButton);
    }
  }
  
  // when adding participants, author selects from this list
  public void setParticipantsToChooseFrom(List<String> participants) {
    this.participantsToChooseFrom = participants != null ? participants : new ArrayList<String>();
  }
  
}
