package edu.ucla.cens.AndWellnessVisualizations.client.view;

import com.google.gwt.user.client.ui.Widget;

import edu.ucla.cens.AndWellnessVisualizations.client.common.DropDownDefinition;

import java.util.Date;
import java.util.List;

public interface DataFilterView<T> {
    // Any Presenter that uses this View must implement these functions to
    // handle events from the View
    public interface Presenter<T> {
        void onGoButtonClicked();
        void onEndDateSelected(Date selectedEndDate);
        void onNumDaysSelected(Integer numDays);
        void onUserSelected(T selectedUser);
    }
  
    // Sets the presenter so the view can call the presenter in response to events
    void setPresenter(Presenter<T> presenter);
    void setDropDownDefinitions(List<DropDownDefinition<T>> dropDownDefinitions);
    // Update the list of displayed users
    void setRowData(List<T> rowData);
    // Hide/show the user list
    void enableUserList(boolean enable);
    Widget asWidget();
}

