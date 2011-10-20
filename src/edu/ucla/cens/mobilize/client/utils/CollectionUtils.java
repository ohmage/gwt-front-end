package edu.ucla.cens.mobilize.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A number of utilities that work with Java Collections.
 * 
 * @author jhicks
 *
 */
public class CollectionUtils {
    /**
     * Filters out items from a Collection based on the passed in predicate.
     * 
     * @param <T> The type of item to filter.
     * @param target The collection from which to filter.
     * @param predicate The filtering predicate.
     * @return A collection of filtered items.
     */
    public static <T> Collection<T> filter(Collection<T> target, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element: target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }
    
    /**
     * Joins a Collection of Strings into a single String with the passed delimiter.
     * Throws runtime exception if collection contains a null string.
     * @param s The Collection to join.
     * @param delimiter The delimiter to insert between joins.
     * @return The single joined String. Returns the empty string if list was empty or null.
     */
    public static String join(Collection<String> s, String delimiter) {
        if (s == null || s.isEmpty()) return "";
        Iterator<String> iter = s.iterator();
        StringBuilder builder = new StringBuilder(iter.next());
        while( iter.hasNext() )
        {
            String item = iter.next();
            if (item == null) throw new RuntimeException("CollectionUtils cannot join collection with null entry.");
            builder.append(delimiter).append(item);
        }
        return builder.toString();
    }
    
    public static <T> Collection<T> setDiff(Collection<T> itemsToStartWith, Collection<T> itemsToSubtract) {
      Collection<T> retval = new ArrayList<T>(); 
      if (itemsToStartWith == null) return new ArrayList<T>();
      retval.addAll(itemsToStartWith); // make copy so you don't mutate original
      if (itemsToSubtract != null) retval.removeAll(itemsToSubtract);
      return retval;
    }
}
