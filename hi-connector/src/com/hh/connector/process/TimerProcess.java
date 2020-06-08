/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.connector.process;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.hh.connector.netty.server.NettyServer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;

/**
 *
 * @author HienDM
 */
public class TimerProcess {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TimerProcess.class.getSimpleName());
    private Class timerTask;
    private Long period;
    private Long start;
    private List lstParam = new ArrayList();
    
    public TimerProcess(Class timerTask, Long period) {
        this.timerTask = timerTask;
        this.period = period;
    }
    
    public TimerProcess(Class timerTask, Long delay, Long period) {
        this.timerTask = timerTask;
        this.period = period;
        this.start = (new Date()).getTime() + delay;
    }    
    
    public TimerProcess(Class timerTask, List lstParam, Long period) {
        this.timerTask = timerTask;
        this.period = period;
        this.lstParam = lstParam;
        this.start = (new Date()).getTime();
    }    
    
    public TimerProcess(Class timerTask, List lstParam, Long delay, Long period) {
        this.timerTask = timerTask;
        this.period = period;
        this.lstParam = lstParam;
        this.start = (new Date()).getTime() + delay;
    }        
    
    public void start() {
        try {
            if(NettyServer.system != null) {
                Thread.sleep(start - (new Date()).getTime());
                log.info("START TIMER TASK: " + timerTask.getName());
                ActorRef tickActor = NettyServer.system.actorOf(Props.create(timerTask, lstParam));
                NettyServer.system.scheduler().schedule(Duration.Zero(),
                        Duration.create(period, TimeUnit.MILLISECONDS),
                        tickActor,
                        "execute",
                        NettyServer.system.dispatcher(),
                        null);
            } else {
                log.info("Can't start because NettyServer.system is null!");
            }
        } catch(Exception ex) {
            log.error("Error when start TimerProcess: ", ex);
        }                
    }
}
