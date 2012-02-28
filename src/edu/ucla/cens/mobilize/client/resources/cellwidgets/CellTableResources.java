package edu.ucla.cens.mobilize.client.resources.cellwidgets;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;

public interface CellTableResources extends Resources {
  
  interface CellTableStyle extends CellTable.Style {}
  
  @Override
  @Source("CellTable.css")
  CellTableStyle cellTableStyle();
}
