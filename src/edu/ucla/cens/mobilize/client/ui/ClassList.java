package edu.ucla.cens.mobilize.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.model.ClassInfo;

public class ClassList extends Composite {

  private static ClassListUiBinder uiBinder = GWT
      .create(ClassListUiBinder.class);

  interface ClassListUiBinder extends UiBinder<Widget, ClassList> {
  }

  @UiField Grid classGrid;
  
  public ClassList() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  // NOTE: it's ok for classes to be null - treated same as empty list
  public void setClasses(List<ClassInfo> classes) {
    this.classGrid.clear();
    if (classes != null && classes.size() > 0) {
      this.classGrid.resize(classes.size() + 1, 6); // +1 for header row
      for (int i = 0; i < classes.size(); i++) {
        ClassInfo classInfo = classes.get(i);
        InlineHyperlink classDetailLink = new InlineHyperlink(classInfo.getClassName(), 
                                                              HistoryTokens.classDetail(classInfo.getClassId()));
        this.classGrid.setWidget(i, 0, classDetailLink);
      }
    } 
  }

}
