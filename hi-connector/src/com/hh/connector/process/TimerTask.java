/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.process;

import akka.actor.AbstractActor;
import akka.japi.pf.FI;
import akka.japi.pf.ReceiveBuilder;
import org.apache.log4j.Logger;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HienDM
 */
public class TimerTask extends AbstractActor {
    private static Logger log = Logger.getLogger(TimerTask.class.getSimpleName());
    public List lstParam = new ArrayList();

    public TimerTask(List lstParam) {
        this.lstParam = lstParam;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.matchEquals("execute",
                new FI.UnitApply() {
                    @Override
                    public void apply(Object message) throws Exception {
                        try {
                            try {
                                process(message);
                            } catch (Exception ex) {
                                log.error("Error receive message", ex);
                            }
                        } catch (Throwable e) {
                            log.error("Co loi Fatal ", e);
                        }
                    }
                }).build();
    }

    public void process(Object message) throws Exception {
    }
}
