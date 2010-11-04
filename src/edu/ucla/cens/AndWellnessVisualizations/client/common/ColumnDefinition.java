package edu.ucla.cens.AndWellnessVisualizations.client.common;

import com.google.gwt.user.client.ui.Widget;

public abstract class ColumnDefinition<T> {
  public abstract Widget render(T t);
  
  public boolean isClickable() {
    return false;
  }
  
  public boolean isSelectable() {
    return false;
  }
}
