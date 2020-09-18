/*
 * Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved.
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
 *
 * THIS FILE WAS MODIFIED BY SUN MICROSYSTEMS, INC.
 */

package com.hh.xml.internal.fastinfoset.stax.events;

import java.util.Iterator;
import com.hh.xml.internal.fastinfoset.CommonResourceBundle;

public class ReadIterator implements Iterator {

    Iterator iterator = EmptyIterator.getInstance();

    public ReadIterator(){
    }

    public ReadIterator(Iterator iterator){
        if (iterator != null) {
            this.iterator = iterator;
        }
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return iterator.next();
    }

    public void remove() {
        throw new  UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.readonlyList"));
    }


}
