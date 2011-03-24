package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.formatters.DateFormat;

import edu.ucla.cens.AndWellnessVisualizations.client.AndWellnessConstants;
import edu.ucla.cens.AndWellnessVisualizations.client.model.ChunkedMobilityAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityDataPointAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityListAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.model.MobilityLocationAwData;
import edu.ucla.cens.AndWellnessVisualizations.client.utils.DateUtils;
import edu.ucla.cens.AndWellnessVisualizations.client.widget.IFrameForm;

public class MobilityChartVisualizationViewImpl extends Composite 
	implements MobilityChartVisualizationView {
	/* Various constants used by the chart */
	// 1440 minutes in a day
	final private static int RANGE = 1440;
	final private static int WIDTH = 680;
	final private static int HEIGHT = 400;
	
	/* Google CHART API parameter names */
	final private static String CHART_TYPE = "cht";
	
	
    private static Logger _logger = Logger.getLogger(MobilityChartVisualizationViewImpl.class.getName());
    
	private Presenter presenter;
	
	// Our main chart widget
	IFrameForm frame = new IFrameForm("https://chart.googleapis.com/chart");
	
	// Stores the location data list
	private List<ChunkedMobilityAwData> locationData = null;
	
	public MobilityChartVisualizationViewImpl() {
		// Set some default chart parameters
		
		// Horizontal bar chart
		frame.setNameValue("cht", "bhs");
		// Sizing
		frame.setSize(WIDTH, HEIGHT);
		frame.setNameValue("chs", WIDTH + "x" + HEIGHT);
		// Bar width and spacing
		frame.setNameValue("chbh", "a,20,20");
		// Axis labels
		frame.setNameValue("chxt", "x,y");
		frame.setNameValue("chxl", "0:|12am|6am|noon|6pm|12am|1:|Sun|Sat|Fri|Thu|Wed|Tue|Mon");
		// Legend
		frame.setNameValue("chdl", "No Data|Still|Walk|Run|Bike|Drive");
		// Text formatting
		frame.setNameValue("chxs", "0,000000,13,0,t|1,000000,13,0,t");
		// Ranging
		frame.setNameValue("chds", "0," + RANGE);
		// Grid lines
		//frame.setNameValue("chg", "25,0,2,2");
		
		frame.setBorder(false);
		
		initWidget(frame);
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	private enum Day {
		Mon(1), Tue(2), Wed(3), Thu(4), Fri(5), Sat(6), Sun(0);
		
		private int dayCode;
	
		private Day(int code) {
			dayCode = code;
		}
		
		public int getCode() {
			return dayCode;
		}
	
		public boolean isLastDay() {
			Day[] dayList = Day.values();
			
			if (dayList[dayList.length - 1].equals(this))
				return true;
			
			return false;
		}
		
		public boolean isFirstDay() {
			Day[] dayList = Day.values();
			
			if (dayList[0].equals(this))
				return true;
			
			return false;
		}
	}
	
	private enum Mode {
		none("DCDCDC"), still("FFC6A5"), walk("FFFF42"), run("DEF3BD"), bike("00A5C6"), drive("DEBDDE");
		
		private String color;
		
		private Mode(String color) {
			this.color = color;
		}
		
		public String getColor() {
			return color;
		}
		
		public static String lookupColor(String modeName) {
			for (Mode mode: Mode.values()) {
				if (modeName.equals(mode.toString())) {
					return mode.getColor();
				}
			}
			
			throw new NoSuchElementException(modeName + " not found");
		}
	}
	
	private class ChartData {
		public int length;
		// Default to white
		public String color;
		
		public ChartData(int _length, String _color) {
			length = _length;
			color = _color;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setDataList(List<ChunkedMobilityAwData> dataList) {
		this.locationData = dataList;
		
		_logger.fine("Received " + dataList.size() + " chunked mobility points.");
		
		// Data point sorted by day
		Map<Integer, List<ChunkedMobilityAwData>> dayMobilityData = new HashMap<Integer, List<ChunkedMobilityAwData>>();
		
		// Data point day bins
		Map<Integer, List<ChartData>> dayParsedData = new HashMap<Integer, List<ChartData>>();
		
		// Init the bins
		for (Day d: Day.values()) {
			dayMobilityData.put(d.getCode(), new ArrayList<ChunkedMobilityAwData>());
			dayParsedData.put(d.getCode(), new ArrayList<ChartData>());
		}
		
		// Now render these data points
		
		// Move this out somewhere later, way too much server/andwellness specific code here
		
		// Basically, we are chunking every datapoint into slots by the day of the week
		// Every data point will be represented as a section of a bar during its day.
		// Bar charts are NOT meant to be used this way, so we have to do some hackiness
		// with colors to get everything to look right.
		
		// The data MUST come sorted by time and MUST be no more than one week.  For example,
		// if there is data from Sun,Mon,Tue,Wed,Thu,Fri,Sat,Sun, then that Sunday will
		// be undefined (and probably look really wonky if it works at all)
		
		// First, let's parse the data into bins by day, then we can go back and construct
		// the color (chco) and data (chd) strings.
		
		// While I'm here, here is a bit of tech background on google charts
		// The chart will be split into seven bars, one for each day of the week.  This makes
		// the data (chd) format: 
		// 
		// t:Mon1,Tue1,Wed1,Thu1,Fri1,Sat1,Sun1|Mon2,Tue2,Wed2,Thu2,Fri2,Sat2,Sun2|...
		// 
		// We need all the first data points from each day, then all the second, etc.
		// which means all days need to be precalculated, we cannot simply go in sorted data
		// order.
		//
		// Also, because we are using the bar charts this way, we need to color each individual
		// data point with a 6 character hex color (which is why we need the 16k POST vs 2k GET).
		
		
		// Put into the bins
		for (ChunkedMobilityAwData data: dataList) {
			// Find the day
			Date day;
			try {
				day = DateUtils.translateFromServerFormat(data.getTimeStamp());
			}
			catch (NullPointerException e) {
				_logger.severe("Cannot find time stamp in chunked mobility data");
				continue;
			}
			dayMobilityData.get(day.getDay()).add(data);
		}
		
		// Sort the bins
		for (List<ChunkedMobilityAwData> dataLists: dayMobilityData.values()) {
			Collections.sort(dataLists, new Comparator<ChunkedMobilityAwData>() {
				// Sort by timestamp
				public int compare(ChunkedMobilityAwData o1, ChunkedMobilityAwData o2) {
					Date d1 = DateUtils.translateFromServerFormat(o1.getTimeStamp());
					Date d2 = DateUtils.translateFromServerFormat(o2.getTimeStamp());
					
					return d1.compareTo(d2);
				}
			});
		}
		
		// Now move the bins into the parsed bins.  Each day must have exactly 86400 seconds of data.
		for (Day d: Day.values()) {
			List<ChunkedMobilityAwData> dayList = dayMobilityData.get(d.getCode());
			List<ChartData> parsedList = dayParsedData.get(d.getCode());
			
			// Keep track of where we are in the day
			int curTime = 0;
			int prevEndTime = 0;
			for (ChunkedMobilityAwData dayDataPoint: dayList) {
				Date start = DateUtils.translateFromServerFormat(dayDataPoint.getTimeStamp());
				// Get the duration in minutes
				int durationInMin = dayDataPoint.getDuration() / 1000 / 60;
				
				// Find where this data point starts
				curTime = DateUtils.secsIntoDay(start) / 60;
				
				// Sanity check, this shouldn't happen
				if (curTime < prevEndTime) {
					_logger.warning("Found mode starting at " + start + "but we are already past that time.");
					continue;
				}
				
				// If curTime is after the previous end time, add a slot for unknown
				// If this is less than 5 minutes, don't add a buffer
				// This has the effect of both removing small "none" spaces and merging two
				// chunks of the same mode that are next to each other.
				if (curTime > prevEndTime + 5) {
					appendChartData(parsedList, curTime - prevEndTime, Mode.none.getColor());
					
					_logger.finer("Adding " + (curTime - prevEndTime) + " of padding to " + d);
					
					prevEndTime += (curTime - prevEndTime);
				}
				
				// Find the max mode for this data point
				int maxModeCount = 0;
				String maxMode = "none";
				Map<String, Integer> modeMap = dayDataPoint.getMode().getModes();
				for (String mode: modeMap.keySet()) {
					int curModeCount = modeMap.get(mode).intValue();
					if (curModeCount > maxModeCount) {
						maxModeCount = curModeCount;
						maxMode = mode;
					}
				}
				
				_logger.finer("Parsing: mode " + maxMode + " start " + start + " duration " + durationInMin);
				
				// Check for day overflow, this shouldn't span more than one day
				if (curTime + durationInMin > RANGE) {
					int todayTime = (curTime + durationInMin) - RANGE;
					int tomorrowTime = durationInMin - todayTime;
					
					// Add time to today
					parsedList.add(new ChartData(todayTime, Mode.lookupColor(maxMode)));
					
					_logger.finer("Adding " + todayTime + " " + maxMode + " to " + d);
					
					// Add time to tomorrow (this should be ok, but check for null anyway)
					List<ChartData> tomorrowParsedList = dayParsedData.get(d.getCode() + 1);
					if (tomorrowParsedList == null) {
						_logger.warning("Cannot find the day after " + d.toString());
					}
					else {
						dayParsedData.get(d.getCode() + 1).add(new ChartData(tomorrowTime, Mode.lookupColor(maxMode)));
						
						_logger.fine("Adding " + tomorrowTime + " to tomorrow");
					}
					
					prevEndTime += todayTime;
				}
				else {
					// Insert into the list
					int time = (curTime - prevEndTime) + durationInMin;
					
					appendChartData(parsedList, time, Mode.lookupColor(maxMode));
					
					_logger.finer("Adding " + time + " " + maxMode + " to " + d);
					
					prevEndTime += time;
				}				
			}
			
			// Fill out the remainder of the day
			if (prevEndTime < RANGE) {
				parsedList.add(new ChartData(RANGE - prevEndTime, Mode.none.getColor()));
				
				_logger.finer("Filling remainder of day with padding of " + (RANGE - prevEndTime));
			}
		}
		
		// Now, translate the days into data point strings
		StringBuffer chco = new StringBuffer();
		StringBuffer chd = new StringBuffer();
		
		// First, append 7 fake points to make the legend colors ok
		chco.append(Mode.none.getColor() + "|" + 
				Mode.still.getColor() + "|" + 
				Mode.walk.getColor() + "|" + 
				Mode.run.getColor() + "|" + 
				Mode.bike.getColor() + "|" + 
				Mode.drive.getColor() + ",");
		chd.append("t:0,0,0,0,0,0,0|");
		
		// Find the day with the max number of modes
		int numMaxModes = 0;
		for (Day d: Day.values()) {
			int curModes = dayParsedData.get(d.getCode()).size();
			
			if (curModes > numMaxModes) {
				numMaxModes = curModes;
			}
		}
		
		// We now have to "stack" the bars in the days.  This must be done starting at the "bottom" of
		// each day and one by one moving upwards.
		for (int i = 0; i < numMaxModes; ++i) {
			for (Day d: Day.values()) {
				List<ChartData> parsedList = dayParsedData.get(d.getCode());
				
				try {
					ChartData data = parsedList.get(i);
					
					// If this is NOT the first day, add separators
					if (! d.isFirstDay()) {
						chco.append("|");
						chd.append(",");
					}
					
					chco.append(data.color);
					chd.append(data.length);
					
					
				}
				// This is ok, this day must just be smaller than the others
				catch (IndexOutOfBoundsException e) {
					// If this is NOT the first day, add separators
					if (! d.isFirstDay()) {
						chco.append("|");
						chd.append(",");
					}
					
					chco.append(Mode.none.getColor());
					chd.append("0");
					
					continue;
				}
			}
			
			// Move to the next "level" of bars
			if (i != numMaxModes - 1) {
				chco.append(",");
				chd.append("|");
			}
		}
		
		_logger.finer("chd: " + chd.toString());
		_logger.finer("chco: " + chco.toString());
		
		// We are done, let's hope this works
		frame.setNameValue("chco", chco.toString());
		frame.setNameValue("chd", chd.toString());
		
		// Check the length first
		int frameLength = frame.length();
		if (frameLength > 16000) {
			_logger.severe("Too many characters in the POST: " + frameLength);
			
			// Reset the frame
			String data = "t:0,0,0,0,0,0,0";
			String color = Mode.none.getColor() + "|" + 
			Mode.still.getColor() + "|" + 
			Mode.walk.getColor() + "|" + 
			Mode.run.getColor() + "|" + 
			Mode.bike.getColor() + "|" + 
			Mode.drive.getColor();
			
			frame.setNameValue("chco", color);
			frame.setNameValue("chd", data);
			
			frame.submit();
		}
		else {
			_logger.fine("Submitting POST with character length: " + frameLength);
			frame.submit();
		}
	}
	
	public Widget asWidget() {
		return this;
	}
	
	private void appendChartData(List<ChartData> list, int duration, String color) {
		// First check to see if the previous ChartData has the same mode
		if (! list.isEmpty()) {
			ChartData prevMode = list.get(list.size() - 1);
			if (prevMode.color.equals(color)) {
				// Just update the previous node instead
				prevMode.length += (duration); 
			}
			else
				list.add(new ChartData(duration, color));
		}
		else {
			list.add(new ChartData(duration, color));
		}
	}
	
	/**
	 * Initialize the chd and chco parameters to make the legend colors work correctly
	 */
	private void initLegendData(StringBuffer chd, StringBuffer chco) {
		
	}
}
