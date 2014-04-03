/*!! shared.model */
package com.todomvc.shared.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/*!
  A sorted collection which has id and contains objects with id's.

  No two elements in the collection have the same id.
 */
public abstract class IdentifiableCollection<E extends HasID>
        extends AbstractCollection<E> implements HasID, Serializable {

    /*! Use a map for fast retrieval. */
    private Map<String, E> children;
    /*! Use a list for addition-order traversal. */
    private List<E> sortedChildren;
    private String id;

    protected IdentifiableCollection() {
    }

    public IdentifiableCollection(String id) {
        super();
        this.id = checkNotNull(id);
        children = Maps.newHashMap();
        sortedChildren = Lists.newArrayList();
    }

    /*! Returns a child element given its id. */
    protected E getChild(String id) {
        checkNotNull(id);
        E child = children.get(id);
        if (child == null) {
            throw new IllegalArgumentException("Element with id '" + id + "' not found");
        }
        return child;
    }

    /*! Returns the id of this collection. */
    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean add(E e) {
        checkNotNull(e);
        checkNotNull(e.getId());
        if (children.containsKey(e.getId())) {
            throw new IllegalArgumentException("Duplicate rejected. "
                    + "Collection already contains an element with id "
                    + e.getId());
        }
        children.put(e.getId(), e);
        sortedChildren.add(e);
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return new AdditionOrderIterator();
    }

    private class AdditionOrderIterator implements Iterator<E> {

        private final Iterator<E> sortedChildrenIterator;
        private E lastVisited;

        public AdditionOrderIterator() {
            sortedChildrenIterator = sortedChildren.iterator();
        }

        @Override
        public boolean hasNext() {
            return sortedChildrenIterator.hasNext();
        }

        @Override
        public E next() {
            lastVisited = sortedChildrenIterator.next();
            return lastVisited;
        }

        @Override
        public void remove() {
            checkNotNull(lastVisited);
            children.remove(lastVisited.getId());
            sortedChildrenIterator.remove();
        }
        
    };

    @Override
    public int size() {
        checkState(children.size() == sortedChildren.size());
        return children.size();
    }

    /*- Object's methods overrides. */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IdentifiableCollection)) {
            return false;
        }
        IdentifiableCollection<?> that = (IdentifiableCollection<?>) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("size", size())
                .add("elements", sortedChildren)
                .toString();
    }

}
