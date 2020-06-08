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

import com.hh.xml.internal.ws.api.SOAPVersion;
import com.hh.xml.internal.ws.api.WSBinding;
import com.hh.xml.internal.ws.api.message.Packet;
import com.hh.xml.internal.ws.api.message.Attachment;
import com.hh.xml.internal.ws.api.pipe.Codec;
import com.hh.xml.internal.ws.api.pipe.ContentType;
import com.hh.xml.internal.ws.message.stream.StreamAttachment;
import com.hh.xml.internal.ws.message.MimeAttachmentSet;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

/**
 * {@link Codec} that uses MIME/multipart as the base format.
 *
 * @author Jitendra Kotamraju
 */
public final class SwACodec extends MimeCodec {

    public SwACodec(SOAPVersion version, WSBinding binding, Codec rootCodec) {
        super(version, binding);
        this.rootCodec = rootCodec;
    }

    private SwACodec(SwACodec that) {
        super(that);
        this.rootCodec = that.rootCodec.copy();
    }

    @Override
    protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
        // TODO: handle attachments correctly
        Attachment root = mpp.getRootPart();
        if (rootCodec instanceof RootOnlyCodec) {
            ((RootOnlyCodec)rootCodec).decode(root.asInputStream(),root.getContentType(),packet, new MimeAttachmentSet(mpp));
        } else {
            rootCodec.decode(root.asInputStream(),root.getContentType(),packet);
            Map<String, Attachment> atts = mpp.getAttachmentParts();
            for(Map.Entry<String, Attachment> att : atts.entrySet()) {
                packet.getMessage().getAttachments().add(att.getValue());
            }
        }
    }

    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        //TODO: not yet implemented
        throw new UnsupportedOperationException();
    }

    public SwACodec copy() {
        return new SwACodec(this);
    }
}
