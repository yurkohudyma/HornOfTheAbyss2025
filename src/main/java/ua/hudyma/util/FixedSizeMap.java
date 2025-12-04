package ua.hudyma.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FixedSizeMap<K, V> implements Map<K, V> {

    private final Map<K, V> delegate;
    private final int maxSize;

    public FixedSizeMap(int initialCapacity, int maxSize) {
        this.delegate = new HashMap<>(initialCapacity);
        this.maxSize = maxSize;
    }

    public FixedSizeMap(Map<K, V> delegate, int maxSize) {
        this.delegate = delegate;
        this.maxSize = maxSize;
    }

    @Override
    public V put(K key, V value) {
        if (!delegate.containsKey(key) && delegate.size() >= maxSize) {
            throw new IllegalStateException("Map size limit exceeded: " + maxSize);
        }
        return delegate.put(key, value);
    }

    @Override public int size() { return delegate.size(); }
    @Override public boolean isEmpty() { return delegate.isEmpty(); }
    @Override public boolean containsKey(Object key) { return delegate.containsKey(key); }
    @Override public boolean containsValue(Object value) { return delegate.containsValue(value); }
    @Override public V get(Object key) { return delegate.get(key); }
    @Override public V remove(Object key) { return delegate.remove(key); }
    @Override public void putAll(Map<? extends K, ? extends V> m) {
        if (delegate.size() + m.size() > maxSize)
            throw new IllegalStateException("Map size limit exceeded: " + maxSize);
        delegate.putAll(m);
    }
    @Override public void clear() { delegate.clear(); }
    @Override public Set<K> keySet() { return delegate.keySet(); }
    @Override public Collection<V> values() { return delegate.values(); }
    @Override public Set<Entry<K, V>> entrySet() { return delegate.entrySet(); }
}

