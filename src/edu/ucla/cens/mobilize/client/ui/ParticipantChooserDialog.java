package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ParticipantChooserDialog {

  private static ParticipantChooserDialogUiBinder uiBinder = GWT
      .create(ParticipantChooserDialogUiBinder.class);

  @UiField DialogBox dialog;
  @UiField ListBox participantsToChooseFromListBox;
  @UiField Button addButton;
  @UiField Button cancelButton;
  
  interface ParticipantChooserDialogUiBinder extends
      UiBinder<Widget, ParticipantChooserDialog> {
  }

  public ParticipantChooserDialog() {
    uiBinder.createAndBindUi(this);
    ParticipantChooserDialogUiBinder uiBinder = GWT.create(ParticipantChooserDialogUiBinder.class);
    uiBinder.createAndBindUi(this);
    dialog.setGlassEnabled(true);
    dialog.hide();
    bind();
  }
  
  private void bind() {
    cancelButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialog.hide();
      }
    });
  }
  
  public void setParticipantsToChooseFrom(List<String> participants) {
    participantsToChooseFromListBox.clear();
    for (String participant : participants) {
      // NOTE: value == text here. We may want value to be id instead.
      participantsToChooseFromListBox.addItem(participant, participant);
    }
  }
  
  public void setSubmitHandler(ClickHandler handler) {
    this.addButton.addClickHandler(handler);
  }

  public List<String> getSelectedParticipants() {
    List<String> selected = new ArrayList<String>();
    for (int i = 0; i < participantsToChooseFromListBox.getItemCount(); i++) {
      if (participantsToChooseFromListBox.isItemSelected(i)) {
        selected.add(participantsToChooseFromListBox.getValue(i));
      }
    }
    return selected;
  }
  
  public void show() {
    dialog.center();
  }
  
  public void hide() {
    dialog.hide();
  }
}
