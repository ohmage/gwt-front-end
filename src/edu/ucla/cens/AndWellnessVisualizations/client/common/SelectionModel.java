package edu.ucla.cens.AndWellnessVisualizations.client.common;

import java.util.ArrayList;
import java.util.List;

public class SelectionModel<T> {
  List<T> selectedItems = new ArrayList<T>();
  
  public List<T> getSelectedItems() {
    return selectedItems;
  }
  
  public void addSelection(T item) {
    selectedItems.add(item);
  }
  
  public void removeSelection(T item) {
    selectedItems.remove(item);
  }
  
  public boolean isSelected(T item) {
    return selectedItems.contains(item);
  }
}