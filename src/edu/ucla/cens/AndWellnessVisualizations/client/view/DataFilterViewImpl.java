package edu.ucla.cens.AndWellnessVisualizations.client.view;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

import edu.ucla.cens.AndWellnessVisualizations.client.common.DropDownDefinition;

/**
 * Widget that displays a data selection filter.
 * 
 * @author jhicks
 *
 */
public class DataFilterViewImpl<T> extends Composite implements DataFilterView<T> {
    @UiTemplate("DataFilterView.ui.xml")
    interface DataFilterViewUiBinder extends UiBinder<Widget, DataFilterViewImpl<?>> {}
    private static DataFilterViewUiBinder uiBinder =
      GWT.create(DataFilterViewUiBinder.class);

    // The fields defined in the ui xml
    @UiField VerticalPanel dataEntryPanel;
    @UiField DatePicker endDatePicker;
    @UiField ListBox numDaysListBox;
    @UiField HorizontalPanel userListBoxHPanel;
    @UiField ListBox userListBox;
    @UiField Button goButton;

    // Call the presenter in response to events (user clicks)
    private Presenter<T> presenter;
    // Defines the structure of the columns in the entryTable
    private List<DropDownDefinition<T>> dropDownDefinitions;
    // Defines the contents of the entryTable
    private List<T> rowData;
    
    // Very simple constructor now that the UI is defined in XML
    public DataFilterViewImpl() {
      initWidget(uiBinder.createAndBindUi(this));
      
      // Init the default datepicker date to today
      endDatePicker.setValue(new Date());
    }
    
    public void setPresenter(Presenter<T> presenter) {
        this.presenter = presenter;
    }
    
    public void setDropDownDefinitions(List<DropDownDefinition<T>> dropDownDefinitions) {
        this.dropDownDefinitions = dropDownDefinitions;    
    }
    
    // Update the data displayed by the entryTable
    public void setRowData(List<T> rowData) {
        this.rowData = rowData;
        // Clear the current data
        userListBox.clear();
        
        // Possible future TODO, make this completely HTML, no ListBox widget
        // for performance reasons, for now leave (not many users anyway)
        for (int i = 0; i < rowData.size(); ++i) {
            T t = rowData.get(i);
            
            // Doesn't really make sense for dropDownDefinition to have a size
            // greater than 1, but do this for generality
            for (int j = 0; j < dropDownDefinitions.size(); ++j) {
                StringBuilder sb = new StringBuilder();
                dropDownDefinitions.get(j).render(t, sb);
                
                // Add the string to the listbox
                userListBox.addItem(sb.toString());
            }
        }
    }
    
    // When the "Go" button is clicked, send notification to the presenter
    @UiHandler("goButton")
    void onGoButtonClicked(ClickEvent event) {
      if (presenter != null) {
        presenter.onGoButtonClicked();
      }
    }
    
    // Whenever a new date is selected, notify the Presenter
    @UiHandler("endDatePicker")
    void onEndDatePickerValueChanged(ValueChangeEvent<Date> event) {
        if (presenter != null) {
            presenter.onEndDateSelected(event.getValue());
        }
    }
    
    // Whenever a new number of days is selected, notify the presenter
    @UiHandler("numDaysListBox")
    void onNumDaysListBoxChanged(ChangeEvent event) {
        int selectedIndex = numDaysListBox.getSelectedIndex();
        
        // If the selected index is -1, nothing is selected, do not notify the presenter
        if (presenter != null || selectedIndex != -1) {
            String selectedValue = numDaysListBox.getValue(selectedIndex);
            Integer numDays = Integer.valueOf(selectedValue);
            presenter.onNumDaysSelected(numDays);
        }
    }
    
    // Whenever a new user is selected, notify the Presenter
    @UiHandler("userListBox")
    void onUserListBoxChanged(ChangeEvent event) {
        int selectedIndex = userListBox.getSelectedIndex();
        
        // If the selected index is -1, nothing is selected, do not notify the presenter
        if (presenter != null || selectedIndex != -1) {
            // Grab the item from the rowData and return
            T selectedItem = rowData.get(selectedIndex);
            presenter.onUserSelected(selectedItem);
        }
    }
    
    // Show or hide the user list
    public void enableUserList(boolean enable) {
        if (enable) {
            userListBoxHPanel.setVisible(true);
        }
        else {
            userListBoxHPanel.setVisible(false);
        }        
    }
    
    public Widget asWidget() {
        return this;
    }
}
