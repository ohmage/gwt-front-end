package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AccountView extends Composite {

  private static AccountViewUiBinder uiBinder = GWT
      .create(AccountViewUiBinder.class);

  interface AccountViewUiBinder extends UiBinder<Widget, AccountView> {
  }

  public AccountView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
