package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.sample.contacts.client.common.ColumnDefinition;
import com.google.gwt.sample.contacts.client.view.ContactsViewImpl;
import com.google.gwt.sample.contacts.client.view.ContactsView.Presenter;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

import edu.ucla.cens.AndWellnessVisualizations.client.presenter.DataFilterPresenter;

/**
 * Widget that displays a data selection filter.
 * 
 * @author jhicks
 *
 */
public class DataFilterViewImpl<T> extends Composite implements DataFilterView<T> {
    @UiTemplate("DataFilterView.ui.xml")
    interface DataFilterViewUiBinder extends UiBinder<Widget, DataFilterViewImpl> {}
    private static DataFilterViewUiBinder uiBinder =
      GWT.create(DataFilterViewUiBinder.class);

    @UiField FlexTable entryTable;
    @UiField Button goButton;

    private Presenter<T> presenter;
    private List<ColumnDefinition<T>> columnDefinitions;
    private List<T> rowData;
    
    public DataFilterViewImpl() {
      initWidget(uiBinder.createAndBindUi(this));
    }
    
    public void setPresenter(Presenter<T> presenter) {
        this.presenter = presenter;
    }
    
    public void setColumnDefinitions(
        List<ColumnDefinition<T>> columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
    }
    
    
/*    
    private final DatePicker endDate;
    private ListBox numDays;
    private FlexTable entryTable;
    private final Button sendButton;
    private ListBox userList;
    private boolean isUserListHidden;
    
    // Constructor, setup the DataFilter view with default values
    // ..and call for a list of user names if applicable
    public DataFilterViewImpl() {
        // The user list is hidden by default
        isUserListHidden = true;
        
        SimplePanel dataFilterDecorator = new SimplePanel();
        dataFilterDecorator.setWidth("150px");
        initWidget(dataFilterDecorator);
        // Shove everything into a vertical styled panel
        VerticalPanel dataFilterPanel = new VerticalPanel();
        dataFilterPanel.setWidth("100%");
        
        // Create the entry table
        entryTable = new FlexTable();
        entryTable.setCellSpacing(0);
        entryTable.setWidth("100%");
        entryTable.addStyleName("contacts-ListContainer");
        entryTable.getColumnFormatter().addStyleName(1, "add-contact-input");
        
        endDate = new DatePicker();
        endDate.setValue(new Date());
        
        numDays = new ListBox();
        numDays.setVisibleItemCount(1);
        numDays.addItem("1 week", "7");
        numDays.addItem("2 weeks", "14");
        numDays.addItem("3 weeks", "21");
        numDays.addItem("4 weeks", "28");
        numDays.setSelectedIndex(1);
        
        // Initialize the user list even though we don't show it at first
        userList = new ListBox();
        userList.setVisibleItemCount(1);
        initEntryTable();
        dataFilterPanel.add(entryTable);
        
        // Add the send button in a horizontal panel and put it all together
        HorizontalPanel menuPanel = new HorizontalPanel();
        sendButton = new Button("Go");
        menuPanel.add(sendButton);
        dataFilterPanel.add(menuPanel);
        dataFilterDecorator.add(dataFilterPanel);
    }
    
    private void initEntryTable() {
        entryTable.setWidget(0, 0, new Label("End Date"));
        entryTable.setWidget(1, 0, endDate);
        entryTable.setWidget(2, 0, new Label("Num Days"));
        entryTable.setWidget(3, 0, numDays);
    }
    
    */

    // Functionality from the Display interface needed by the Presenter.
    @Override
    public HasValue<Date> getEndDate() {
        return endDate;
    }

    @Override
    public String getNumDays() {
        return numDays.getValue(numDays.getSelectedIndex());
    }
    
    @Override
    public HasClickHandlers getSendButton() {
        return sendButton;
    }

    // Allows the presenter to show or hide the user list, based on whatever
    // logic is implemented within the presenter
    @Override
    public void hideUserList(boolean toHide) {
        if (toHide) {
            // If the user list is already hidden do nothing, else hide it
            if (!isUserListHidden) {
                doHideUserList();
            }
        }
        else {
            // If the user list is already shown do nothing, else show it
            if (isUserListHidden) {
                doShowUserList();
            }
        }
        
    }
    
    // Update the user list with a new list of users to display
    @Override
    public void setData(List<String> data) {
        // Run through the new data, add into the user list
        userList.clear();
        
        for (int i = 0; i < data.size(); ++i) {
          userList.addItem(data.get(i));
        }  
    }
    
    @Override
    public String getSelectedUser() {
        // -1 if no user is selected
        return userList.getValue(userList.getSelectedIndex());
    }
    
    
    @Override
    public void setSelectedUser(String user) {
        // Set the drop down to the passed user name, loop to find the user index
        int foundUserIndex = -1;
        for (int i = 0; i < userList.getItemCount(); ++i) {
            String userAtIndex = userList.getValue(i);
            if (userAtIndex.equals(user)) {
                foundUserIndex = i;
                break;
            }
        }
        
        // Set the selected user, set to -1 if user not found
        userList.setSelectedIndex(foundUserIndex);
    }
    
    @Override
    public Widget asWidget() {
        return this;
    }
    
    // Functionality to show or hide the user selection list
    private void doHideUserList() {
        // Since the user list has a row in the flex table, simply calling setVisible(false)
        // is not enough.  Remove the list from the flex table entirely.
        // We know it's in row 4/5, so hard code that in for now
        entryTable.removeRow(5);
        entryTable.removeRow(4);
       
        isUserListHidden = true;
    }
    
    private void doShowUserList() {
        entryTable.setWidget(4, 0, new Label("User"));
        entryTable.setWidget(5, 0, userList);
        
        isUserListHidden = false;
    }
    
}
