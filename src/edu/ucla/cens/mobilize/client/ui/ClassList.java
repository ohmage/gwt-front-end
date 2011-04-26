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

  @UiField ListBox districtListBox;
  @UiField ListBox supervisorListBox;
  @UiField Grid classGrid;
  
  public ClassList() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  public void setDistricts(List<String> districts) {
  }
  
  public void setSupervisors(List<String> supervisors) {
  }
  
  public void setClasses(List<ClassInfo> classes) {
    this.classGrid.clear();
    this.classGrid.resize(classes.size() + 1, 6); // +1 for header row
    for (int i = 0; i < classes.size(); i++) {
      ClassInfo classInfo = classes.get(i);
      InlineHyperlink classDetailLink = new InlineHyperlink(classInfo.getClassName(), 
                                                            HistoryTokens.classDetail(classInfo.getClassId()));
      this.classGrid.setWidget(i, 0, classDetailLink);
      this.classGrid.setText(i, 1, classInfo.getDistrict());
      this.classGrid.setText(i, 2, classInfo.getSchool());
      this.classGrid.setText(i, 3, classInfo.getTerm());
      this.classGrid.setText(i, 4, Integer.toString(classInfo.getYear()));
    }
  }

}
