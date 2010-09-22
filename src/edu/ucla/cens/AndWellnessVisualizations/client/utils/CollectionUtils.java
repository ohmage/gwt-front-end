package edu.ucla.cens.AndWellnessVisualizations.client.utils;

import java.util.ArrayList;
import java.util.Collection;

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
}