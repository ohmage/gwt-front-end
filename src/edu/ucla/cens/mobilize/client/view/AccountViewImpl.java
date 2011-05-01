package edu.ucla.cens.mobilize.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.model.UserInfo;

public class AccountViewImpl extends Composite implements AccountView {

  private static AccountViewUiBinder uiBinder = GWT
      .create(AccountViewUiBinder.class);

  @UiTemplate("AccountView.ui.xml")
  interface AccountViewUiBinder extends UiBinder<Widget, AccountViewImpl> {
  }
  
  public AccountViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public void setPresenter(Presenter presenter) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void showUserDetails(String login, String email) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void showPasswordChangeForm() {
    // TODO Auto-generated method stub
    
  }

}
