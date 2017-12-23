package com.iskyshop.smilife.broadcast;

import java.util.Map;

public interface IBroadcastService {

    /**
     * 获取广播消息轮播
     * @return
     */
    public Map<String, Object> getAppBroadcasts();
}
