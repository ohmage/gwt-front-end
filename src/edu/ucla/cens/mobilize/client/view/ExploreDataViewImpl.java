package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class ExploreDataViewImpl extends Composite implements ExploreDataView {

  private static ExploreDataViewUiBinder uiBinder = GWT
      .create(ExploreDataViewUiBinder.class);

  @UiTemplate("ExploreDataView.ui.xml")
  interface ExploreDataViewUiBinder extends UiBinder<Widget, ExploreDataViewImpl> {
  }

  @UiField ListBox campaignListBox;
  @UiField ListBox versionListBox;
  @UiField ListBox surveyListBox;
  @UiField ListBox groupListBox;
  @UiField ListBox userListBox;
  @UiField DateBox startDate;
  @UiField DateBox endDate;
  @UiField Tree variableTree;
  @UiField FlowPanel content;
  @UiField RadioButton byTimeRadio;
  @UiField RadioButton byLocationRadio;
  @UiField RadioButton rawDataRadio;
  
  ExploreDataView.Presenter presenter;
  
  public ExploreDataViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    LoadPlottableVariables();
    LoadCampaigns();
    WireUpEventHandlers();
    byTimeRadio.setValue(true);
    UpdateDisplay();
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setVariableList(ArrayList<String> plottableVariables) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setPlot(Image plot) {
    // TODO Auto-generated method stub
    
  }

  public void LoadPlottableVariables() {
    for (int k = 0; k < 3; k++) {
      TreeItem item = new TreeItem("Sleep Sens");
      item.addStyleName("treeItemCampaign");
      for (int i = 0; i < 2; i++) {
        TreeItem survey = new TreeItem("Survey");
        survey.addStyleName("treeItemSurvey");
        for (int j = 0; j < 3; j++) {
          TreeItem prompt = new TreeItem("Prompt");
          prompt.addStyleName("treeItemPrompt");
          survey.addItem(prompt);
        }
        item.addItem(survey);
      }
      variableTree.addItem(item);
    }
  }
  
  public void LoadCampaigns() {
    // FIXME: this should be done by presenter with setCampaign instead
    campaignListBox.addItem("Sleep Sens");
    campaignListBox.addItem("Advertising");
    versionListBox.addItem("v1.0");
    versionListBox.addItem("v2.0");
    versionListBox.addItem("the-cool-one");
    surveyListBox.addItem("Restedness");
    surveyListBox.addItem("Some other example");
    groupListBox.addItem("CS101");
    groupListBox.addItem("CS102");
    groupListBox.addItem("Bio");
    userListBox.addItem("Joe Brown");
  }
  
  public void WireUpEventHandlers() {
    // FIXME: event should be reported to presenter instead and handled there
    ValueChangeHandler<Boolean> radioChange = new ValueChangeHandler<Boolean>() {
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        UpdateDisplay();
      }
    };

    // FIXME: handler will be called twice
    byTimeRadio.addValueChangeHandler(radioChange);
    byLocationRadio.addValueChangeHandler(radioChange);
    rawDataRadio.addValueChangeHandler(radioChange);
  }
  
  public void UpdateDisplay() {
    if (byTimeRadio.getValue() == true) {
      ShowByTime();
    } else if (byLocationRadio.getValue() == true) {
      ShowByLocation();
    } else if (rawDataRadio.getValue() == true) {
      ShowRawData();
    }
  }
  
  private void ShowByTime() {
    // FIXME: update existing image instead of creating a new one every time
    Image plot = new Image();
    plot.setUrl("images/histogram.png");
    content.clear();
    content.add(plot);

  }
  
  private void ShowByLocation() {
    Image plot = new Image();
    plot.setUrl("images/map.gif");
    content.clear();
    content.add(plot);
  }
  
  private void ShowRawData() {
    FlexTable data = new FlexTable();
    
    data.setText(0, 0, "Time");
    data.setText(0, 1, "User");
    data.setText(0, 2, "Location");
    data.setText(0, 3, "Value");
    data.setText(0, 4, "Campaign");
    
    data.setText(1, 0, "Tues 1/3/11 5:00");
    data.setText(1, 1, "Joe Brown");
    data.setText(1, 2, "1.23N 325E");
    data.setText(1, 3, "100");
    data.setText(1, 4, "Sleep Sens");
    
    data.setText(2, 0, "Wed 1/11/11 3:30");
    data.setText(2, 1, "Joe Brown");
    data.setText(2, 2, "123N 321W");
    data.setText(2, 3, "Yes");
    data.setText(2, 4, "Sleep Sens");
    
    data.setText(3, 0, "Sun 3/2/11 12:00");
    data.setText(3, 1, "Jan Blue");
    data.setText(3, 2, "908N 123E");
    data.setText(3, 3, "12");
    data.setText(3, 4, "Ads"); 
    
    content.clear();
    content.add(data);
  }
  
}
