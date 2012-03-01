package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;

import edu.ucla.cens.mobilize.client.AwConstants;
import edu.ucla.cens.mobilize.client.common.Privacy;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.ui.MessageWidget;
import edu.ucla.cens.mobilize.client.ui.ResponseDisplayWidget;
import edu.ucla.cens.mobilize.client.ui.ResponseWidgetBasic;
import edu.ucla.cens.mobilize.client.ui.AwSimplePager;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MapUtils;

public class ResponseViewImpl extends Composite implements ResponseView {
  
  private static ResponseViewUiBinder uiBinder = GWT
      .create(ResponseViewUiBinder.class);

  @UiTemplate("ResponseView.ui.xml")
  interface ResponseViewUiBinder extends UiBinder<Widget, ResponseViewImpl> {
  }

  public interface ResponseViewStyles extends CssResource {
    String disabled();
    String emptyResponseListMessage();
    String selectedTopNav();
    String waiting();
  }

  @UiField ResponseViewStyles style;
  @UiField HTMLPanel editResponsesMenuItem; // used for showing/hiding
  @UiField Anchor viewLinkEdit;
  @UiField Anchor viewLinkBrowse;
  @UiField MenuItem resultsPerPage10MenuItem;
  @UiField MenuItem resultsPerPage50MenuItem;
  @UiField MenuItem resultsPerPage100MenuItem;
  @UiField Label singleParticipantLabel;
  @UiField ListBox participantFilter;
  @UiField HTMLPanel optionalFilters;
  @UiField ListBox campaignFilter;
  @UiField ListBox surveyFilter;
  @UiField ListBox privacyFilter;
  @UiField DateBox fromDateBox;
  @UiField DateBox toDateBox;
  @UiField HTMLPanel onlyPhotoResponsesPanel;
  @UiField CheckBox onlyPhotoResponsesCheckBox;
  @UiField Button applyFiltersButton;
  @UiField MessageWidget messageWidget;
  @UiField Label sectionHeaderTitle;
  @UiField Label sectionHeaderDetail;
  @UiField HTMLPanel centerPanel;
  @UiField HTMLPanel scrollPanel;
  @UiField FlowPanel responseList;
  @UiField Button shareButtonTop;
  @UiField Button makePrivateButtonTop;
  @UiField Button deleteButtonTop;
  @UiField Button shareButtonBottom;
  @UiField Button makePrivateButtonBottom;
  @UiField Button deleteButtonBottom;
  @UiField Anchor selectAllLinkTop;
  @UiField Anchor selectNoneLinkTop;
  @UiField Anchor selectAllLinkBottom;
  @UiField Anchor selectNoneLinkBottom;
  @UiField HTMLPanel expandCollapseLinksTop;
  @UiField Anchor expandLinkTop;
  @UiField Anchor collapseLinkTop;
  @UiField HTMLPanel expandCollapseLinksBottom;
  @UiField Anchor expandLinkBottom;
  @UiField Anchor collapseLinkBottom;
  @UiField HTMLPanel buttonPanelTop;
  @UiField HTMLPanel buttonPanelBottom;
  @UiField(provided=true) AwSimplePager pager;
  @UiField(provided=true) AwSimplePager pagerBottom;
  
  ResponseView.Presenter presenter;
  Privacy selectedPrivacy = Privacy.UNDEFINED;
  private Subview selectedSubview;
  private String emptyParticipantListString = "None visible.";
  private int visibleRangeStart = 0;
  private int visibleRangeMaxLength;
  private int rowCount;
  private boolean rowCountIsExact = true;

  private FlowPanel loading;
  
  public ResponseViewImpl() {
    // instantiate pagers here so instructor params can be passed
    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class); 
    pager = new AwSimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
    pagerBottom = new AwSimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
    initWidget(uiBinder.createAndBindUi(this));
    initComponents();
    setEventHandlers();
  }
  
  private void initComponents() {
    // set up date pickers
    DateBox.Format fmt = new DateBox.DefaultFormat(DateUtils.getDateBoxDisplayFormat());
    fromDateBox.setFormat(fmt);
    toDateBox.setFormat(fmt);
    
    // set up pager
    pager.setDisplay(this);
    pager.setHeight("15px");
    pagerBottom.setDisplay(this);
    pagerBottom.setHeight("15px");
    setVisibleRangeMaxLength(10);
    
    // set up wait indicator
    loading = new FlowPanel();
    loading.setStyleName(style.waiting());
  }
  
  private void setEventHandlers() {
    
    selectAllLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectAll();
      }
    });
    
    selectNoneLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectNone();
      }
    });
   
    selectAllLinkBottom.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectAll();
      }
    });
    
    selectNoneLinkBottom.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        selectNone();
      }
    });
    
    expandLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        expandAll();
      }
    });
    
    expandLinkBottom.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        expandAll();
      }
    });
    
    collapseLinkTop.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        collapseAll();
      }
    });
    
    collapseLinkBottom.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        collapseAll();
      }
    });
    
    resultsPerPage10MenuItem.setCommand(new Command() {
      @Override
      public void execute() {
        setVisibleRangeMaxLength(10);
        setVisibleRange(visibleRangeStart, visibleRangeMaxLength);
      }
    });
    
    resultsPerPage50MenuItem.setCommand(new Command() {
      @Override
      public void execute() {
        setVisibleRangeMaxLength(50);
        setVisibleRange(visibleRangeStart, visibleRangeMaxLength);
      }
    });
    
    resultsPerPage100MenuItem.setCommand(new Command() {
      @Override
      public void execute() {
        setVisibleRangeMaxLength(100);
        setVisibleRange(visibleRangeStart, visibleRangeMaxLength);
      }
    });
  }


  private void selectAll() {
    for (int i = 0; i < responseList.getWidgetCount(); i++) {
        ResponseDisplayWidget widget = (ResponseDisplayWidget)responseList.getWidget(i);
        widget.setSelected(true); 
    }
  }
  
  private void selectNone() {
    for (int i = 0; i < responseList.getWidgetCount(); i++) {
      ResponseDisplayWidget widget = (ResponseDisplayWidget)responseList.getWidget(i);
      widget.setSelected(false); 
    }
  }
  
  private void expandAll() {
    for (int i = 0; i < responseList.getWidgetCount(); i++) {
      if (responseList.getWidget(i).getClass() == ResponseWidgetBasic.class) {
        ResponseDisplayWidget widget = (ResponseDisplayWidget)responseList.getWidget(i);
        widget.expand();
      }
    }
  }
  
  private void collapseAll() {
    for (int i = 0; i < responseList.getWidgetCount(); i++) {
      if (responseList.getWidget(i).getClass() == ResponseWidgetBasic.class) {
        ResponseDisplayWidget widget = (ResponseDisplayWidget)responseList.getWidget(i);
        widget.collapse();
      }
    }    
  }
  
  private void setExpandCollapseLinksVisible(boolean isVisible) {
    this.expandCollapseLinksTop.setVisible(isVisible);
    this.expandCollapseLinksBottom.setVisible(isVisible);
  }
  
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setParticipantList(SortedSet<String> participantNames, boolean makeFirstItemAll) {
    if (participantNames == null || participantNames.size() == 0) {
      singleParticipantLabel.setVisible(true);
      singleParticipantLabel.setText(this.emptyParticipantListString);
      participantFilter.setVisible(false);
    } else if (participantNames.size() == 1) {
      singleParticipantLabel.setVisible(true);
      singleParticipantLabel.setText(participantNames.first());
      participantFilter.setVisible(false);
    } else {
      singleParticipantLabel.setVisible(false);
      participantFilter.setVisible(true);
      participantFilter.clear();
      if (makeFirstItemAll) participantFilter.addItem("All", AwConstants.specialAllValuesToken);
      for (String name : participantNames) {
        participantFilter.addItem(name, name); 
      }
      // default is "All" if it's an option or no selection otherwise
      participantFilter.setSelectedIndex(makeFirstItemAll ? 0 : -1);
    }
  } 

  @Override
  public void setCampaignList(Map<String, String> campaignIdToNameMap) {
    campaignFilter.clear();
    if (campaignIdToNameMap == null) return;
    List<String> campaignIdsSortedByCampaignNames = MapUtils.getKeysSortedByValues(campaignIdToNameMap);
    for (String campaignId : campaignIdsSortedByCampaignNames) {
      campaignFilter.addItem(campaignIdToNameMap.get(campaignId), campaignId);
    }
    campaignFilter.setSelectedIndex(-1);
  }

  @Override
  public void setSurveyList(List<String> surveyNames) {
    surveyFilter.clear();
    if (surveyNames == null) return;
    surveyFilter.addItem("All", "");
    for (String name : surveyNames) {
      surveyFilter.addItem(name);
    }
  }
  
  @Override
  public void setPrivacyStates(List<Privacy> privacyStates) {
    privacyFilter.clear();
    if (privacyStates == null) return;
    privacyFilter.addItem("All", "");
    for (Privacy privacy : privacyStates) {
      privacyFilter.addItem(privacy.toUserFriendlyString(), privacy.toServerString());
    }
    
    // default is "All" if it's an option or no selection otherwise
    privacyFilter.setSelectedIndex(0);
  }

  @Override
  public void selectParticipant(String participantName) {
    for (int i = 0; i < participantFilter.getItemCount(); i++) {
      if (participantFilter.getValue(i).equals(participantName)) {
        participantFilter.setSelectedIndex(i);
        return;
      }
    }
    // if not found, select none
    participantFilter.setSelectedIndex(-1);
  }

  @Override
  public void selectCampaign(String campaignId) {
    for (int i = 0; i < campaignFilter.getItemCount(); i++) {
      if (campaignFilter.getValue(i).equals(campaignId)) {
        campaignFilter.setSelectedIndex(i);
        return;
      }
    }
    // if not found, select nothing
    campaignFilter.setSelectedIndex(-1);
  }

  @Override
  public void selectSurvey(String surveyName) {
    for (int i = 0; i < surveyFilter.getItemCount(); i++) {
      if (surveyFilter.getItemText(i).equals(surveyName)) {
        surveyFilter.setSelectedIndex(i);
        return;
      }
    }
    // if not found, select first item ("All")
    surveyFilter.setSelectedIndex(0);
  }
  
  @Override 
  public void selectPrivacyState(Privacy privacy) {
    if (privacy == null) {
      privacyFilter.setSelectedIndex(0);
      return;
    }
    
    String serverString = privacy.toServerString();
    for (int i = 0; i < privacyFilter.getItemCount(); i++) {
      if (privacyFilter.getValue(i).equals(serverString)) {
        privacyFilter.setSelectedIndex(i);
        return;
      }
    }
    // if not found, select first item ("All")
    privacyFilter.setSelectedIndex(0);
  }
  
  @Override
  public void selectStartDate(Date fromDate) {
    fromDateBox.setValue(fromDate);
  }

  @Override
  public void selectEndDate(Date toDate) {
    toDateBox.setValue(toDate);
  }
  
  @Override
  public void setPhotoFilter(boolean showOnlyResponsesWithPhotos) {
    onlyPhotoResponsesCheckBox.setValue(showOnlyResponsesWithPhotos);
  }

  @Override
  public void showWaitIndicator() {
    this.centerPanel.add(loading);
  }

  @Override
  public void hideWaitIndicator() {
    this.centerPanel.remove(loading);
  }
  
  @Override
  public void showNoPhotoResponsesMessage() {
    HTMLPanel panel = new HTMLPanel("The selected campaign has no photo prompts.");
    panel.setStyleName(style.emptyResponseListMessage());
    this.responseList.clear();
    this.responseList.add(panel);
  }
  
  @Override
  public int getVisibleRangeStart() {
    return this.visibleRangeStart;
  }
  
  @Override
  public void setVisibleRangeStart(int start) {
    this.visibleRangeStart = start;
  }
  
  public void renderResponses(List<SurveyResponse> responses) {
    if (Subview.EDIT.equals(selectedSubview)) {
      renderResponsesEditView(responses);
    } else if (Subview.BROWSE.equals(selectedSubview)) {
      renderResponsesBrowseView(responses);
    } else { // default to browse
      renderResponsesBrowseView(responses);
    }
    this.scrollPanel.getElement().setScrollTop(0);
  }
  
  private void renderResponsesEditView(List<SurveyResponse> responses) {
    if (this.responseList == null || responses == null) return;
    this.responseList.clear();
    for (SurveyResponse response : responses) {
      ResponseWidgetBasic responseWidget = new ResponseWidgetBasic();
      responseWidget.setSelectable(true);
      responseWidget.setResponse(response);
      this.responseList.add(responseWidget);
    }
  }
  
  private void renderResponsesBrowseView(List<SurveyResponse> responses) {
    if (this.responseList == null || responses == null) return;
    this.responseList.clear();
    for (SurveyResponse response : responses) {
      ResponseWidgetBasic responseWidget = new ResponseWidgetBasic();
      responseWidget.setSelectable(false);
      responseWidget.setResponse(response);
      this.responseList.add(responseWidget);
    }
  }

  private void setEditControlsVisible(boolean isVisible) {
    this.buttonPanelTop.setVisible(isVisible);
    this.buttonPanelBottom.setVisible(isVisible);
  }
  
  @Override
  public String getSelectedParticipant() {
    String selectedUser = null;
    if (this.singleParticipantLabel.isVisible()) {
      // when only one user is visible, that is the selected user
      selectedUser = this.singleParticipantLabel.getText();
      // check for special case where there are no participants
      if (selectedUser.equals(this.emptyParticipantListString)) {
        selectedUser = "";
      }
    } else { 
      // otherwise, get selection from dropdown
      int index = this.participantFilter.getSelectedIndex();
      selectedUser = (index > -1) ? this.participantFilter.getValue(index) : "";
    }
    return selectedUser;
  }

  @Override
  public String getSelectedCampaign() {
    int index = this.campaignFilter.getSelectedIndex();
    return (index > -1) ? this.campaignFilter.getValue(index) : "";
  }
  
  @Override
  public String getSelectedCampaignName() {
    int index = this.campaignFilter.getSelectedIndex();
    return (index > -1) ? this.campaignFilter.getItemText(index) : "";
  }

  @Override
  public String getSelectedSurvey() {
    int index = this.surveyFilter.getSelectedIndex();
    return (index > -1) ? this.surveyFilter.getValue(index) : "";
  }

  @Override
  public Privacy getSelectedPrivacyState() {
    int index = this.privacyFilter.getSelectedIndex();
    return (index > -1) ? Privacy.fromServerString(this.privacyFilter.getValue(index)) : null;
  }

  @Override
  public Date getSelectedStartDate() {
    return this.fromDateBox.getValue();
  }

  @Override
  public Date getSelectedEndDate() {
    return this.toDateBox.getValue();
  }
  
  @Override
  public boolean getOnlyPhotoResponsesFlag() {
    return this.onlyPhotoResponsesCheckBox.isEnabled() ? this.onlyPhotoResponsesCheckBox.getValue() : false;
  }

  @Override
  public List<HasClickHandlers> getShareButtons() {
    List<HasClickHandlers> retval = new ArrayList<HasClickHandlers>();
    retval.add(this.shareButtonTop);
    retval.add(this.shareButtonBottom);
    return retval;
  }

  @Override
  public List<HasClickHandlers> getMakePrivateButtons() {
    List<HasClickHandlers> retval = new ArrayList<HasClickHandlers>();
    retval.add(this.makePrivateButtonTop);
    retval.add(this.makePrivateButtonBottom);
    return retval;
  }

  @Override
  public List<HasClickHandlers> getDeleteButtons() {
    List<HasClickHandlers> retval = new ArrayList<HasClickHandlers>();
    retval.add(this.deleteButtonTop);
    retval.add(this.deleteButtonBottom);
    return retval;
  }
  
  @Override
  public HasClickHandlers getApplyFiltersButton() {
    return this.applyFiltersButton;
  }

  @Override
  public HasChangeHandlers getCampaignFilter() {
    return this.campaignFilter;
  }

  @Override
  public HasChangeHandlers getSurveyFilter() {
    return this.surveyFilter;
  }

  @Override
  public HasChangeHandlers getParticipantFilter() {
    return this.participantFilter;
  }
  

  @Override
  public HasChangeHandlers getPrivacyFilter() {
    return this.privacyFilter;
  }

  @Override
  public HasValueChangeHandlers<Date> getStartDateFilter() {
    return this.fromDateBox;
  }

  @Override
  public HasValueChangeHandlers<Date> getEndDateFilter() {
    return this.toDateBox;
  }

  @Override
  public List<String> getSelectedSurveyResponseKeys() {
    List<String> keys = new ArrayList<String>();
    for (int i = 0; i < responseList.getWidgetCount(); i++) {
      ResponseDisplayWidget widget = (ResponseDisplayWidget)responseList.getWidget(i);
      if (widget != null && widget.isSelected()) {
        keys.add(widget.getResponseKey());
        
      }
    }    
    return keys;
  }
  
  @Override
  public void clearSelectedSurveyResponseKeys() {
    selectNone();
  }

  @Override
  public void clearResponseList() {
    this.responseList.clear();
  }

  @Override
  public void markShared(String responseKey) {
    int numWidgets = this.responseList.getWidgetCount();
    for (int i = 0; i < numWidgets; i++) {
      ResponseDisplayWidget responseWidget = (ResponseDisplayWidget)responseList.getWidget(i);
      if (responseWidget.getResponseKey().equals(responseKey)) {
        responseWidget.setPrivacy(Privacy.SHARED);
        break;
      }
    }
  }

  @Override
  public void markPrivate(String responseKey) {
    int numWidgets = this.responseList.getWidgetCount();
    for (int i = 0; i < numWidgets; i++) {
      ResponseDisplayWidget responseWidget = (ResponseDisplayWidget)responseList.getWidget(i);
      if (responseWidget.getResponseKey().equals(responseKey)) {
        responseWidget.setPrivacy(Privacy.PRIVATE);
        break;
      }
    }
  }
  
  @Override
  public void removeResponse(String responseKey) {
    int numWidgets = this.responseList.getWidgetCount();
    for (int i = 0; i < numWidgets; i++) {
      ResponseDisplayWidget responseWidget = (ResponseDisplayWidget)responseList.getWidget(i);
      if (responseWidget.getResponseKey().equals(responseKey)) {
        this.responseList.remove(i);
        break;
      }
    }    
  }

  @Override
  public void showInfoMessage(String info) {
    this.messageWidget.showInfoMessage(info);
  }

  @Override
  public void addErrorMessage(String error, String detail) {
    this.messageWidget.addError(error, detail);
  }
  
  @Override 
  public void clearErrorMessages() {
    this.messageWidget.clearErrors();
    this.messageWidget.hide();
  }

  @Override
  public void showConfirmDelete(final ClickHandler onConfirmDelete) {
    final DialogBox dialog = new DialogBox();
    dialog.setGlassEnabled(true);
    dialog.setText("Are you sure you want to delete the selected responses? " +
                   "This action cannot be undone.");
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

  @Override
  public void enableSurveyFilter() {
    this.surveyFilter.setEnabled(true);
  }

  @Override
  public void disableSurveyFilter() {
    this.surveyFilter.setSelectedIndex(-1);
    this.surveyFilter.setEnabled(false);
  }

  @Override
  public void clearSurveyList() {
    this.surveyFilter.clear();
    this.surveyFilter.setEnabled(false);
  }
  
  @Override
  public void enableParticipantFilter() {
    this.participantFilter.setEnabled(true);
  }

  @Override
  public void disableParticipantFilter() {
    this.participantFilter.setSelectedIndex(-1);
    this.participantFilter.setEnabled(false);
  }

  @Override 
  public void clearParticipantList() {
    this.participantFilter.clear();
    this.singleParticipantLabel.setText("");
  }

  @Override
  public void enableShowResponsesButton() {
	this.applyFiltersButton.setEnabled(true);
  }
  
  @Override
  public void disableShowResponsesButton() {
	this.applyFiltersButton.setEnabled(false);
  }
  
  @Override
  public void setSectionHeader(String headerText) {
    this.sectionHeaderTitle.setText(headerText);
  }

  @Override
  public void setSectionHeaderDetail(String detailText) {
    this.sectionHeaderDetail.setText(detailText);
  }
  
  @Override
  public void showResponseCountInSectionHeader(String username, int responseCount) {
    String userDisplayName = username.equals(AwConstants.specialAllValuesToken) ? "all users" : username;
    setSectionHeader("Found " + Integer.toString(responseCount) + " responses by " + userDisplayName);
  }
  
  @Override
  public Subview getSelectedSubview() {
    return selectedSubview != null ? selectedSubview : Subview.BROWSE; // default to browse view
  }
  

  @Override
  public void setSelectedSubview(Subview subview) {
    clearSelectedView();
    selectedSubview = (subview != null) ? subview : Subview.BROWSE;
    if (Subview.BROWSE.equals(selectedSubview)) {
      viewLinkBrowse.addStyleName(style.selectedTopNav());
      setEditControlsVisible(false);
      setExpandCollapseLinksVisible(true);
    } else if (Subview.EDIT.equals(selectedSubview)) {
      viewLinkEdit.addStyleName(style.selectedTopNav());
      setEditControlsVisible(true);
      setExpandCollapseLinksVisible(true);
    } 
  }  
  
  @Override
  public void setEditMenuItemVisible(boolean isVisible) {
    this.editResponsesMenuItem.setVisible(isVisible);
  }

  private void clearSelectedView() {
    selectedSubview = null;
    viewLinkEdit.removeStyleName(style.selectedTopNav());
    viewLinkBrowse.removeStyleName(style.selectedTopNav());
  }

  @Override
  public HasClickHandlers getViewLinkBrowse() {
    return this.viewLinkBrowse;
  }

  @Override
  public HasClickHandlers getViewLinkEdit() {
    return this.viewLinkEdit;
  }

  @Override
  public void showAllFilters() {
    this.optionalFilters.setVisible(true);
  }
  
  @Override
  public void hideOptionalFilters() {
    this.optionalFilters.setVisible(false);
  }
  
  @Override
  public void setPhotoResponsesCheckBoxEnabled(boolean isEnabled) {
    if (isEnabled) {
      this.onlyPhotoResponsesCheckBox.setEnabled(true);
      this.onlyPhotoResponsesPanel.removeStyleName(style.disabled());
      this.onlyPhotoResponsesPanel.setTitle(null);
    } else {
      this.onlyPhotoResponsesCheckBox.setEnabled(false);
      this.onlyPhotoResponsesPanel.addStyleName(style.disabled());
      this.onlyPhotoResponsesPanel.setTitle("Set survey filter to 'All' to use this option");
    }
  }

  @Override
  public HandlerRegistration addRangeChangeHandler(Handler handler) {
    return addHandler(handler, RangeChangeEvent.getType());
  }

  @Override
  public HandlerRegistration addRowCountChangeHandler(RowCountChangeEvent.Handler handler) {
    return addHandler(handler, RowCountChangeEvent.getType());
  }

  @Override
  public int getRowCount() {
    return this.rowCount;
  }

  @Override
  public Range getVisibleRange() {
    return new Range(this.visibleRangeStart, this.visibleRangeMaxLength);
  }

  @Override
  public boolean isRowCountExact() {
    return this.rowCountIsExact;
  }

  @Override
  public void setRowCount(int count) {
    this.rowCount = count;
    RowCountChangeEvent.fire(this, count, true);
  }

  @Override
  public void setRowCount(int count, boolean isExact) {
    setRowCount(count);
    this.rowCountIsExact = isExact;
  }
  
  @Override
  public void setVisibleRange(Range range) {
    setVisibleRange(range.getStart(), range.getLength());
  }

  @Override
  public void setVisibleRange(int start, int maxLength) {
    this.visibleRangeStart = start;
    RangeChangeEvent.fire(this, new Range(start, maxLength));
  }
  
  private void setVisibleRangeMaxLength(int length) {
    // convert length (which could be any number, possibly typed in the url by user) to 
    // one of the allowed page sizes - 10, 50, or 100
    int pageSize; 
    if (length < 50) pageSize = 10;
    else if (length >= 50 && length < 100) pageSize = 50;
    else pageSize = 100;
    // save page size to make it available to presenter
    this.visibleRangeMaxLength = pageSize;
    // remove underline from selected number 
    this.resultsPerPage10MenuItem.setStyleName(pageSize == 10 ? "" : "link");
    this.resultsPerPage50MenuItem.setStyleName(pageSize == 50 ? "" : "link");
    this.resultsPerPage100MenuItem.setStyleName(pageSize == 100 ? "" : "link");
  }
  
  @Override
  public int getSelectedPageSize() {
    return this.visibleRangeMaxLength;
  }
  
  @Override
  public void setSelectedPageSize(int pageSize) {
    setVisibleRangeMaxLength(pageSize);
  }
}
