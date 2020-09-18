/*
 * Copyright (c) 1997, 2010, Oracle and/or its affiliates. All rights reserved.
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

/*
 * Created on Feb 28, 2003
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.hh.xml.internal.messaging.saaj.util;

import java.io.*;

/**
 * @author pgoodwin
 */
public class TeeInputStream extends InputStream {
    protected InputStream source;
    protected OutputStream copySink;

    public TeeInputStream(InputStream source, OutputStream sink) {
        super();
        this.copySink = sink;
        this.source = source;
    }

    public int read() throws IOException {
        int result = source.read();
        copySink.write(result);
        return result;
    }

    public int available() throws IOException {
        return source.available();
    }

    public void close() throws IOException {
        source.close();
    }

    public synchronized void mark(int readlimit) {
        source.mark(readlimit);
    }

    public boolean markSupported() {
        return source.markSupported();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int result = source.read(b, off, len);
        copySink.write(b, off, len);
        return result;
    }

    public int read(byte[] b) throws IOException {
        int result = source.read(b);
        copySink.write(b);
        return result;
    }

    public synchronized void reset() throws IOException {
        source.reset();
    }

    public long skip(long n) throws IOException {
        return source.skip(n);
    }

}
