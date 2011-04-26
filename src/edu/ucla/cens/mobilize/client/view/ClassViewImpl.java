package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

import edu.ucla.cens.mobilize.client.model.ClassInfo;
import edu.ucla.cens.mobilize.client.ui.ClassDetail;
import edu.ucla.cens.mobilize.client.ui.ClassEditForm;
import edu.ucla.cens.mobilize.client.ui.ClassList;


public class ClassViewImpl extends Composite implements ClassView {

  private static ClassViewUiBinder uiBinder = GWT
  .create(ClassViewUiBinder.class);
  
  @UiTemplate("ClassView.ui.xml")
  interface ClassViewUiBinder extends UiBinder<Widget, ClassViewImpl> {
  }
  
  @UiField ClassList classList;
  @UiField ClassDetail classDetail;
  @UiField ClassEditForm classEdit;
  
  ClassView.Presenter presenter;
  
  public ClassViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  private void hideAllWidgets() {
    classList.setVisible(false);
    classDetail.setVisible(false);
    classEdit.setVisible(false);
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void showList(List<ClassInfo> classes) {
    hideAllWidgets();
    // TODO: should also set schools, supervisors, etc
    classList.setClasses(classes);
    classList.setVisible(true);
  }

  @Override
  public void showDetail(ClassInfo classDetail) {
    hideAllWidgets();
    this.classDetail.setVisible(true);
  }

  @Override
  public void showEditForm(ClassInfo classDetail) {
    hideAllWidgets();
    this.classEdit.setVisible(true);
  }

  @Override
  public void showError(String msg) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void showMsg(String msg) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void hideMsg() {
    // TODO Auto-generated method stub
    
  }

}
