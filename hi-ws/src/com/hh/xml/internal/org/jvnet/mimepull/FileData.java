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

package com.hh.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

/**
 * Keeps the Part's partial content data in a file.
 *
 * @author Kohsuke Kawaguchi
 * @author Jitendra Kotamraju
 */
final class FileData implements Data {
    private final DataFile file;
    private final long pointer;         // read position
    private final int length;

    FileData(DataFile file, ByteBuffer buf) {
        this(file, file.writeTo(buf.array(), 0, buf.limit()), buf.limit());
    }

    FileData(DataFile file, long pointer, int length) {
        this.file = file;
        this.pointer = pointer;
        this.length = length;
    }

    public byte[] read() {
        byte[] buf = new byte[length];
        file.read(pointer, buf, 0, length);
        return buf;
    }

    /*
     * This shouldn't be called
     */
    public long writeTo(DataFile file) {
        throw new IllegalStateException();
    }

    public int size() {
        return length;
    }

    /*
     * Always create FileData
     */
    public Data createNext(DataHead dataHead, ByteBuffer buf) {
        return new FileData(file, buf);
    }
}
