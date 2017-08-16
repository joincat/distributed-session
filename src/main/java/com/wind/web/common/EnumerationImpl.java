package com.wind.web.common;

import java.util.*;

public class EnumerationImpl implements Enumeration {
    private Iterator iterator;

    @SuppressWarnings("unchecked")
    public EnumerationImpl(Collection collection) {
        super();
        this.iterator = collection.iterator();
        List list = new ArrayList();
        while (this.iterator.hasNext()) {
            list.add(iterator.next());
        }
        this.iterator = list.iterator();
    }

    @Override
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    @Override
    public Object nextElement() {
        return this.iterator.next();
    }
}
