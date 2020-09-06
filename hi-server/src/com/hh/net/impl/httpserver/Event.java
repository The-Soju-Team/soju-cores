/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
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

package com.hh.net.impl.httpserver;

class Event {

    ExchangeImpl exchange;

    protected Event(ExchangeImpl t) {
        this.exchange = t;
    }
}

class WriteFinishedEvent extends Event {
    WriteFinishedEvent(ExchangeImpl t) {
        super(t);
        assert !t.writefinished;
        t.writefinished = true;
    }
}
