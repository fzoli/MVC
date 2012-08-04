package org.dyndns.fzoli.util;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;

/**
 *
 * @author zoli
 */
public class ArrayList<E> extends java.util.ArrayList<E> {

    private static final int millis = 100, maxErr = 10;
    
    public ArrayList() {
    }

    public ArrayList(Collection<? extends E> c) {
        super(c);
    }

    public ArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr(super.iterator());
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return new ListItr(super.listIterator());
    }
    
    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListItr(super.listIterator(index));
    }
    
    private void sleep() {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException ex) {
            ;
        }
    }
    
    private class Itr<T extends Iterator<E>> implements Iterator<E> {
        
        T defItr;
        int next = 0, remove = 0;
        int expectedModCount = modCount;
        
        public Itr(T defItr) {
            this.defItr = defItr;
        }

        @Override
        public boolean hasNext() {
            return defItr.hasNext();
        }

        @Override
        public E next() {
            try {
                return defItr.next();
            }
            catch(ConcurrentModificationException ex) {
                if (isComodification()) {
                    next++;
                    if (next >= maxErr) throw ex;
                    sleep();
                    return next();
                }
                throw ex;
            }
        }

        @Override
        public void remove() {
            try {
                defItr.remove();
                expectedModCount = modCount;
            }
            catch (ConcurrentModificationException ex) {
                if (isComodification()) {
                    remove++;
                    if (remove >= maxErr) throw ex;
                    sleep();
                    remove();
                    return;
                }
                throw ex;
            }
        }
        
        final boolean isComodification() {
            return modCount != expectedModCount;
        }
        
    }
    
    private class ListItr extends Itr<ListIterator<E>> implements ListIterator<E> {

        int previous = 0, set = 0, add = 0;
        
        public ListItr(ListIterator<E> defItr) {
            super(defItr);
        }

        @Override
        public boolean hasPrevious() {
            return defItr.hasPrevious();
        }

        @Override
        public int nextIndex() {
            return defItr.nextIndex();
        }

        @Override
        public int previousIndex() {
            return defItr.previousIndex();
        }
        
        @Override
        public E previous() {
            try {
                return defItr.previous();
            }
            catch (ConcurrentModificationException ex) {
                if (isComodification()) {
                    previous++;
                    if (previous >= maxErr) throw ex;
                    sleep();
                    return previous();
                }
                throw ex;
            }
        }
        
        @Override
        public void set(E e) {
            try {
                defItr.set(e);
            }
            catch (ConcurrentModificationException ex) {
                if (isComodification()) {
                    set++;
                    if (set >= maxErr) throw ex;
                    sleep();
                    set(e);
                    return;
                }
                throw ex;
            }
        }

        @Override
        public void add(E e) {
            try {
                defItr.add(e);
                expectedModCount = modCount;
            }
            catch (ConcurrentModificationException ex) {
                if (isComodification()) {
                    add++;
                    if (add >= maxErr) throw ex;
                    sleep();
                    add(e);
                    return;
                }
                throw ex;
            }
        }
        
    }
    
}