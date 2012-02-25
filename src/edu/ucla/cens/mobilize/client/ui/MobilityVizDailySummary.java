package edu.ucla.cens.mobilize.client.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;

import edu.ucla.cens.mobilize.client.common.MobilityMode;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;
import edu.ucla.cens.mobilize.client.utils.MobilityUtils;

public class MobilityVizDailySummary extends Composite {
	private static final int PIE_CHART_HEIGHT_PX = 200;
	private static final int PIE_CHART_WIDTH_PX = 200;
	private static final int TEMPORAL_WIDTH_PX = 750;
	private static final int TEMPORAL_HEIGHT_PX = 120;

	private static MobilityVizDailySummaryUiBinder uiBinder = GWT
			.create(MobilityVizDailySummaryUiBinder.class);

	interface MobilityVizDailySummaryStyle extends CssResource {
		// TODO: CSS references
		/* OLD
		String membersTableHeader();
		String missingValue();
		String rolePrivileged();
		*/
		String hidden();
		String visible();
	}

	interface MobilityVizDailySummaryUiBinder extends UiBinder<Widget, MobilityVizDailySummary> {
	}

	// TODO: UiFields
	@UiField MobilityVizDailySummaryStyle style;
	@UiField FlowPanel distancePlot;
	@UiField FlowPanel durationPlot;
	@UiField FlowPanel temporalPlot;
	@UiField VerticalPanel distanceInfo;
	@UiField VerticalPanel durationInfo;
	@UiField VerticalPanel mobilityStats;

	public MobilityVizDailySummary(List<MobilityInfo> data) {
		// Init stuff
		initWidget(uiBinder.createAndBindUi(this));
		bind();

		// Load and render charts
		loadAndDisplayMobilityData(data);
	}

	private void bind() {
		// Nothing to bind
	}

	public void clearData() {
		// TODO: clears all set data
		// TODO: show place-holder message? "No data loaded"
	}

	public void loadAndDisplayMobilityData(List<MobilityInfo> data) {
		clearData();
		
		Map<MobilityMode, List<Float>> speedsMap = MobilityUtils.getModeSpeeds(data);
		
		// --- (2) Temporal Summary
		int interval = 5;
		List<MobilityMode> buckets = MobilityUtils.bucketByInterval(data, interval);
		Widget temporalViz = MobilityUtils.createMobilityBarChartCanvasWidget(buckets, interval, TEMPORAL_WIDTH_PX, TEMPORAL_HEIGHT_PX, true, true);
		temporalPlot.add(temporalViz);
		
		// --- (3a) Duration distribution
		// Calculate duration of each mode & tabulate in a map
		Map<MobilityMode, Integer> durationMap = getModeDurations(data);
		// Generate pie widget, generate duration estimates
		Widget durationPieViz = createDurationPieChart(durationMap, "minutes");
		durationPlot.add(durationPieViz);
		// Set text info
		setDurationInfo(durationMap);

		// --- (3b) Distance distribution
		// Calculate miles of each mode & tabulate in a map
		Map<MobilityMode, Float> distanceMap = getModeDistances(data);
		// Generate pie widget, generate duration estimates
		Widget distancePieViz = createDistancePieChart(distanceMap, "meters");
		distancePlot.add(distancePieViz);
		// Set text info
		setDistanceInfo(distanceMap);
		
		// --- (1) Stats Summary
		setMobilityStats(durationMap, distanceMap, speedsMap);
	}

	private void setMobilityStats(Map<MobilityMode,Integer> durationMap, Map<MobilityMode,Float> distanceMap, Map<MobilityMode, List<Float>> speedsMap) {
		// Display "mPulse" number
		// Display sedentary/ambulatory % (one in big, one in small) --- for waking day?
		// Display distance traveled -- by foot/bike
		// Display avg walking speed
		// Display avg running speed

		// (1) "mPulse"
		InlineLabel label1 = new InlineLabel();
		
		// (2) Ambulatory duration + of the total records available
		int ambulatoryMin = 0;
		int sedentaryMin = 0;
		if (durationMap.containsKey(MobilityMode.STILL))
			sedentaryMin += durationMap.get(MobilityMode.STILL);
		if (durationMap.containsKey(MobilityMode.WALK))
			ambulatoryMin += durationMap.get(MobilityMode.WALK);
		if (durationMap.containsKey(MobilityMode.RUN))
			ambulatoryMin += durationMap.get(MobilityMode.RUN);
		if (durationMap.containsKey(MobilityMode.BIKE))
			ambulatoryMin += durationMap.get(MobilityMode.BIKE);
		if (durationMap.containsKey(MobilityMode.DRIVE))
			sedentaryMin += durationMap.get(MobilityMode.DRIVE);
		
		InlineLabel label2 = new InlineLabel();
		label2.setText("You were ambulatory for " + Integer.toString(ambulatoryMin) + " minutes of the " + Integer.toString(sedentaryMin) + " minutes tracked.");
		mobilityStats.add(label2);
		
		// (3) Distanced traveled by foot
		float distanceRunWalked = 0.0f;
		if (distanceMap.containsKey(MobilityMode.WALK))
			distanceRunWalked += distanceMap.get(MobilityMode.WALK);
		if (distanceMap.containsKey(MobilityMode.RUN))
			distanceRunWalked += distanceMap.get(MobilityMode.RUN);
		
		InlineLabel label3 = new InlineLabel();
		label3.setText("You trekked " + Float.toString(distanceRunWalked) + " meters by foot today.");
		mobilityStats.add(label3);
		
		// (4) Average walking speed
		float avgWalkingSpeed = 0.0f;
		if (speedsMap.containsKey(MobilityMode.WALK)) {
			for (Float f : speedsMap.get(MobilityMode.WALK)) {
				avgWalkingSpeed += f;
			}
			avgWalkingSpeed /= (float)speedsMap.get(MobilityMode.WALK).size();
		}
		
		InlineLabel label4 = new InlineLabel();
		label4.setText("You average walking pace was " + Float.toString(avgWalkingSpeed) + " m/s.");
		mobilityStats.add(label4);
	}
	
	private Map<MobilityMode, Integer> getModeDurations(final List<MobilityInfo> data) {
		return MobilityUtils.getModeDurations(data);
	}
	
	private void setDurationInfo(Map<MobilityMode, Integer> durationMap) {
		for (MobilityMode mode : durationMap.keySet()) {
			if (mode.equals(MobilityMode.ERROR))
				continue;
			
			HTML label = new HTML();
			String txt = "";
			if (mode.equals(MobilityMode.STILL))
				txt += "You <b>sat</b> for ";
			else if (mode.equals(MobilityMode.WALK))
				txt += "You <b>walked</b> for ";
			else if (mode.equals(MobilityMode.RUN))
				txt += "You <b>ran</b> for ";
			else if (mode.equals(MobilityMode.BIKE))
				txt += "You <b>biked</b> for ";
			else if (mode.equals(MobilityMode.DRIVE))
				txt += "You <b>drove</b> for ";
			else
				txt += "Your mode was <b>\"" + mode.toString().toLowerCase() + "\"</b> for ";
			txt += MobilityUtils.getPrettyHoursMinutesStr(durationMap.get(mode));
			label.setHTML(txt);
			
			//label.setStyleName(style.asdf());	//TODO: put this in the massive if statement above
			durationInfo.add(label);
		}
	}
	
	private Map<MobilityMode, Float> getModeDistances(final List<MobilityInfo> data) {
		return MobilityUtils.getModeDistances(data);
	}
	
	private void setDistanceInfo(Map<MobilityMode, Float> distanceMap) {
		for (MobilityMode mode : distanceMap.keySet()) {
			if (mode.equals(MobilityMode.STILL) || mode.equals(MobilityMode.ERROR))
				continue;
			
			HTML label = new HTML();
			String txt = "";
			if (mode.equals(MobilityMode.WALK))
				txt += "You <b>walked</b> for ";
			else if (mode.equals(MobilityMode.RUN))
				txt += "You <b>ran</b> for ";
			else if (mode.equals(MobilityMode.BIKE))
				txt += "You <b>biked</b> for ";
			else if (mode.equals(MobilityMode.DRIVE))
				txt += "You <b>drove</b> for ";
			else
				txt += "Your mode was <b>\"" + mode.toString().toLowerCase() + "\"</b> for ";
			if (distanceMap.get(mode) > 1000.0)
				txt += NumberFormat.getFormat("0.000").format(distanceMap.get(mode) / 1000.0) + " kilometers";
			else
				txt += NumberFormat.getFormat("0").format(distanceMap.get(mode)) + " meters";
			label.setHTML(txt);
			
			//label.setStyleName(style.asdf());	//TODO: put this in the massive if statement above
			distanceInfo.add(label);
		}
	}

	private Widget createDurationPieChart(final Map<MobilityMode,Integer> dMap, final String unitLabel) {
		if (dMap == null)
			return null;
		
		final SimplePanel pieWidgetWrapper = new SimplePanel();
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				DataTable data = DataTable.create();
				data.addColumn(AbstractDataTable.ColumnType.STRING, "Mobility Mode");
				data.addColumn(AbstractDataTable.ColumnType.NUMBER, unitLabel);
				data.addRows(dMap.size());	//FIXME: this might include any modes we exclude in the for loop
				
				List<String> colors = new ArrayList<String>();
				int index = 0;
				for (MobilityMode mode : dMap.keySet()) {
					// Skip ERROR modes
					if (mode.equals(MobilityMode.ERROR))
						continue;
					
					// Set data point
					data.setValue(index, 0, mode.toString());
					data.setValue(index, 1, dMap.get(mode));
					
					// Set color
					colors.add(MobilityUtils.getMobilityHTMLHexColor(mode));
					
					// Increment index
					index++;
				}
				
				// Generate piechart options
				PieOptions options = PieOptions.create();
				options.setWidth(PIE_CHART_WIDTH_PX);
				options.setHeight(PIE_CHART_HEIGHT_PX);
				options.setColors(colors.toArray(new String[colors.size()]));
				options.setLegend(LegendPosition.NONE);
				
				PieChart staticPieWidget = new PieChart(data, options);
				pieWidgetWrapper.add(staticPieWidget);
			}
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);

		return pieWidgetWrapper;
	}
	
	private Widget createDistancePieChart(final Map<MobilityMode,Float> dMap, final String unitLabel) {
		if (dMap == null)
			return null;
		
		final SimplePanel pieWidgetWrapper = new SimplePanel();
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				DataTable data = DataTable.create();
				data.addColumn(AbstractDataTable.ColumnType.STRING, "Mobility Mode");
				data.addColumn(AbstractDataTable.ColumnType.NUMBER, unitLabel);
				data.addRows(dMap.size());	//FIXME: this might include any modes we exclude in the for loop
				
				List<String> colors = new ArrayList<String>();
				int index = 0;
				for (MobilityMode mode : dMap.keySet()) {
					// Skip ERROR modes
					if (mode.equals(MobilityMode.STILL) || mode.equals(MobilityMode.ERROR))
						continue;
					
					// Set data point
					data.setValue(index, 0, mode.toString());
					data.setValue(index, 1, dMap.get(mode));
					
					// Set color
					colors.add(MobilityUtils.getMobilityHTMLHexColor(mode));
					
					// Increment index
					index++;
				}
				
				// Generate piechart options
				PieOptions options = PieOptions.create();
				options.setWidth(PIE_CHART_WIDTH_PX);
				options.setHeight(PIE_CHART_HEIGHT_PX);
				options.setColors(colors.toArray(new String[colors.size()]));
				options.setLegend(LegendPosition.NONE);
				
				PieChart staticPieWidget = new PieChart(data, options);
				pieWidgetWrapper.add(staticPieWidget);
			}
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);

		return pieWidgetWrapper;
	}
}
