package edu.ucla.cens.AndWellnessVisualizations.client.common;

public class SetModel<T> {
    T setItem = null;
   
    public T getSetItem()  {
        return setItem;
    }
    
    public void updateSetItem(T item) {
        setItem = item;
    }
    
    public boolean isSet() {
        return setItem != null; 
    }
    
    public void clear() {
        setItem = null;
    }
}
