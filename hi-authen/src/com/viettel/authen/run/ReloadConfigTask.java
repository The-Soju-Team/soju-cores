/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.authen.run;

import static com.viettel.authen.run.StartApp.config;

import com.hh.connector.process.TimerTask;
import com.hh.util.ConfigUtils;

import java.util.List;

/**
 * @author dvgp_admin
 */
public class ReloadConfigTask extends TimerTask {

    public ReloadConfigTask(List lstParam) {
        super(lstParam);
    }

    @Override
    public void process(Object message) {
        config = new ConfigUtils((String) lstParam.get(0));
    }
}
