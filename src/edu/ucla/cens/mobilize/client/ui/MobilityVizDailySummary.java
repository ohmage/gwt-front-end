package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.Series.Type;

import edu.ucla.cens.mobilize.client.common.MobilityMode;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MobilityUtils;

public class MobilityVizDailySummary extends Composite {
	private static final int CHART_HEIGHT_PX = 200;
	private static final int CHART_WIDTH_PX = 200;
	private static final int TEMPORAL_WIDTH_PX = 800;
	private static final int TEMPORAL_HEIGHT_PX = 120;

	private static MobilityVizDailySummaryUiBinder uiBinder = GWT
			.create(MobilityVizDailySummaryUiBinder.class);

	interface MobilityVizDailySummaryStyle extends CssResource {
		String hidden();
		String visible();
	}

	interface MobilityVizDailySummaryUiBinder extends UiBinder<Widget, MobilityVizDailySummary> {
	}

	@UiField MobilityVizDailySummaryStyle style;
	@UiField FlowPanel distancePlot;
	@UiField FlowPanel durationPlot;
	@UiField FlowPanel temporalPlot;
	@UiField VerticalPanel distanceInfo;
	@UiField VerticalPanel durationInfo;
	
	@UiField Label date_label;
	@UiField Label stat_total_time_ambulatory;
	@UiField Label stat_total_time_sedentary;
	@UiField Label stat_total_foot_distance;
	@UiField Label stat_average_walking_speed;
	@UiField Label stat_total_time_tracked;

	public MobilityVizDailySummary(List<MobilityInfo> data) {
		// Init stuff
		initWidget(uiBinder.createAndBindUi(this));

		// Load and render charts
		loadAndDisplayMobilityData(data);
	}

	public void loadAndDisplayMobilityData(List<MobilityInfo> data) {
		// --- (0) Set date
		if (data.isEmpty())
			date_label.setText("Daily summary for unknown date");
		else {
			DateTimeFormat format = DateTimeFormat.getFormat("EEEE, MMMM dd, yyyy");
			String day_str = format.format(data.get(0).getDate());
			date_label.setText("Daily summary for " + day_str);
		}
		
		// --- (2) Temporal Summary
		int interval = 5;
		List<MobilityMode> buckets = MobilityUtils.bucketByInterval(data, interval);
		Widget temporalViz = MobilityUtils.createMobilityBarChartCanvasWidget(buckets, interval, TEMPORAL_WIDTH_PX, TEMPORAL_HEIGHT_PX, true, true);
		temporalPlot.add(temporalViz);
		
		// --- (3a) Duration distribution
		// Calculate duration of each mode & tabulate in a map
		Map<MobilityMode, Integer> durationMap = getModeDurations(data);
		// Generate column widget, generate duration estimates
		Widget durationColumnViz = createDurationComboChart(durationMap);
		durationPlot.add(durationColumnViz);
		// Set text info
		setDurationInfo(durationMap);
		
		// --- (3b) Distance distribution
		// Calculate miles of each mode & tabulate in a map
		Map<MobilityMode, Float> distanceMap = getModeDistances(data);
		// Generate column widget, generate duration estimates
		Widget distanceColumnViz = createDistanceComboChart(distanceMap);
		distancePlot.add(distanceColumnViz);
		// Set text info
		setDistanceInfo(distanceMap);

		// --- (1) Stats Summary
		setMobilityStats(durationMap, distanceMap);
	}

	private void setMobilityStats(Map<MobilityMode,Integer> durationMap, Map<MobilityMode,Float> distanceMap) {
		// (1 & 2) Ambulatory duration + of the total records available
		int ambulatoryMin = 0;
		int sedentaryMin = 0;
		if (durationMap != null) {
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
		}
		
		stat_total_time_ambulatory.setText(MobilityUtils.getPrettyHoursMinutesStr(ambulatoryMin));
		stat_total_time_sedentary.setText(MobilityUtils.getPrettyHoursMinutesStr(sedentaryMin));
		stat_total_time_tracked.setText(MobilityUtils.getPrettyHoursMinutesStr(ambulatoryMin+sedentaryMin));
		
		// (3) Distanced traveled by foot
		float distanceRunWalked = 0.0f;
		if (distanceMap != null) {
			if (distanceMap.containsKey(MobilityMode.WALK))
				distanceRunWalked += distanceMap.get(MobilityMode.WALK);
			if (distanceMap.containsKey(MobilityMode.RUN))
				distanceRunWalked += distanceMap.get(MobilityMode.RUN);
		}
		
		stat_total_foot_distance.setText(NumberFormat.getFormat("0").format(distanceRunWalked) + " meters");
	}
	
	private Map<MobilityMode, Integer> getModeDurations(final List<MobilityInfo> data) {
		return MobilityUtils.getModeDurations(data);
	}
	
	private void setDurationInfo(Map<MobilityMode, Integer> durationMap) {
		for (MobilityMode mode : durationMap.keySet()) {
			if (mode.equals(MobilityMode.ERROR))
				continue;
			
			String hexColorStr = MobilityUtils.getMobilityHTMLHexColor(mode);
			
			HTML label = new HTML();
			String txt = "";
			if (mode.equals(MobilityMode.STILL))
				txt += "You <b><font color=\""+ hexColorStr + "\">sat</font></b> for ";
			else if (mode.equals(MobilityMode.WALK))
				txt += "You <b><font color=\""+ hexColorStr + "\">walked</font></b> for ";
			else if (mode.equals(MobilityMode.RUN))
				txt += "You <b><font color=\""+ hexColorStr + "\">ran</font></b> for ";
			else if (mode.equals(MobilityMode.BIKE))
				txt += "You <b><font color=\""+ hexColorStr + "\">biked</font></b> for ";
			else if (mode.equals(MobilityMode.DRIVE))
				txt += "You <b><font color=\""+ hexColorStr + "\">drove</font></b> for ";
			else
				txt += "Your mode was <b>\"" + mode.toString().toLowerCase() + "\"</b> for ";
			txt += MobilityUtils.getPrettyHoursMinutesStr(durationMap.get(mode));
			label.setHTML(txt);
			
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
			
			String hexColorStr = MobilityUtils.getMobilityHTMLHexColor(mode);
			
			HTML label = new HTML();
			String txt = "";
			if (mode.equals(MobilityMode.WALK))
				txt += "You <b><font color=\""+ hexColorStr + "\">walked</font></b> for ";
			else if (mode.equals(MobilityMode.RUN))
				txt += "You <b><font color=\""+ hexColorStr + "\">ran</font></b> for ";
			else if (mode.equals(MobilityMode.BIKE))
				txt += "You <b><font color=\""+ hexColorStr + "\">biked</font></b> for ";
			else if (mode.equals(MobilityMode.DRIVE))
				txt += "You <b><font color=\""+ hexColorStr + "\">drove</font></b> for ";
			else
				txt += "Your mode was <b>\"" + mode.toString().toLowerCase() + "\"</b> for ";
			if (distanceMap.get(mode) > 1000.0)
				txt += NumberFormat.getFormat("0.000").format(distanceMap.get(mode) / 1000.0) + " kilometers";
			else
				txt += NumberFormat.getFormat("0").format(distanceMap.get(mode)) + " meters";
			label.setHTML(txt);
			
			distanceInfo.add(label);
		}
	}

	private Widget createDurationComboChart(final Map<MobilityMode,Integer> dMap) {
		if (dMap == null)
			return null;
		
		final SimplePanel columnWidgetWrapper = new SimplePanel();
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				DataTable data = DataTable.create();
				
				data.addColumn(AbstractDataTable.ColumnType.STRING, "X-axis");
				if (dMap.containsKey(MobilityMode.STILL))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.STILL.toString());
				if (dMap.containsKey(MobilityMode.WALK))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.WALK.toString());
				if (dMap.containsKey(MobilityMode.RUN))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.RUN.toString());
				if (dMap.containsKey(MobilityMode.BIKE))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.BIKE.toString());
				if (dMap.containsKey(MobilityMode.DRIVE))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.DRIVE.toString());
				
				data.addRow();
				data.setValue(0, 0, "Activity");
				int columnIndex = 1;
				if (dMap.containsKey(MobilityMode.STILL))	data.setValue(0, columnIndex++, (float)dMap.get(MobilityMode.STILL) / 60.0);
				if (dMap.containsKey(MobilityMode.WALK))	data.setValue(0, columnIndex++, (float)dMap.get(MobilityMode.WALK) / 60.0);
				if (dMap.containsKey(MobilityMode.RUN))		data.setValue(0, columnIndex++, (float)dMap.get(MobilityMode.RUN) / 60.0);
				if (dMap.containsKey(MobilityMode.BIKE))	data.setValue(0, columnIndex++, (float)dMap.get(MobilityMode.BIKE) / 60.0);
				if (dMap.containsKey(MobilityMode.DRIVE))	data.setValue(0, columnIndex++, (float)dMap.get(MobilityMode.DRIVE) / 60.0);
				
				List<String> colors = new ArrayList<String>();
				if (dMap.containsKey(MobilityMode.STILL))	colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.STILL));
				if (dMap.containsKey(MobilityMode.WALK))	colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.WALK));
				if (dMap.containsKey(MobilityMode.RUN))		colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.RUN));
				if (dMap.containsKey(MobilityMode.BIKE))	colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.BIKE));
				if (dMap.containsKey(MobilityMode.DRIVE))	colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.DRIVE));
				
				// Generate ComboChart options
				Options options = Options.create();
				options.setWidth(CHART_WIDTH_PX);
				options.setHeight(CHART_HEIGHT_PX);
				options.setColors(colors.toArray(new String[colors.size()]));
				options.setLegend(LegendPosition.NONE);
				options.setBackgroundColor("transparent");
				options.setSeriesType(Type.BARS);	//for some reason we need this here
				
				AxisOptions vOptions = AxisOptions.create();
				vOptions.set("viewWindowMode", "explicit");
			    Options viewWindowOption = Options.create();
			    viewWindowOption.set("max", 6.0);	//set 6 hours as the max plot height
			    vOptions.set("viewWindow",viewWindowOption);
			    vOptions.setTitle("Hours");
				options.setVAxisOptions(vOptions);
				
				ComboChart staticColumnWidget = new ComboChart(data, options);
				columnWidgetWrapper.add(staticColumnWidget);
			}
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, ComboChart.PACKAGE);

		return columnWidgetWrapper;
	}
	
	private Widget createDistanceComboChart(final Map<MobilityMode,Float> dMap) {
		if (dMap == null)
			return null;
		
		final SimplePanel columnWidgetWrapper = new SimplePanel();
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				DataTable data = DataTable.create();

				data.addColumn(AbstractDataTable.ColumnType.STRING, "X-axis");
				if (dMap.containsKey(MobilityMode.WALK))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.WALK.toString());
				if (dMap.containsKey(MobilityMode.RUN))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.RUN.toString());
				if (dMap.containsKey(MobilityMode.BIKE))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.BIKE.toString());
				if (dMap.containsKey(MobilityMode.DRIVE))
					data.addColumn(AbstractDataTable.ColumnType.NUMBER, MobilityMode.DRIVE.toString());
				
				data.addRow();
				data.setValue(0, 0, "Activity");
				int columnIndex = 1;
				if (dMap.containsKey(MobilityMode.WALK))	data.setValue(0, columnIndex++, dMap.get(MobilityMode.WALK).intValue());
				if (dMap.containsKey(MobilityMode.RUN))		data.setValue(0, columnIndex++, dMap.get(MobilityMode.RUN).intValue());
				if (dMap.containsKey(MobilityMode.BIKE))	data.setValue(0, columnIndex++, dMap.get(MobilityMode.BIKE).intValue());
				if (dMap.containsKey(MobilityMode.DRIVE))	data.setValue(0, columnIndex++, dMap.get(MobilityMode.DRIVE).intValue());
				
				List<String> colors = new ArrayList<String>();
				if (dMap.containsKey(MobilityMode.WALK))	colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.WALK));
				if (dMap.containsKey(MobilityMode.RUN))		colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.RUN));
				if (dMap.containsKey(MobilityMode.BIKE))	colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.BIKE));
				if (dMap.containsKey(MobilityMode.DRIVE))	colors.add(MobilityUtils.getMobilityHTMLHexColor(MobilityMode.DRIVE));
				
				// Generate ComboChart options
				Options options = Options.create();
				options.setWidth(CHART_WIDTH_PX);
				options.setHeight(CHART_HEIGHT_PX);
				options.setColors(colors.toArray(new String[colors.size()]));
				options.setLegend(LegendPosition.NONE);
				options.setBackgroundColor("transparent");
				options.setSeriesType(Type.BARS);	//for some reason we need this here
				
				AxisOptions vOptions = AxisOptions.create();
			    vOptions.setTitle("Meters");
				options.setVAxisOptions(vOptions);
				
				ComboChart staticColumnWidget = new ComboChart(data, options);
				columnWidgetWrapper.add(staticColumnWidget);
			}
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback, ComboChart.PACKAGE);

		return columnWidgetWrapper;
	}
	
	
}
