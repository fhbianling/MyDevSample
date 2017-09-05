package com.bian.base.util.utilevent;

import org.greenrobot.eventbus.EventBus;

import com.bian.base.util.utilbase.L;

/**
 * author 边凌
 * date 2017/6/29 19:40
 * desc ${事件总线工具}
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class EventUtil {


    private final static String LOG_TAG = "EventUtil";
    private final EventBus bus;
    private EventUtil() {
        bus=EventBus.getDefault();
    }
    public static EventUtil get() {
        return Holder.BUS;
    }

    public static void post(Object event) {
        L.d(event);
        get().bus.post(event);
    }

    public void postObj(Object obj) {
        bus.post(obj);
    }

    public void register(Object target) {
        bus.register(target);
    }

    public void unregister(Object target) {
        bus.unregister(target);
    }

    private static class Holder {
        private static final EventUtil BUS = new EventUtil();
    }
}
