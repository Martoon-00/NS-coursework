package ru.ifmo.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Wrapper for Function<K, V> which stores result for requested arguments
 * and returns it at repeated calls.
 */
public class CachingWrapper<K, V> implements Function<K, V> {
    private final Function<K, V> f;

    private final Map<K, V> cache = new HashMap<>();

    /**
     * Creates a function with the save behavior as parameter function, but
     * this function would be called only once for each arguments anyway.
     *
     * @param f wrapped function
     */
    public CachingWrapper(Function<K, V> f) {
        this.f = f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V apply(K key) {
        return cache.computeIfAbsent(key, f);
    }
}
