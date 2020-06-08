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

package com.hh.xml.internal.stream.events;

import javax.xml.stream.events.DTD;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author  Neeraj Bajaj, Sun Microsystesm.
 *
 */
public class DTDEvent extends DummyEvent implements DTD{

    private String fDoctypeDeclaration;
    private java.util.List fNotations;
    private java.util.List fEntities;

    /** Creates a new instance of DTDEvent */
    public DTDEvent() {
        init();
    }

    public DTDEvent(String doctypeDeclaration){
        init();
        fDoctypeDeclaration = doctypeDeclaration;
    }

    public void setDocumentTypeDeclaration(String doctypeDeclaration){
        fDoctypeDeclaration = doctypeDeclaration;
    }

    public String getDocumentTypeDeclaration() {
        return fDoctypeDeclaration;
    }

    //xxx: we can change the signature if the implementation doesn't store the entities in List Datatype.
    //and then convert that DT to list format here. That way callee dont need to bother about conversion

    public void setEntities(java.util.List entites){
        fEntities = entites;
    }

    public java.util.List getEntities() {
        return fEntities;
    }

    //xxx: we can change the signature if the implementation doesn't store the entities in List Datatype.
    //and then convert that DT to list format here. That way callee dont need to bother about conversion

    public void setNotations(java.util.List notations){
        fNotations = notations;
    }

    public java.util.List getNotations() {
        return fNotations;
    }

    /**
     *Returns an implementation defined representation of the DTD.
     * This method may return null if no representation is available.
     *
     */
    public Object getProcessedDTD() {
        return null;
    }

    protected void init(){
        setEventType(XMLEvent.DTD);
    }

    public String toString(){
        return fDoctypeDeclaration ;
    }

    protected void writeAsEncodedUnicodeEx(java.io.Writer writer)
    throws java.io.IOException
    {
        writer.write(fDoctypeDeclaration);
    }
}
