/*
 * Original source (c) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Modification to MarkerClusterer v2 GWT wrapper to work with vinay's gwt maps v3 implementation
 * 
 * @author ewang9
 */

package edu.ucla.cens.mobilize.client.utils;

import java.util.Arrays;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.maps.client.Map;
import com.google.gwt.maps.client.overlay.Marker;

/**
 * Marker clusterer creates and manages per-zoom-level clusters for large
 * amounts of markers (hundreds or thousands).
 */
public class MarkerClusterer extends JavaScriptObject {

  /**
   * Constructs a new MarkerClusterer to cluster markers on the map.
   * 
   * @param map The map widget to manage.
   * @return A MarkerClusterer object.
   */
  public static native MarkerClusterer newInstance(Map map) /*-{
    return new $wnd.MarkerClusterer(map);
  }-*/;
  
  /**
   * Constructs a new MarkerClusterer to cluster markers on the map.
   * 
   * @param map The map widget to manage.
   * @param markers The markers to add.
   * @return A MarkerClusterer object.
   */
  public static native MarkerClusterer newInstance(Map map, Marker[] markers) /*-{
  	var mcOptions = {
  		averageCenter: true,
  		gridSize: 30,
  		maxZoom: 14,
  		minimumClusterSize: 6,
  		title: 'Click to expand this marker group'
  	};
    return new $wnd.MarkerClusterer(map, markers, mcOptions);
  }-*/;
  
  /**
   * Constructs a new MarkerClusterer to cluster markers on the map.
   * Proceed with caution when modifying any javascript code here.
   * TODO: Clean up this hacky code!
   * 
   * @param map The map widget to manage.
   * @param markers The markers to add.
   * @param toggleClusterColor True for red cluster color, False for gray cluster color.
   * @return A MarkerClusterer object.
   */
  public static native MarkerClusterer newInstance(Map map, Marker[] markers, boolean toggleClusterColor) /*-{
    var markerStyles = [[{
        url: 'images/markerclusterer_icons/red1.png',
        height: 45,
        width: 46,
        anchor: [17,0],
        textColor: '#000000',
        textSize: 10
      }, {
        url: 'images/markerclusterer_icons/red2.png',
        height: 55,
        width: 56,
        anchor: [22,0],
        textColor: '#000000',
        textSize: 11
      }, {
        url: 'images/markerclusterer_icons/red3.png',
        height: 65,
        width: 66,
        anchor: [26,0],
        textColor: '#000000',
        textSize: 12
      }],
      [{
        url: 'images/markerclusterer_icons/neutral46.png',
        height: 45,
        width: 46,
        anchor: [17,0],
        textColor: '#000000',
        textSize: 10
      }, {
        url: 'images/markerclusterer_icons/neutral56.png',
        height: 55,
        width: 56,
        anchor: [22,0],
        textColor: '#000000',
        textSize: 11
      }, {
        url: 'images/markerclusterer_icons/neutral66.png',
        height: 65,
        width: 66,
        anchor: [26,0],
        textColor: '#000000',
        textSize: 12
      }]];
    
    if (toggleClusterColor == true)
    	var markerStyle = markerStyles[0];
	else
		var markerStyle = markerStyles[1];
    
  	var mcOptions = {
  		averageCenter: true,
  		gridSize: 30,
  		maxZoom: 15,
  		minimumClusterSize: 10,
  		title: 'Click to expand this marker cluster',
  		styles: markerStyle
  	};
  	
  	var markerCluster = new $wnd.MarkerClusterer(map, markers, mcOptions);
  	
  	// only show mouseover infowindow for mobility
  	if (toggleClusterColor == false) {
	  	//handle mouseover event
	  	$wnd.google.maps.event.addListener(markerCluster, "mouseover", function (cluster) {
	  	      // Convert lat/long from cluster object to a usable MVCObject
	          var info = new $wnd.google.maps.MVCObject;
	          info.set('position', cluster.center_);
	          
	          // Get markers in the cluster
	          var markersInCluster = cluster.getMarkers();
	          
	          //var counts = {};
	          //for (var i=0; i < markersInCluster.length; ++i) {
	          //    var key = markersInCluster[i].getTitle();
	          //    if (counts[key] == undefined) {
	          //        counts[key] = 1;
	          //    } else {
	          //        counts[key] += 1;
	          //    }
	          //}
	          
	          var counts = {};
	          for (var i=0; i < markersInCluster.length; ++i) {
	              var key = markersInCluster[i].getTitle();
	              var valueToAdd = 1;
	              
	              //NOTE: THIS DURATION ESTIMATION IS EXTREMELY HACKY
	              var duration = markersInCluster[i]['mobility_duration'];
	              if (duration !== "undefined" && typeof duration == "number") {
	                  valueToAdd = duration;
	              }
	              
	              if (counts[key] == undefined) {
	                  counts[key] = valueToAdd;
	              } else {
	                  counts[key] += valueToAdd;
	              }
	          }
	          
	          var content = 'There are <b>' + markersInCluster.length + '</b> markers in this cluster.<br/><br/>';
	          content += "Here's the estimated time distribution (hh:mm'):<br/>";
	          content += "<table cellspacing=\"10\"><tr>";
	          for (var key in counts) {
	              content += "<td align=\"center\">";
	              content += "<b>" + key.toUpperCase() + "</b><br/>";
	              
	              switch (key.toLowerCase()) {
	              	case 'still':  content += "<img src=\"images/mobility/m_still.png\" /><br/>"; break;
	              	case 'walk':   content += "<img src=\"images/mobility/m_walk.png\" /><br/>"; break;
	              	case 'run':    content += "<img src=\"images/mobility/m_run.png\" /><br/>"; break;
	              	case 'bike':   content += "<img src=\"images/mobility/m_bike.png\" /><br/>"; break;
	              	case 'drive':  content += "<img src=\"images/mobility/m_drive.png\" /><br/>"; break;
	              	case 'error':  content += "<img src=\"images/mobility/m_error.png\" /><br/>"; break;
	              	default:       break;
	              }
	              
	              var str = "";
	              str += parseInt(counts[key]/60) + ":";
	              str += (counts[key]%60 >= 10) ? (counts[key]%60) : "0" + (counts[key]%60);
              	  str += "\'";
              	  content += "<b>" + str + "</b>";
	              //content += "<b>" + counts[key] + "</b>";
	              content += "</td>";
	          }
	          content += "</tr></table>";
	  
	          // Store infowindow as global in $wnd
	          if (!$wnd.infowindow) {
		          $wnd.infowindow = new $wnd.google.maps.InfoWindow();
		          $wnd.infowindow.setOptions({ pixelOffset: new $wnd.google.maps.Size(12,-12) });
	          }
	          $wnd.infowindow.setContent(content); //set infowindow content to titles
	          $wnd.infowindow.open(map, info);
	        });
	    
	    // Hide/reset infowindows on mouseout, click, or zoom events
	    $wnd.google.maps.event.addListener(markerCluster, "mouseout", function (cluster) {
	          if ($wnd.infowindow) {
	              $wnd.infowindow.close();
	          }
	        });
	    $wnd.google.maps.event.addListener(markerCluster, "click", function (cluster) {
	          if ($wnd.infowindow) {
	              $wnd.infowindow.close();
	          }
	        });
	    $wnd.google.maps.event.addListener(map, 'zoom_changed', function() {
              if ($wnd.infowindow) {
	              $wnd.infowindow.close();
	          }
            });
  	}
  	
    return markerCluster;
  }-*/;

  protected MarkerClusterer() { }

  /**
   * Adds a set of markers.
   * 
   * @param markers The markers to add.
   */
  public final native void addMarkers(Marker[] markers) /*-{
    this.addMarkers(markers);
  }-*/;
  
  /**
   * Removes all markers from MarkerClusterer.
   */
  public final native void clearMarkers() /*-{
    this.clearMarkers();
  }-*/;
  
  /**
   * Retrieves the total number of clusters.
   * 
   * @return The total number of clusters.
   */
  public final native int getTotalClusters() /*-{
    return this.getTotalClusters();
  }-*/;
  
  /**
   * Retrieves the total number of markers.
   * 
   * @return The total number of markers.
   */
  public final native int getTotalMarkers() /*-{
    return this.getTotalMarkers();
  }-*/;
}