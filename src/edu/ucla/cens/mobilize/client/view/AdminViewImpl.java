package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.common.HistoryTokens;
import edu.ucla.cens.mobilize.client.ui.AdminMenu;


public class AdminViewImpl extends Composite implements AdminView {

  private static AdminViewUiBinder uiBinder = GWT
      .create(AdminViewUiBinder.class);

  @UiField AdminMenu adminMenu;
  @UiField HorizontalPanel adminDashboard;
  
  @UiTemplate("AdminView.ui.xml")
  interface AdminViewUiBinder extends UiBinder<Widget, AdminViewImpl> {
  }

  public AdminViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
    this.adminMenu.selectAdminHome();
  }

}
