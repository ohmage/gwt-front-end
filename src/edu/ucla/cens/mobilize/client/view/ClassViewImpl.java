package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;


public class ClassViewImpl extends Composite implements ClassView {

  private static ClassViewUiBinder uiBinder = GWT
  .create(ClassViewUiBinder.class);
  
  @UiTemplate("ClassView.ui.xml")
  interface ClassViewUiBinder extends UiBinder<Widget, ClassViewImpl> {
  }
  
  public ClassViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public void setPresenter(Presenter presenter) {
    // TODO Auto-generated method stub
    
  }

}
