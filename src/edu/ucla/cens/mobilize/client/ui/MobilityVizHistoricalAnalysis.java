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
import com.google.gwt.user.client.ui.CheckBox;
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
	@UiField CheckBox durationButton;
	@UiField CheckBox distanceButton;
	@UiField RadioButton ambulatoryButton;
	@UiField RadioButton stillButton;
	@UiField RadioButton walkButton;
	@UiField RadioButton runButton;
	@UiField RadioButton bikeButton;
	@UiField RadioButton driveButton;
	@UiField Label ambulatoryNote;
	@UiField FlowPanel timePlot;
	@UiField Label timePlotNotice;
	@UiField FlowPanel distributionPlot;
	@UiField HTMLPanel distributionSection;
	
	private List<List<MobilityInfo>> mobilityParam = null;
	
	/**
	 * Constructor
	 * @param multiMobilityData
	 */
	public MobilityVizHistoricalAnalysis(List<List<MobilityInfo>> multiMobilityData) {
		// Init stuff
		initWidget(uiBinder.createAndBindUi(this));

		// Init mobility radio handlers
		initMobilityModeHandlers();
		initMobilityCategoryHandlers();
		
		mobilityParam = multiMobilityData;
		
		// Load and render charts
		drawPlots();
	}
	
	//---

	final List<CheckBox> mobilityCategories = new ArrayList<CheckBox>();
	private void initMobilityCategoryHandlers() {
		mobilityCategories.add(durationButton);
		mobilityCategories.add(distanceButton);
		
		for (final CheckBox b : mobilityCategories) {
			b.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// Highlight selected radio
					if (b.getValue())
						b.addStyleName(style.selected());
					else
						b.removeStyleName(style.selected());
					
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
					
					/*
					// Hide all other styles
					for (RadioButton z : mobilityCategories) {
						if (b != z) {
							z.removeStyleName(style.selected());
						}
					}
					*/
					
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
	
	private String getSelectedMobilityCategory() {
		String selection = null;
		if (durationButton.getValue() && distanceButton.getValue())
			selection = "both";
		else if (durationButton.getValue())
			selection = "duration";
		else if (distanceButton.getValue())
			selection = "distance";
		return selection;
	}
	
	private String getSelectedMobilityMode() {
		for (RadioButton r : mobilityModes) {
			if (r.getValue())
				return r.getText().toLowerCase();
		}
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
	
		distributionSection.setVisible(false);
		loadAndDisplayMobilityAnalysis(mobilityCategory, mobilityMode);
	}
	
	//---
	
	private void loadAndDisplayMobilityAnalysis(String mobilityCategory, String mobilityMode) {
		if (mobilityCategory.equals("both") == false) {
			Map<Date,Float> mobilityDataSet = generateMobilityDataSet(mobilityParam, mobilityCategory, mobilityMode);
			
			// Draw the plots
			String chartTitle = "Time series for " + mobilityMode + " " + mobilityCategory;
			String yAxisUnit = "Unit";
			if (mobilityCategory.contains("duration"))
				yAxisUnit = "Duration (minutes)";
			else if (mobilityCategory.contains("distance"))
				yAxisUnit = "Distance (meters)";
			String color = null;
			if (mobilityCategory.contains("duration"))	color = "#15a2ea";
			else if (mobilityCategory.contains("distance"))	color = "#ea3d15";
			
			timePlot.clear();
			timePlot.add(createSingleTimeAnalysisChart(mobilityDataSet, chartTitle, yAxisUnit, color));
			
			// Display note if not enough data
			timePlotNotice.setVisible((mobilityDataSet.size() <= 1));
		} else {
			Map<Date,Float> durationDataSet = generateMobilityDataSet(mobilityParam, "duration", mobilityMode);
			Map<Date,Float> distanceDataSet = generateMobilityDataSet(mobilityParam, "distance", mobilityMode);
			
			// Draw the plots
			String chartTitle = "Time series for " + mobilityMode + " duration and distance";
			
			timePlot.clear();
			timePlot.add(createDualTimeAnalysisChart(durationDataSet, distanceDataSet, chartTitle));
			
			// Display note if not enough data
			timePlotNotice.setVisible((durationDataSet.size() <= 1));
		}
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
	
	private Widget createSingleTimeAnalysisChart(Map<Date,Float> mobilityDataSet, String title, String yAxisLabel, String color) {
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
		.setAxisTitleText("Date")
		.setType(Axis.Type.DATE_TIME)
		.setTickInterval(24 * 3600 * 1000)  // one day
		.setTickWidth(0)
		.setGridLineWidth(1)
		.setLabels(new XAxisLabels()
		.setAlign(Labels.Align.LEFT)
		//.setX(3)
		//.setY(-3)
				);

		chart.getYAxis()  
		.setAxisTitleText(yAxisLabel)  
		.setMin(0);  

		Series series = chart.createSeries();
		if (color != null && !color.isEmpty())
			chart.setColors(color);
		series.setName(yAxisLabel);
		for (Date day : mobilityDataSet.keySet()) {
			series.addPoint(day.getTime(), mobilityDataSet.get(day));
		}
		chart.addSeries(series);

		return chart;  
	}
	
	private Widget createDualTimeAnalysisChart(Map<Date,Float> durationDataSet, Map<Date,Float> distanceDataSet, String title) {
		// (1) --- Initialize chart
		final Chart chart = new Chart();
		
		// Set dimensions
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

		// (3) --- Setup Y-Axis
		
		//primary (left) Y-axis
		chart.getYAxis(0)
		.setLabels(new YAxisLabels().setColor("#15a2ea"))
		.setAxisTitleText("Duration (minutes)")  
		.setMin(0);
		
		//secondary (right) Y-axis
		chart.getYAxis(1)
		.setLabels(new YAxisLabels().setColor("#ea3d15"))
		.setOpposite(true)
		.setAxisTitleText("Distance (meters)")  
		.setMin(0);
		
		// (4) --- Load Y-Axis data
		
		// Load series 1 (left)
		Series series1 = chart.createSeries();
		series1.setName("Duration (minutes)")
		.setYAxis(0);
		for (Date day : durationDataSet.keySet()) {
			series1.addPoint(day.getTime(), durationDataSet.get(day));
		}
		series1.setType(Type.SPLINE);
		chart.addSeries(series1);
		
		// Load series 2 (right)
		Series series2 = chart.createSeries();
		series2.setName("Distance (meters)")
		.setYAxis(1);
		for (Date day : distanceDataSet.keySet()) {
			series2.addPoint(day.getTime(), distanceDataSet.get(day));
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
