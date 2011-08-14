package edu.ucla.cens.mobilize.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.mobilize.client.ui.ErrorDialog;
import edu.ucla.cens.mobilize.client.ui.WaitIndicator;


public class LoginViewImpl extends Composite implements LoginView {
    @UiTemplate("LoginView.ui.xml")
    interface LoginBoxViewUiBinder extends UiBinder<Widget, LoginViewImpl> {}
    private static LoginBoxViewUiBinder uiBinder =
      GWT.create(LoginBoxViewUiBinder.class);
    
    // Fields defined in the ui XML
    @UiField TextBox userNameTextBox;
    @UiField PasswordTextBox passwordTextBox;
    @UiField Button loginButton;
    // dynamically fill html here depending on the installation
    @UiField Image logo;
    @UiField UListElement linkList;
    @UiField HTMLPanel linkListDiv;
    @UiField HTMLPanel descriptionPanel;
    
    private Presenter presenter;
    
    public LoginViewImpl() {
        initWidget(uiBinder.createAndBindUi(this)); 
        initEventHandlers();
    }
    
    private void initEventHandlers() {
      // clicking login button submits (note: pressing enter
      //  while login has focus also submits)
      loginButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          submit();
        }
      });
      
      // pressing enter in user name box submits
      userNameTextBox.addKeyPressHandler(new KeyPressHandler() {
        @Override
        public void onKeyPress(KeyPressEvent event) {
          if (event.getCharCode() == KeyCodes.KEY_ENTER) {
            submit();
          }
        }
      });
      
      // pressing enter in password box submits
      passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
        @Override
        public void onKeyPress(KeyPressEvent event) {
          if (event.getCharCode() == KeyCodes.KEY_ENTER) {
            submit();
          }
        }
      });
    }
   
    // verifies that both name and password are set then notifies presenter
    private void submit() {
      this.presenter.onSubmit();
    }
    
    /**
     * Sets the views presenter to call to handle events.
     * 
     * @param presenter The presenter to bind to.
     */
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLoginFailed(String msg) {
      ErrorDialog.show("Login failed.", msg);
    }

    @Override
    public String getUserName() {
      return userNameTextBox.getText();
    }

    @Override
    public String getPassword() {
      return passwordTextBox.getText();
    }

    @Override
    public void disableLoginForm() {
      loginButton.setEnabled(false);
      userNameTextBox.setEnabled(false);
      passwordTextBox.setEnabled(false);
      WaitIndicator.show();
    }

    @Override
    public void enableLoginForm() {
      loginButton.setEnabled(true);
      userNameTextBox.setEnabled(true);
      passwordTextBox.setEnabled(true);
      WaitIndicator.hide();
    }

    @Override
    public void showError(String errorMsg) {
      ErrorDialog.show(errorMsg);
    }

    @Override
    public void setAppName(String appName) {
      Window.setTitle(appName);
    }

    @Override
    public void setLogoUrl(String url) {
      logo.setUrl(url);
    }

    @Override
    public void setLinks(List<String> linkTexts, List<String> linkUrls) {
      assert linkTexts.size() == linkUrls.size() : "Number of link text strings must match number of urls";
      this.linkList.setInnerHTML(""); // clear existing links, if any
      for (int i = 0; i < linkTexts.size(); i++) {
        // create the link
        AnchorElement link = Document.get().createAnchorElement();
        link.setInnerText(linkTexts.get(i));
        link.setHref(linkUrls.get(i));
        // add link to a list element (<li>)
        LIElement listElement = Document.get().createLIElement();
        listElement.appendChild(link);
        // add list element to the list (<ul>)
        this.linkList.appendChild(listElement);
      }
    }

    @Override
    public void setDescriptionHtml(String loginPageHtml) {
      this.descriptionPanel.getElement().setInnerHTML(""); // clear existing content, if any
      this.descriptionPanel.add(new HTML(loginPageHtml));
    }

}
