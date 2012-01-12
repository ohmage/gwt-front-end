package edu.ucla.cens.mobilize.client.view;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.common.RoleClass;
import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class AdminClassDetailView extends Composite {

  private static AdminClassDetailViewUiBinder uiBinder = GWT
      .create(AdminClassDetailViewUiBinder.class);

  interface AdminClassDetailViewUiBinder extends
      UiBinder<Widget, AdminClassDetailView> {
  }
  
  interface AdminClassDetailViewStyle extends CssResource {
    String campaignLink();
    String emptyFieldMsg();
    String memberLink();
    String memberLinkPrivileged();
  }

  @UiField AdminClassDetailViewStyle style;
  @UiField InlineHyperlink actionLinkEditClass;
  @UiField Anchor actionLinkUploadRoster;
  @UiField Anchor actionLinkDownloadRoster;
  @UiField Anchor actionLinkDeleteClass;
  @UiField Anchor backLink;
  @UiField InlineLabel classUrnField;
  @UiField InlineLabel classNameField;
  @UiField InlineLabel descriptionField; // FIXME textarea?
  @UiField Grid membersGrid;
  @UiField Grid campaignsGrid;
  
  public AdminClassDetailView() {
    initWidget(uiBinder.createAndBindUi(this));
    this.backLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        History.back();
      }
    });
  }
  
  public void setClassUrn(String classUrn) {
    this.classUrnField.setText(classUrn);
    this.actionLinkEditClass.setTargetHistoryToken(HistoryTokens.adminClassEdit(classUrn));
  }

  public void setClassName(String className) {
    this.classNameField.setText(className);
  }
  
  public void setDescription(String description) {
    if (description != null && !description.isEmpty()) {
      this.descriptionField.setText(description);
      this.descriptionField.removeStyleName(style.emptyFieldMsg());
    } else {
      this.descriptionField.setText("---");
      this.descriptionField.addStyleName(style.emptyFieldMsg());
    }
  }
  
  public void setMembers(List<String> usernames) {
    Collections.sort(usernames);
    int numUsers = usernames.size();
    int numColumns = 4;
    int numUsersPerColumn = numUsers / numColumns;
    this.membersGrid.resize(numUsersPerColumn + 1, numColumns);
    this.membersGrid.clear(); // remove any leftover links
    int currRow = 0;
    int currCol = 0;
    for (String username : usernames) {
      Hyperlink usernameLink = new Hyperlink(username, HistoryTokens.adminUserDetail(username));
      this.membersGrid.setWidget(currRow++, currCol, usernameLink);
      if (currRow > numUsersPerColumn) {
        currRow = 0;
        currCol++;
      }
    }
  }
  
  public void setCampaigns(Map<String, String> campaignIdToNameMap) {
    List<String> campaignIds = MapUtils.getKeysSortedByValues(campaignIdToNameMap);
    this.campaignsGrid.resize(campaignIds.size(), 1);
    int currRow = 0;
    for (String campaignId : campaignIds) {
      String campaignName = campaignIdToNameMap.get(campaignId);
      Hyperlink campaignLink = new Hyperlink(campaignName, HistoryTokens.campaignDetail(campaignId));
      campaignLink.addStyleName(style.campaignLink());
      this.campaignsGrid.setWidget(currRow++, 0, campaignLink);
    }
  }
  
  public String getClassUrn() {
    return this.classUrnField.getText();
  }

  public HasClickHandlers getUploadRosterLink() {
    return this.actionLinkUploadRoster;
  }
  
  public HasClickHandlers getDownloadRosterLink() {
    return this.actionLinkDownloadRoster;
  }
  
  public HasClickHandlers getDeleteClassLink() {
    return this.actionLinkDeleteClass;
  }

  public void showError(String msg, String detail) {
    ErrorDialog.show(msg, detail);
  }
  
  public void showClassRosterUploadDialog(String requestUrl,
                                          String authToken, 
                                          String client,
                                          SubmitCompleteHandler submitCompleteHandler) {
    
    final DialogBox dialog = new DialogBox(true);
    dialog.setText("Class Roster Upload");
    FormPanel form = new FormPanel();
    FlowPanel panel = new FlowPanel();
    Hidden authTokenHidden = new Hidden();
    Hidden clientHidden = new Hidden();
    final Label msg = new Label("Please select a file.");
    msg.addStyleName("formFieldInvalid");
    msg.setVisible(false);
    final FileUpload fileUpload = new FileUpload();
    Button cancelButton = new Button("Cancel");
    
    // set names of fields to match API arguments
    authTokenHidden.setName("auth_token");
    clientHidden.setName("client");
    fileUpload.setName("roster");
    
    // arguments that aren't edited by user are submitted as hidden fields
    authTokenHidden.setValue(authToken);
    clientHidden.setValue(client);
    
    form.setAction(requestUrl);
    form.setEncoding(FormPanel.ENCODING_MULTIPART);
    form.setMethod(FormPanel.METHOD_POST);
    form.addSubmitHandler(new SubmitHandler() {
      @Override
      public void onSubmit(SubmitEvent event) {
        msg.setVisible(false);
        if (fileUpload.getFilename().isEmpty()) {
          msg.setVisible(true);
          event.cancel();
        }
      }
    });
    
    form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
      @Override
      public void onSubmitComplete(SubmitCompleteEvent event) {
        dialog.hide();
      }
    });
    
    form.addSubmitCompleteHandler(submitCompleteHandler);
    
    cancelButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        dialog.hide();
      }
    });
    
    panel.add(authTokenHidden);
    panel.add(clientHidden);
    panel.add(msg);
    panel.add(fileUpload);
    panel.add(new SubmitButton("Upload"));
    panel.add(cancelButton);
    
    form.add(panel);
    dialog.add(form);
    dialog.center();
  }
  
}
