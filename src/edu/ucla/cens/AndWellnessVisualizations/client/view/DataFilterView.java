package edu.ucla.cens.AndWellnessVisualizations.client.view;

import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public interface DataFilterView<T> {

  public interface Presenter<T> {
    void onGoButtonClicked();
    void onItemClicked(T clickedItem);
    void onItemSelected(T selectedItem);
  }
  
  void setPresenter(Presenter<T> presenter);
  void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions);
  void setRowData(List<T> rowData);
  Widget asWidget();
}

