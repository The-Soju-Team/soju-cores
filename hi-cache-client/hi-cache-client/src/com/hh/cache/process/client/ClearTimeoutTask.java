package com.hh.cache.process.client;

import com.google.common.cache.Cache;
import com.hh.connector.process.TimerTask;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ClearTimeoutTask
        extends TimerTask {

    public ClearTimeoutTask(List lstParam) {
        super(lstParam);
    }

    public void process(Object message) {
        Cache<Object, Object> cache = HiCacheSession.cacheStore.get(lstParam.get(1));
        if(cache != null) {
            Map timeoutRequest = cache.asMap();
            for (Object entry : timeoutRequest.entrySet()) {
                Map.Entry<Object, Object> item = (Map.Entry) entry;
                Object lock = item.getValue();
                if ((lock != null) && ((lock instanceof Long))
                        && (new Date().getTime() - ((Long) lock).longValue() > ((Long) this.lstParam.get(0)).longValue())) {
                    synchronized (lock) {
                        lock.notify();
                        cache.invalidate(item.getKey());
                    }
                }
            }
        }
    }
}
