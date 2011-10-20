package edu.ucla.cens.mobilize.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;

public class ClassList extends Composite {

  private static ClassListUiBinder uiBinder = GWT
      .create(ClassListUiBinder.class);

  interface ClassListUiBinder extends UiBinder<Widget, ClassList> {
  }

  @UiField Grid classGrid;
  
  public ClassList() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  /**
   * Load two-column list of class names that link to class detail pages
   * @param classIdToNameMap (Treated as empty list if null)
   */
  public void setClasses(Map<String, String> classIdToNameMap) {
    this.classGrid.clear();
    if (classIdToNameMap != null && classIdToNameMap.size() > 0) {
      int numColumns = 3;
      int numRows = (classIdToNameMap.size() / numColumns) + 1; // int division, +1 for remainder
      this.classGrid.resize(numRows, numColumns);
      int row = 0;
      int col = 0;
      List<String> sortedIds = getClassIdsSortedByClassName(classIdToNameMap);
      for (String classId : sortedIds) {
        String className = classIdToNameMap.get(classId);
        InlineHyperlink classDetailLink = new InlineHyperlink(className,
                                                              HistoryTokens.classDetail(classId));
        this.classGrid.setWidget(row, col, classDetailLink);
        
        // Calc row/col for the next iteration. If current col is full, move to next.
        if (++row == numRows) { row = 0; col++; } 
      }
    }
  }
  
  private List<String> getClassIdsSortedByClassName(Map<String, String> classIdToNameMap) {
    List<String> sortedIds = new ArrayList<String>();
    List<String> nameIdPairs = new ArrayList<String>();
    String sep = "###";
    for (String classId : classIdToNameMap.keySet()) {
      nameIdPairs.add(classIdToNameMap.get(classId) + sep + classId);
    }
    Collections.sort(nameIdPairs);
    for (String nameIdPair : nameIdPairs) {
      sortedIds.add(nameIdPair.split(sep)[1]); // copy over the id
    }
    return sortedIds;
  }
  
}
