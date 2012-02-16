package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.ucla.cens.mobilize.client.common.MobilityMode;
import edu.ucla.cens.mobilize.client.model.MobilityInfo;

public class MobilityUtils {
	//private static Logger _logger = Logger.getLogger(AwDataTranslators.class.getName());
	
	private final static int MINUTES_IN_DAY = 24*60;
	
	/**
	 * @param date Date to calculate minutes
	 * @return Elapsed time in minutes for the single day
	 */
	public static int getTimeInMinutes(Date date) {
		return (60*date.getHours() + date.getMinutes());
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static Map<MobilityMode, Integer> estimatedDurationOfModes(final List<MobilityInfo> data) {
		final int DEFAULT_INTERVAL = 2;	// Minutes
		final List<MobilityMode> bucketed = bucketByInterval(data, DEFAULT_INTERVAL);
		
		Map<MobilityMode, Integer> durations = new HashMap<MobilityMode, Integer>();
		for (MobilityMode m : bucketed) {
			addKeyValueToModeMap(durations, m, DEFAULT_INTERVAL);
		}
		
		return durations;
	}
	
	/**
	 * Buckets your single-day, time-sorted list of MobilityInfo by a specified interval in minutes
	 * @param data Time-sorted list of MobilityInfo for a single day of data
	 * @param intervalInMinutes Interval/resolution to bucket the data
	 * @return List of MobilityModes, where each item represents the time interval starting from midnight (00:00)
	 */
	public static List<MobilityMode> bucketByInterval(final List<MobilityInfo> data, final int intervalInMinutes) {
		// Validate parameters
		if (data == null || data.size() == 0 || intervalInMinutes <= 0) {
			return null;	//FIXME
		}
		
		int intervals = MINUTES_IN_DAY / intervalInMinutes;
		//int overflow = MINUTES_IN_DAY % intervalInMinutes;	// NOTE: This should be handled by method callee
		
		List<MobilityMode> bucketedModes = new ArrayList<MobilityMode>();
		List<MobilityInfo> bucket = new ArrayList<MobilityInfo>();
		Map<MobilityMode, Integer> votes = new HashMap<MobilityMode, Integer>();
		
		// What we're doing:
		// 
		// 
		// 
		// 
		
		int curIndex = 0;
		for (int i = 0; i < intervals; i++) {
			MobilityInfo prevData = null;
			bucket.clear();
			votes.clear();
			
			// Fill up bucket with mobility data within current interval
			while (curIndex < data.size()) {
				MobilityInfo m = data.get(curIndex);
				
				// Compute the day's mobility time in minutes
				int curTimeInMin = getTimeInMinutes(m.getDate());

				// Check if the current mobility point is within the current interval
				if (curTimeInMin < i * intervalInMinutes) {
					bucket.add(m);
					curIndex++;
				} else {
					prevData = m;
					break;
				}
			}
			
			// Count the durations
			for (int j = 0; j < bucket.size(); j++) {
				// Case 1: Leading duration prior to first data point
				// Case 2: Tailing data point duration
				// Case 3: Calculate mid-progress duration votes
				
				if (j == 0 && prevData != null) {
					// Case 1: head
					addKeyValueToModeMap(votes, prevData.getMode(), getTimeInMinutes(prevData.getDate()));
					prevData = null;	// Clear this just to be safe
				}
				
				if (j == bucket.size() - 1) {	// NOTE: This should NOT be an else-if to handle the case where there is only 1 data point in bucket
					// Case 2: tail
					int remainingTime = intervalInMinutes - getTimeInMinutes(bucket.get(j).getDate());
					addKeyValueToModeMap(votes, bucket.get(j).getMode(), remainingTime);
				} else {
					// Case 3: middle
					int elapsedTime = getTimeInMinutes(bucket.get(j+1).getDate()) - getTimeInMinutes(bucket.get(j).getDate());
					addKeyValueToModeMap(votes, bucket.get(j).getMode(), elapsedTime);
				}
			}
			
			// Determine highest vote; if any ties, just choose first one
			Map.Entry<MobilityMode, Integer> maxEntry = null;
			for (Map.Entry<MobilityMode, Integer> entry : votes.entrySet()) {
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
					maxEntry = entry;
				}
			}
			
			// Add to bucketedModes
			if (maxEntry != null) {
				bucketedModes.add(maxEntry.getKey());
			} else {	// If no data for this interval, we default to "ERROR"
				bucketedModes.add(MobilityMode.ERROR);
			}
		}
		
		return bucketedModes;
	}
	
	/**
	 * @param votes Map of mobility mode votes (in minutes)
	 * @param key New or existing MobilityMode
	 * @param valueToAdd Integer value to add
	 */
	private static void addKeyValueToModeMap(Map<MobilityMode,Integer> votes, MobilityMode key, int valueToAdd) {
		if (votes == null || key == null || valueToAdd <= 0)
			return;
		
		if (votes.containsKey(key))
			votes.put(key, votes.get(key) + valueToAdd);
		else
			votes.put(key, valueToAdd);
	}
}
