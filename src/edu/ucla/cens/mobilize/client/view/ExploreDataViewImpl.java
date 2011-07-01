package edu.ucla.cens.mobilize.client.view;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
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

  public interface ExploreDataStyles extends CssResource {
    String treeItemCategory();
    String treeItemPlotType();
  }
  
  @UiField ExploreDataStyles style;
  @UiField Tree plotTypeTree;
  
  public ExploreDataViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    loadPlotTypeTree();
  }
  
  public void loadPlotTypeTree() {

    // response count plot nodes 
    TreeItem responseCount = new TreeItem("Response Count"); // category 
    TreeItem surveyResponseCount = new TreeItem("Survey Response Count");
    // style 
    responseCount.setStyleName(style.treeItemCategory());
    surveyResponseCount.setStyleName(style.treeItemPlotType());
    
    // univariate plot nodes
    TreeItem univariate = new TreeItem("Univariate"); // category
    TreeItem userTimeseries = new TreeItem("User Timeseries");
    TreeItem promptTimeseries = new TreeItem("Prompt Timeseries");
    TreeItem promptDistribution = new TreeItem("PromptDistribution");
    // style
    univariate.setStyleName(style.treeItemCategory());
    userTimeseries.setStyleName(style.treeItemPlotType());
    promptTimeseries.setStyleName(style.treeItemPlotType());
    promptDistribution.setStyleName(style.treeItemPlotType());
    
    // multivariate plot nodes
    TreeItem multivariate = new TreeItem("Multivariate"); // category
    TreeItem scatterplot = new TreeItem("Scatterplot");
    TreeItem density = new TreeItem("2D Density Plot");
    // style    
    multivariate.setStyleName(style.treeItemCategory());
    scatterplot.setStyleName(style.treeItemPlotType());
    density.setStyleName(style.treeItemPlotType());
    
    // geographic plot nodes
    TreeItem geographic = new TreeItem("Geographical"); // category
    TreeItem googleMap = new TreeItem("Google Map");
    // style
    geographic.setStyleName(style.treeItemCategory());
    googleMap.setStyleName(style.treeItemPlotType());
    
    // build the tree
    plotTypeTree.addItem(responseCount);
    plotTypeTree.addItem(univariate);
    plotTypeTree.addItem(multivariate);
    plotTypeTree.addItem(geographic);
    responseCount.addItem(surveyResponseCount);
    univariate.addItem(userTimeseries);
    univariate.addItem(promptTimeseries);
    univariate.addItem(promptDistribution);
    multivariate.addItem(scatterplot);
    multivariate.addItem(density);
    geographic.addItem(googleMap);
  }

/*
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
  }*/
    
   
  
}
