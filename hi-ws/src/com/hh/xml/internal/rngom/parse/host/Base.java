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
/*
 * Copyright (C) 2004-2011
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.hh.xml.internal.rngom.parse.host;

import com.hh.xml.internal.rngom.ast.builder.Annotations;
import com.hh.xml.internal.rngom.ast.om.Location;

/**
 *
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class Base {
    protected AnnotationsHost cast( Annotations ann ) {
        if(ann==null)
            return nullAnnotations;
        else
            return (AnnotationsHost)ann;
    }

    protected LocationHost cast( Location loc ) {
        if(loc==null)
            return nullLocation;
        else
            return (LocationHost)loc;
    }

    private static final AnnotationsHost nullAnnotations = new AnnotationsHost(null,null);
    private static final LocationHost nullLocation = new LocationHost(null,null);
}
