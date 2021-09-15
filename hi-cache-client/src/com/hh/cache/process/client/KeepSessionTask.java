package com.hh.cache.process.client;

import com.hh.connector.process.TimerTask;
import java.util.List;

public class KeepSessionTask
        extends TimerTask {

    public KeepSessionTask(List lstParam) {
        super(lstParam);
    }

    public void process(Object message) {
        if (this.lstParam != null) {
            HiCacheSession cacheSession = (HiCacheSession) this.lstParam.get(0);
            Object spaceSize = cacheSession.getSpaceSize();
            if (spaceSize == null) {
                cacheSession.reConnect();
            }
        }
    }
}
