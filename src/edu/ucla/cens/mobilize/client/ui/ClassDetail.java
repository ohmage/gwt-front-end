package edu.ucla.cens.mobilize.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.model.ClassInfo;

public class ClassDetail extends Composite {

  private static ClassDetailUiBinder uiBinder = GWT
      .create(ClassDetailUiBinder.class);

  interface ClassDetailUiBinder extends UiBinder<Widget, ClassDetail> {
  }

  @UiField InlineLabel className;
  @UiField InlineLabel classUrn;
  @UiField TextArea descriptionTextArea;
  @UiField VerticalPanel supervisorsVerticalPanel;
  @UiField VerticalPanel membersVerticalPanel;
  
  public ClassDetail() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  public void setClass(ClassInfo classDetail) {
    this.className.setText(classDetail.getClassName());
    this.classUrn.setText(classDetail.getClassId());
    
  }

}
