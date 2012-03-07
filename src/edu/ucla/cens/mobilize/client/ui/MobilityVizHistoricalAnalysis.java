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
import edu.ucla.cens.mobilize.client.model.SurveyResponse;
import edu.ucla.cens.mobilize.client.utils.DateUtils;
import edu.ucla.cens.mobilize.client.utils.MobilityUtils;

public class MobilityVizHistoricalAnalysis extends Composite {
	private static final int CHART_WIDTH_PX = 800;
	private static final int CHART_HEIGHT_PX = 300;

	private static MobilityVizHistoricalAnalysisUiBinder uiBinder = GWT
			.create(MobilityVizHistoricalAnalysisUiBinder.class);

	interface MobilityVizHistoricalAnalysisStyle extends CssResource {
		String hidden();
		String visible();
	}

	interface MobilityVizHistoricalAnalysisUiBinder extends UiBinder<Widget, MobilityVizHistoricalAnalysis> {
	}

	@UiField MobilityVizHistoricalAnalysisStyle style;

	/**
	 * View 1: Mobility-only analysis
	 * @param multiMobilityData
	 */
	public MobilityVizHistoricalAnalysis(List<List<MobilityInfo>> multiMobilityData) {
		// Init stuff
		initWidget(uiBinder.createAndBindUi(this));
		bind();

		// Load and render charts
		loadAndDisplayMobilityAnalysis(multiMobilityData);
	}
	
	/**
	 * View 2: Mobility and Survey Response analysis
	 * @param multiMobilityData
	 * @param responseData
	 */
	public MobilityVizHistoricalAnalysis(List<List<MobilityInfo>> multiMobilityData, List<SurveyResponse> responseData) {
		// Init stuff
		initWidget(uiBinder.createAndBindUi(this));
		bind();

		// Load and render charts
		loadAndDisplayMobilityResponseAnalysis(multiMobilityData, responseData);
	}

	private void bind() {
		// Nothing to bind
	}

	public void loadAndDisplayMobilityAnalysis(List<List<MobilityInfo>> multiDayData) {
		// TODO
		
		for (List<MobilityInfo> day : multiDayData) {
			Map<MobilityMode, Integer> durationMap = getModeDurations(day);
			Map<MobilityMode, Float> distanceMap = getModeDistances(day);
		}
	}
	
	public void loadAndDisplayMobilityResponseAnalysis(List<List<MobilityInfo>> multiMobilityData, List<SurveyResponse> responseData) {
		// TODO (LATER)
	}
	
	private Widget createSingleTimeAnalysisChart(String title) {
		// TODO
		return null;
	}
	
	private Widget createDualTimeAnalysisChart(String title) {
		// TODO
		return null;
	}
	
	private Widget createDistributionAnalysisChart(String title) {
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
