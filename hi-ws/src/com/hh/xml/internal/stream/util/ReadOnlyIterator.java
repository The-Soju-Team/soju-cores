/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.hh.xml.internal.stream.util;
/**
 *
 * @author K.Venugopal ,Neeraj Bajaj Sun Microsystems.
 */

import java.util.Iterator;

public class ReadOnlyIterator implements Iterator {

    Iterator iterator = null;

    public ReadOnlyIterator(){
    }

    public ReadOnlyIterator(Iterator itr){
        iterator = itr;
    }

    /**
     * @return
     */
    public boolean hasNext() {
        if(iterator  != null)
            return iterator.hasNext();
        return false;
    }

    /**
     * @return
     */
    public Object next() {
        if(iterator  != null)
            return iterator.next();
        return null;
    }

    public void remove() {
        throw new  UnsupportedOperationException("Remove operation is not supported");
    }

}
