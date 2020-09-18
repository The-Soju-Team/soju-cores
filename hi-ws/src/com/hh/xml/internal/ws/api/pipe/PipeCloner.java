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

package com.hh.xml.internal.ws.api.pipe;

import com.hh.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;

/**
 * Clones the whole pipeline.
 *
 * <p>
 * Since {@link Pipe}s may form an arbitrary directed graph, someone needs
 * to keep track of isomorphism for a clone to happen correctly. This class
 * serves that role.
 *
 * @deprecated
 *      Use {@link TubeCloner}.
 * @author Kohsuke Kawaguchi
 */
public final class PipeCloner extends TubeCloner {
    /**
     * {@link Pipe} version of {@link #clone(Tube)}
     */
    public static Pipe clone(Pipe p) {
        return new PipeCloner().copy(p);
    }

    // no need to be constructed publicly. always use the static clone method.
    /*package*/ PipeCloner() {}

    /**
     * {@link Pipe} version of {@link #copy(Tube)}
     */
    public <T extends Pipe> T copy(T p) {
        Pipe r = (Pipe)master2copy.get(p);
        if(r==null) {
            r = p.copy(this);
            // the pipe must puts its copy to the map by itself
            assert master2copy.get(p)==r : "the pipe must call the add(...) method to register itself before start copying other pipes, but "+p+" hasn't done so";
        }
        return (T)r;
    }


    /**
     * The {@link Pipe} version of {@link #add(Tube, Tube)}.
     */
    public void add(Pipe original, Pipe copy) {
        assert !master2copy.containsKey(original);
        assert original!=null && copy!=null;
        master2copy.put(original,copy);
    }

    /**
     * Disambiguation version.
     */
    public void add(AbstractTubeImpl original, AbstractTubeImpl copy) {
        add((Tube)original,copy);
    }
}
