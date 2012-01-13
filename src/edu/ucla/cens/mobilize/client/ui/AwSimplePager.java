package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.Range;

/**
 * Subclass to override unexpected behavior in the cellview SimplePager.
 * This class can be discarded if the problem is fixed in a newer version of GWT. 
 * 
 * http://stackoverflow.com/questions/8391646/gwt-celltable-with-simple-pager-issue
 * http://code.google.com/p/google-web-toolkit/issues/detail?id=6163 
 */
public class AwSimplePager extends SimplePager {
  public AwSimplePager(SimplePager.TextLocation location) {
    super(location);
  }
  
  public AwSimplePager(SimplePager.TextLocation location, 
                       SimplePager.Resources resources, 
                       boolean showFastForwardButton, 
                       int fastForwardRows, 
                       boolean showLastPageButton) {
    super(location, resources, showFastForwardButton, fastForwardRows, showLastPageButton);
  }
  
  @Override
  public void setPageStart(int index) {
    if (this.getDisplay() != null) {
      Range range = this.getDisplay().getVisibleRange();
      int pageSize = range.getLength();
//      if (isRangeLimited && display.isRowCountExact()) {
//        index = Math.min(index, display.getRowCount() - pageSize);
//      }
      index = Math.max(0, index);
      if (index != range.getStart()) {
        this.getDisplay().setVisibleRange(index, pageSize);
      }
    }
  }
}
