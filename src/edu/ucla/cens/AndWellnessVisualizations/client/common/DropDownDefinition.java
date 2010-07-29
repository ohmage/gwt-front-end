package edu.ucla.cens.AndWellnessVisualizations.client.common;

public abstract class DropDownDefinition<T> {
  public abstract void render(T t, StringBuilder sb);
  
  public boolean isSelectable() {
    return false;
  }
}
