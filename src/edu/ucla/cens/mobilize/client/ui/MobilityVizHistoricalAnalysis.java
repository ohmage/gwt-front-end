package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.moxieapps.gwt.highcharts.client.*;  
import org.moxieapps.gwt.highcharts.client.Series.Type;
import org.moxieapps.gwt.highcharts.client.labels.*;  

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.MobilityMode;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;
import edu.ucla.cens.mobilize.client.model.PromptResponse;
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.utils.MobilityUtils;

public class MobilityVizHistoricalAnalysis extends Composite {
	private static final int CHART_WIDTH_PX = 800;
	private static final int CHART_HEIGHT_PX = 300;

	private static Logger _logger = Logger.getLogger(MobilityVizHistoricalAnalysis.class.getName());
	
	private static MobilityVizHistoricalAnalysisUiBinder uiBinder = GWT
			.create(MobilityVizHistoricalAnalysisUiBinder.class);

	interface MobilityVizHistoricalAnalysisStyle extends CssResource {
		String selected();
		String faded();
	}

	interface MobilityVizHistoricalAnalysisUiBinder extends UiBinder<Widget, MobilityVizHistoricalAnalysis> {
	}

	@UiField MobilityVizHistoricalAnalysisStyle style;
	@UiField RadioButton durationButton;
	@UiField RadioButton distanceButton;
	@UiField RadioButton ambulatoryButton;
	@UiField RadioButton stillButton;
	@UiField RadioButton walkButton;
	@UiField RadioButton runButton;
	@UiField RadioButton bikeButton;
	@UiField RadioButton driveButton;
	@UiField Label ambulatoryNote;
	@UiField ListBox responseListBox;
	@UiField FlowPanel timePlot;
	@UiField FlowPanel distributionPlot;
	@UiField HTMLPanel distributionSection;
	@UiField HTMLPanel responseDomainSection;
	
	private List<List<MobilityInfo>> mobilityParam = null;
	private List<SurveyResponse> responseParam = null;
	private Map<String,String> promptIdMap = null;
	
	/**
	 * View 1: Mobility-only analysis
	 * @param multiMobilityData
	 */
	public MobilityVizHistoricalAnalysis(List<List<MobilityInfo>> multiMobilityData) {
		// Init stuff
		initWidget(uiBinder.createAndBindUi(this));

		// Init mobility radio handlers
		initMobilityModeHandlers();
		initMobilityCategoryHandlers();
		
		mobilityParam = multiMobilityData;
		responseParam = null;
		
		// Load and render charts
		drawPlots();
	}
	
	/**
	 * View 2: Mobility and Survey Response analysis
	 * @param multiMobilityData
	 * @param responseData
	 */
	public MobilityVizHistoricalAnalysis(List<List<MobilityInfo>> multiMobilityData, List<SurveyResponse> responseData, Map<String,String> questionMap) {
		// Init stuff
		initWidget(uiBinder.createAndBindUi(this));

		// Init mobility radio handlers
		initMobilityModeHandlers();
		initMobilityCategoryHandlers();
		initSurveyQuestionHandlers(questionMap);
		
		mobilityParam = multiMobilityData;
		responseParam = responseData;
		
		// Load and render charts
		drawPlots();
	}
	
	//---

	final List<RadioButton> mobilityCategories = new ArrayList<RadioButton>();
	private void initMobilityCategoryHandlers() {
		mobilityCategories.add(durationButton);
		mobilityCategories.add(distanceButton);
		
		for (final RadioButton b : mobilityCategories) {
			b.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// Highlight selected radio
					b.addStyleName(style.selected());
					
					// Disable appropriate modes
					if (b.getText().toLowerCase().contains("duration")) {
						// Nothing! All modes are valid for duration
						stillButton.setEnabled(true);
						stillButton.removeStyleName(style.faded());
					} else if (b.getText().toLowerCase().contains("distance")) {
						// Distance for "still" is meaningless
						stillButton.setEnabled(false);
						stillButton.setValue(false);
						stillButton.removeStyleName(style.selected());
						stillButton.addStyleName(style.faded());
					}
					
					// Hide all other styles
					for (RadioButton z : mobilityCategories) {
						if (b != z) {
							z.removeStyleName(style.selected());
						}
					}
					
					// render plots
					drawPlots();
				}
			});
		}
		
		// Pre-select a default category
		mobilityCategories.get(0).setValue(true, true);
		mobilityCategories.get(0).addStyleName(style.selected());
	}
	
	final List<RadioButton> mobilityModes = new ArrayList<RadioButton>();
	private void initMobilityModeHandlers() {
		mobilityModes.add(ambulatoryButton);
		mobilityModes.add(stillButton);
		mobilityModes.add(walkButton);
		mobilityModes.add(runButton);
		mobilityModes.add(bikeButton);
		mobilityModes.add(driveButton);
		
		for (final RadioButton b : mobilityModes) {
			b.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// Highlight selected radio
					b.addStyleName(style.selected());
					
					// Hide all other styles
					for (RadioButton z : mobilityModes) {
						if (b != z) {
							z.removeStyleName(style.selected());
						}
					}
					
					// Show ambulatory note, if appropriate
					ambulatoryNote.setVisible((b.getText().toLowerCase().contains("ambulatory")));
					
					// render plots
					drawPlots();
				}
			});
		}
		
		// Pre-select a default mode
		mobilityModes.get(0).setValue(true, true);
		mobilityModes.get(0).addStyleName(style.selected());
	}
	
	private void initSurveyQuestionHandlers(Map<String,String> questionMap) {
		promptIdMap = questionMap;
		responseListBox.clear();
		for (String promptId : questionMap.keySet())
			responseListBox.addItem(questionMap.get(promptId), promptId);
		
		responseListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// TODO Auto-generated method stub
				drawPlots();
			}
		});
	}
	
	private String getSelectedMobilityCategory() {
		for (RadioButton r : mobilityCategories) {
			if (r.getValue())
				return r.getText().toLowerCase();
		}
		return null;
	}
	
	private String getSelectedMobilityMode() {
		for (RadioButton r : mobilityModes) {
			if (r.getValue())
				return r.getText().toLowerCase();
		}
		return null;
	}
	
	private String getSelectedPromptId() {
		int selectedIndex = responseListBox.getSelectedIndex();
		if (selectedIndex >= 0)
			return responseListBox.getValue(selectedIndex);
		else
			return null;
	}
	
	//---
	
	private void drawPlots() {
		String mobilityCategory = getSelectedMobilityCategory();
		String mobilityMode = getSelectedMobilityMode();
		
		if (mobilityCategory == null || mobilityCategory.isEmpty())
			return;
		if (mobilityMode == null || mobilityMode.isEmpty())
			return;
		
		_logger.fine("drawing with mobilityCategory="+mobilityCategory+";mobilityMode="+mobilityMode+";");
		
		if (responseParam == null || responseParam.isEmpty()) {
			distributionSection.setVisible(false);
			responseDomainSection.setVisible(false);
			loadAndDisplayMobilityAnalysis(mobilityCategory, mobilityMode);
		} else {
			String promptId = getSelectedPromptId();
			if (promptId == null || promptId.isEmpty())
				return;
			
			distributionSection.setVisible(true);
			responseDomainSection.setVisible(true);
			loadAndDisplayMobilityResponseAnalysis(mobilityCategory, mobilityMode, promptId);
		}
	}
	
	//---
	
	private void loadAndDisplayMobilityAnalysis(String mobilityCategory, String mobilityMode) {
		Map<Date,Float> mobilityDataSet = generateMobilityDataSet(mobilityParam, mobilityCategory, mobilityMode);
		
		// Draw the plots
		String chartTitle = "Time series of " + mobilityMode + " " + mobilityCategory;
		String xAxisUnit = "Date";
		String yAxisUnit = "Unit";
		if (mobilityCategory.contains("duration"))
			yAxisUnit = "Duration (minutes)";
		else if (mobilityCategory.contains("distance"))
			yAxisUnit = "Distance (meters)";
		
		timePlot.clear();
		timePlot.add(createSingleTimeAnalysisChart(mobilityDataSet, chartTitle, xAxisUnit, yAxisUnit));
	}
	
	private void loadAndDisplayMobilityResponseAnalysis(String mobilityCategory, String mobilityMode, String responseQuestion) {
		Map<Date,Float> mobilityDataSet = generateMobilityDataSet(mobilityParam, mobilityCategory, mobilityMode);
		Map<Date,String> responseDataSet = generateResponseDataSet(responseParam, responseQuestion);
		
		String chartTitle = "Time series of " + mobilityMode + " " + mobilityCategory + " vs. survey question";
		String xAxisUnit = "Date";
		String yAxisUnit = "Unit";
		if (mobilityCategory.contains("duration"))
			yAxisUnit = "Duration (minutes)";
		else if (mobilityCategory.contains("distance"))
			yAxisUnit = "Distance (meters)";
		timePlot.clear();
		
		// debug
		//VerticalPanel v = new VerticalPanel();
		//for (Date d : responseDataSet.keySet())
		//	v.add(new Label(d.toString() + " --> " + responseDataSet.get(d)));
		//timePlot.add(v);
		
		timePlot.add(createDualTimeAnalysisChart(mobilityDataSet, responseDataSet, chartTitle, xAxisUnit, yAxisUnit));
		

		distributionPlot.clear();
	}
	
	//--- helper fxns

	private Map<Date,Float> generateMobilityDataSet(List<List<MobilityInfo>> rawMobilityData, String mobilityCategory, String mobilityMode) {
		Map<Date,Float> mobilityDataSet = new LinkedHashMap<Date,Float>();
		
		for (List<MobilityInfo> dayList : rawMobilityData) {
			if (dayList.isEmpty())
				continue;
			
			if (mobilityCategory.contains("duration")) {
				Map<MobilityMode, Integer> durationMap = getModeDurations(dayList);
				
				float value = 0.0f;
				if (mobilityMode.contains("ambulatory")) {
					if (durationMap.containsKey(MobilityMode.WALK))
						value += durationMap.get(MobilityMode.WALK);
					if (durationMap.containsKey(MobilityMode.RUN))
						value += durationMap.get(MobilityMode.RUN);
					if (durationMap.containsKey(MobilityMode.BIKE))
						value += durationMap.get(MobilityMode.BIKE);
				} else if (mobilityMode.contains("still")) {
					if (durationMap.containsKey(MobilityMode.STILL))
						value += durationMap.get(MobilityMode.STILL);
				}
				else if (mobilityMode.contains("walk")) {
					if (durationMap.containsKey(MobilityMode.WALK))
						value += durationMap.get(MobilityMode.WALK);
				}
				else if (mobilityMode.contains("run")) {
					if (durationMap.containsKey(MobilityMode.RUN))
						value += durationMap.get(MobilityMode.RUN);
				}
				else if (mobilityMode.contains("bike")) {
					if (durationMap.containsKey(MobilityMode.BIKE))
						value += durationMap.get(MobilityMode.BIKE);
				}
				else if (mobilityMode.contains("drive")) {
					if (durationMap.containsKey(MobilityMode.DRIVE))
						value += durationMap.get(MobilityMode.DRIVE);
				}
				else {
					// ERROR
					_logger.warning("Got invalid mobilityMode=\""+mobilityMode+"\"");
				}
				
				// Save data point
				mobilityDataSet.put(dayList.get(0).getDate(), value);
			} else if (mobilityCategory.contains("distance")) {
				Map<MobilityMode, Float> distanceMap = getModeDistances(dayList);
				
				float value = 0.0f;
				if (mobilityMode.contains("ambulatory")) {
					if (distanceMap.containsKey(MobilityMode.WALK))
						value += distanceMap.get(MobilityMode.WALK);
					if (distanceMap.containsKey(MobilityMode.RUN))
						value += distanceMap.get(MobilityMode.RUN);
					if (distanceMap.containsKey(MobilityMode.BIKE))
						value += distanceMap.get(MobilityMode.BIKE);
				} else if (mobilityMode.contains("walk")) {
					if (distanceMap.containsKey(MobilityMode.WALK))
						value += distanceMap.get(MobilityMode.WALK);
				}
				else if (mobilityMode.contains("run")) {
					if (distanceMap.containsKey(MobilityMode.RUN))
						value += distanceMap.get(MobilityMode.RUN);
				}
				else if (mobilityMode.contains("bike")) {
					if (distanceMap.containsKey(MobilityMode.BIKE))
						value += distanceMap.get(MobilityMode.BIKE);
				}
				else if (mobilityMode.contains("drive")) {
					if (distanceMap.containsKey(MobilityMode.DRIVE))
						value += distanceMap.get(MobilityMode.DRIVE);
				}
				else {
					// ERROR
					_logger.warning("Got invalid mobilityMode=\""+mobilityMode+"\"");
				}
				
				// Save data point
				mobilityDataSet.put(dayList.get(0).getDate(), value);
			} else {
				// ERROR
				_logger.warning("Got invalid mobilityCategory=\""+mobilityCategory+"\"");
			}
		}
		
		return mobilityDataSet;
	}
	
	private Map<Date,String> generateResponseDataSet(List<SurveyResponse> rawResponseData, String selectedPromptId) {
		Map<Date,String> result = new LinkedHashMap<Date,String>();
		
		for (SurveyResponse s : rawResponseData) {
			List<PromptResponse> prompts = s.getPromptResponses();
			for (PromptResponse p : prompts) {
				if (p.getPromptId().equals(selectedPromptId)) {
					_logger.fine("Putting in date = " + s.getResponseDate().toString());
					result.put(s.getResponseDate(), p.getResponsePrepared());
					break;
				}
			}
		}
		
		return result;
	}
	
	private Widget createSingleTimeAnalysisChart(Map<Date,Float> mobilityDataSet, String title, String xAxisLabel, String yAxisLabel) {
		final Chart chart = new Chart()  
		.setType(Series.Type.SPLINE);
		
		chart.setWidth(CHART_WIDTH_PX);
		chart.setHeight(CHART_HEIGHT_PX);
		
		chart.setChartTitleText(title);
		Legend legend = new Legend();
		legend.setEnabled(false);
		chart.setLegend(legend);
		Credits credits = new Credits();
		credits.setEnabled(false);
		chart.setCredits(credits);
		
		chart.getXAxis()
		.setAxisTitleText(xAxisLabel)
		.setType(Axis.Type.DATE_TIME)  
		.setDateTimeLabelFormats(new DateTimeLabelFormats()
		.setMonth("%e %b")
		.setDay("%e%b")
		.setYear("%b")  // don't display the dummy year  
				);  

		chart.getYAxis()  
		.setAxisTitleText(yAxisLabel)  
		.setMin(0);  

		Series series = chart.createSeries();
		series.setName(yAxisLabel);
		for (Date day : mobilityDataSet.keySet()) {
			series.addPoint(day.getTime(), mobilityDataSet.get(day));
		}
		chart.addSeries(series);

		return chart;  
	}
	
	private Widget createDualTimeAnalysisChart(Map<Date,Float> mobilityDataSet, Map<Date,String> responseDataSet, String title, String xAxisLabel, String yAxisLabel) {
		final Map<String,Integer> questionIndexMapping = new LinkedHashMap<String,Integer>();
		int newQuestionIndex = 0;
		
		final Chart chart = new Chart();
		
		chart.setWidth(CHART_WIDTH_PX);
		chart.setHeight(CHART_HEIGHT_PX);
		
		// Set title
		chart.setChartTitleText(title);
		chart.setColors("#15a2ea","#ea3d15");
		
		// Disable legend and credits
		Legend legend = new Legend();
		legend.setEnabled(false);
		chart.setLegend(legend);
		Credits credits = new Credits();
		credits.setEnabled(false);
		chart.setCredits(credits);
		
		// (2) --- Setup X-Axis
		chart.getXAxis()
		.setAxisTitleText("Date")
		.setType(Axis.Type.DATE_TIME)
		.setTickInterval(24 * 3600 * 1000)  // one day
		.setTickWidth(0)
		.setGridLineWidth(1)
		.setLabels(new XAxisLabels()
		.setAlign(Labels.Align.LEFT)
				);

		// (3) --- Init Y-Axes
		
		//primary (left) Y-axis
		chart.getYAxis(0)  
		.setAxisTitleText(yAxisLabel)  
		.setMin(0);  

		//secondary (right) Y-axis
		chart.getYAxis(1)
		.setTickInterval(1)
		.setOpposite(true)
		.setLabels(new YAxisLabels()  
                .setFormatter(new AxisLabelsFormatter() {  
                    public String format(AxisLabelsData axisLabelsData) {
                    	for (String str : questionIndexMapping.keySet()) {
                    		_logger.fine("Value="+Long.toString(axisLabelsData.getValueAsLong())+";QuestionIndex="+Integer.toString(questionIndexMapping.get(str))+";QuestionStr="+str);
                    		if (questionIndexMapping.get(str).equals((int)axisLabelsData.getValueAsLong()))
                    			return str;
                    	}
                    	return Long.toString(axisLabelsData.getValueAsLong());
                    }  
                })  
            )
		.setAxisTitleText("Responses")  
		.setMin(0);
		
		// (4) --- Load data
		
		//primary series
		Series series1 = chart.createSeries();
		series1.setName(yAxisLabel)
		.setYAxis(0);
		for (Date day : mobilityDataSet.keySet()) {
			series1.addPoint(day.getTime(), mobilityDataSet.get(day));
		}
		series1.setType(Type.COLUMN);
		chart.addSeries(series1);
		
		//primary series
		Series series2 = chart.createSeries();
		series2.setName(yAxisLabel)
		.setYAxis(1);
		for (Date day : responseDataSet.keySet()) {
			String responseStr = responseDataSet.get(day);
			if (questionIndexMapping.containsKey(responseStr) == false)
				questionIndexMapping.put(responseStr, newQuestionIndex++);
			
			series2.addPoint(day.getTime(), questionIndexMapping.get(responseStr));
		}
		series2.setType(Type.SPLINE);
		chart.addSeries(series2);
		
		return chart;  
	}
	
	private Widget createDistributionAnalysisChart() {
		// TODO
		return null;
	}
	
	private Map<MobilityMode, Integer> getModeDurations(final List<MobilityInfo> data) {
		return MobilityUtils.getModeDurations(data);
	}
	
	private Map<MobilityMode, Float> getModeDistances(final List<MobilityInfo> data) {
		return MobilityUtils.getModeDistances(data);
	}
}
