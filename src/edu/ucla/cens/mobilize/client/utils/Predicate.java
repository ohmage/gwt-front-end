package edu.ucla.cens.mobilize.client.utils;

/**
 * A simple interface used to filter items from a Collection.  Define a Predicate for a
 * certain data type, then use that Predicate to filter in CollectionUtils.
 * 
 * @author jhicks
 *
 * @param <T> The data type of the Collection to filter.
 */
public interface Predicate<T> { 
    boolean apply(T type); 
}
