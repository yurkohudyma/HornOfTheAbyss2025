package ua.hudyma.util;

import java.util.*;

public class FixedSizeList<T> implements List<T> {
    private final List<T> delegate;
    private final int maxSize;

    public FixedSizeList() {
        this.delegate = new ArrayList<>();
        this.maxSize = 5;
    }
    public FixedSizeList(int initialCapacity, int maxSize) {
        this.delegate = new ArrayList<>(initialCapacity);
        this.maxSize = maxSize;
    }
    public FixedSizeList(List<T> delegate, int maxSize) {
        this.delegate = delegate;
        this.maxSize = maxSize;
    }

    public FixedSizeList(int maxSize) {
        this.maxSize = maxSize;
        this.delegate = new ArrayList<>();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(T t) {
        if (delegate.size() >= maxSize){
            throw new IllegalStateException
                    ("List size limit exceeded: " + maxSize);
        }
        return delegate.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (delegate.size() + c.size() > maxSize) {
            throw new IllegalStateException("List size limit exceeded: " + maxSize);
        }

        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (delegate.size() + c.size() > maxSize) {
            throw new IllegalStateException("List size limit exceeded: " + maxSize);
        }

        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public T get(int index) {
        return delegate.get(index);
    }

    @Override
    public T set(int index, T element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        if (delegate.size() >= maxSize) {
            throw new IllegalStateException("List size limit exceeded: " + maxSize);
        }
        delegate.add(index, element);
    }

    @Override
    public T remove(int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }
}
