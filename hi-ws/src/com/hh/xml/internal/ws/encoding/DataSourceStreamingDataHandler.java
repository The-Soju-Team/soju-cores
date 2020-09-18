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

package com.hh.xml.internal.ws.encoding;

import com.hh.xml.internal.org.jvnet.mimepull.MIMEPart;

import javax.activation.DataSource;
import java.io.*;

import com.hh.xml.internal.ws.developer.StreamingDataHandler;

/**
 * @author Jitendra Kotamraju
 */
public class DataSourceStreamingDataHandler extends StreamingDataHandler {

    public DataSourceStreamingDataHandler(DataSource ds) {
        super(ds);
    }

    public InputStream readOnce() throws IOException {
        return getInputStream();
    }

    public void moveTo(File file) throws IOException {
        InputStream in = getInputStream();
        OutputStream os = new FileOutputStream(file);
        byte[] temp = new byte[8192];
        int len;
        while((len=in.read(temp)) != -1) {
            os.write(temp, 0, len);
        }
        in.close();
        os.close();
    }

    public void close() throws IOException {
        // nothing to do here
    }

}
