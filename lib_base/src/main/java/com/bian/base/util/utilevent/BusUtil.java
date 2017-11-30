package com.bian.base.util.utilevent;

import com.bian.base.util.utilbase.L;

import org.greenrobot.eventbus.EventBus;


/**
 * author 边凌
 * date 2017/6/29 19:40
 * desc ${事件总线工具}
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class BusUtil {


    private final static String LOG_TAG = "BusUtil";
    private final EventBus bus;
    private BusUtil() {
        bus=EventBus.getDefault();
    }
    public static BusUtil get() {
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
        private static final BusUtil BUS = new BusUtil();
    }
}
