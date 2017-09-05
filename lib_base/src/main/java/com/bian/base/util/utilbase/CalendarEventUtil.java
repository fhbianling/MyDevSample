package com.bian.base.util.utilbase;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * author 边凌
 * date 2017/7/28 16:53
 * 类描述：系统日历 工具类，存入事件等
 * 该类使用需要权限：
 * <uses-permission android:name="android.permission.READ_CALENDAR" />
 * <uses-permission android:name="android.permission.WRITE_CALENDAR" />
 * 应该在app的manifest中申请
 */

public class CalendarEventUtil {
    public final static String[] PERMISSION = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
    private final static String LOG_TAG = "CalendarEventUtil";
    private final static String EVENT_SHARPF = "CalendarEvent";
    private final static CalendarAccount DEFAULT_ACCOUNT;
    private final static String CALENDAR_URL = "content://com.android.calendar/calendars";
    private final static String CALENDAR_EVENT_URL = "content://com.android.calendar/events";
    private final static String CALENDAR_REMINDER_URL = "content://com.android.calendar/reminders";
    private final static String CALENDARS_ACCOUNT_TYPE = "com.android.exchange";
    private final static String TIMEZONE_DEFAULT = "GMT+8";
    private final static
    @ColorInt
    int DEFAULT_EVENT_COLOR = Color.parseColor("#159fff");
    private static
    @ColorInt
    int sEventColor = DEFAULT_EVENT_COLOR;
    private static CalendarAccount sCreatedCalendarAccount;
    private static String sTimeZoneId = TIMEZONE_DEFAULT;

    static {
        DEFAULT_ACCOUNT = new CalendarAccount();
        DEFAULT_ACCOUNT.name = "test";
        DEFAULT_ACCOUNT.email = "test@gmail.com";
        DEFAULT_ACCOUNT.displayName = "测试账户";
        sCreatedCalendarAccount = DEFAULT_ACCOUNT;
    }

    private CalendarEventUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 由于存在账户不存在需要代码创建账户的可能性，这里可设置被创建账户的相关配置实体类
     *
     * @param sCreatedCalendarAccount {@link CalendarAccount}
     */
    public static void setCreatedCalendarAccount(CalendarAccount sCreatedCalendarAccount) {
        CalendarEventUtil.sCreatedCalendarAccount = sCreatedCalendarAccount;
    }

    /**
     * 设置时区，日历事件与时区相关，默认为东八区
     *
     * @param timeZoneId 例"GMT+8"
     */
    public static void setTimeZone(String timeZoneId) {
        CalendarEventUtil.sTimeZoneId = timeZoneId;
    }

    /**
     * 创建一个日历事件
     *
     * @param context       context
     * @param calendarEvent {@link CalendarEvent}
     * @return 是否创建成功
     */
    public static boolean addOrUpdateCalendarEvent(Context context, CalendarEvent calendarEvent) {
        int accountId = getCalendarAccountId(context);
        return accountId >= 0 && addOrUpdateCalendarEvent(context, accountId, calendarEvent);
    }

    public static void setEventColor(@ColorInt int eventColor) {
        sEventColor = eventColor;
    }

    public static void deleteCalendarEvent(Context context, String title) {
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDAR_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null) {
                Log.d(LOG_TAG, "deleteFailed");
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDAR_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) {
                            //事件删除失败
                            Log.d(LOG_TAG, "deleteFailed");
                            return;
                        }
                        Log.d(LOG_TAG, "deleteSuccess");
                        return;
                    }
                }
                Log.d(LOG_TAG, "deleteFailed");
            } else {
                Log.d(LOG_TAG, "deleteFailed");
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }
    /*---------------------------------------------------------------------*/
    /*下面是私有方法，不用看*/

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     *
     * @param context 上下文
     * @return 账户id
     */
    private static int getCalendarAccountId(Context context) {
        int account = queryCalendarAccount(context);
        if (account >= 0) {
            return account;
        } else {
            if (addCalendarAccount(context)) {
                return queryCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 通过账户id添加事件
     *
     * @param context       上下文
     * @param accountId     账户id
     * @param calendarEvent 日历事件的对象
     * @return 是否添加成功
     */
    private static boolean addOrUpdateCalendarEvent(Context context, int accountId, CalendarEvent calendarEvent) {
        assignIdOfCalendarEvent(context, calendarEvent);
        Uri newEvent = createOrUpdateCalendarEvent(context, accountId, calendarEvent);
        if (newEvent == null) {
            return false;
        }
        Uri uri = createOrUpdateReminderOfEvent(context, calendarEvent, newEvent);
        return uri != null;
    }

    /**
     * 为一个日历事件创建或更新提醒事件
     */
    private static Uri createOrUpdateReminderOfEvent(Context context, CalendarEvent calendarEvent, Uri newEvent) {
        long reminderId = queryReminderOfCalendarEvent(context, newEvent);
        //事件提醒
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 设置提醒时间，单位分钟
        values.put(CalendarContract.Reminders.MINUTES, calendarEvent.remindMinutes);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        if (reminderId != -1) {
            Uri uri = getUri(reminderId, CALENDAR_REMINDER_URL);
            context.getContentResolver().
                    update(uri, values, null, null);
            Log.d(LOG_TAG, "updateReminder");
            return uri;
        } else {
            Log.d(LOG_TAG, "insertReminder");
            return context.getContentResolver().insert(Uri.parse(CALENDAR_REMINDER_URL), values);
        }
    }

    /**
     * 创建或更新一个日历事件
     */
    private static Uri createOrUpdateCalendarEvent(Context context, int accountId, CalendarEvent calendarEvent) {
        ContentValues event = new ContentValues();
        event.put("title", calendarEvent.title);
        event.put("description", calendarEvent.content);
        // 插入账户的id
        event.put("calendar_id", accountId);

        long start = calendarEvent.startDate.getTimeInMillis();
        long end = calendarEvent.endDate.getTimeInMillis();

        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, sTimeZoneId);

        Uri uri;
        if (calendarEvent.idInLocal == -1) {
            uri = insertEvent(context, calendarEvent, event);
        } else {
            uri = updateEvent(context, event, calendarEvent.idInLocal);
            Log.d(LOG_TAG, "update:" + calendarEvent.toString());
        }
        return uri;
    }

    /**
     * 更新事件
     */
    @NonNull
    private static Uri updateEvent(Context context, ContentValues event, long idInLocal) {
        Uri uri = getUri(idInLocal, CALENDAR_EVENT_URL);
        context.getContentResolver().update(uri, event, null, null);
        return uri;
    }

    /**
     * 新建事件
     */
    @Nullable
    private static Uri insertEvent(Context context, CalendarEvent calendarEvent, ContentValues event) {
        Log.d(LOG_TAG, "insert:" + calendarEvent.toString());
        Uri uri = context.getContentResolver().insert(Uri.parse(CALENDAR_EVENT_URL), event);
        if (uri != null) {
            SharedPrefUtil.open(EVENT_SHARPF).putLong(calendarEvent.idInService, ContentUris.parseId(uri));
        }
        return uri;
    }

    /**
     * 将id解析为uri
     */
    private static Uri getUri(long id, String url) {
        return ContentUris.withAppendedId(Uri.parse(url), id);
    }

    /**
     * 查询对应的日历事件是否存在，存在则为赋值为{@link CalendarEvent#idInLocal}
     */
    private static void assignIdOfCalendarEvent(Context context, CalendarEvent calendarEvent) {
        long cacheId = SharedPrefUtil.open(EVENT_SHARPF).getLong(calendarEvent.idInService);

        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDAR_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null)
                return;
            if (eventCursor.getCount() > 0) {
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    long id = eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));
                    /*确保Sharpf和本地日历同时存在这个CalendarEvent的记录*/
                    if (id == cacheId) {
                        calendarEvent.idInLocal = id;
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    private static long queryReminderOfCalendarEvent(Context context, Uri eventUri) {
        long parseId = ContentUris.parseId(eventUri);
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDAR_REMINDER_URL), null, null, null, null);
        try {
            if (eventCursor == null)
                return -1;
            if (eventCursor.getCount() > 0) {
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    long id = eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Reminders.EVENT_ID));
                    if (parseId == id) {
                        return eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Reminders._ID));
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        return -1;
    }

    /**
     * 查询日历账户id
     *
     * @param context 上下文
     * @return id
     */
    private static int queryCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDAR_URL), null, null, null, null);
        try {
            if (userCursor == null) return -1;

            int count = userCursor.getCount();
            //存在现有账户，取第一个账户的id返回
            if (count > 0) {
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加一个日历账户
     *
     * @param context context
     * @return 是否添加成功
     */
    private static boolean addCalendarAccount(Context context) {
        if (sCreatedCalendarAccount == null) {
            sCreatedCalendarAccount = DEFAULT_ACCOUNT;
        }
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();

        value.put(CalendarContract.Calendars.NAME, sCreatedCalendarAccount.name);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, sCreatedCalendarAccount.email);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, sCreatedCalendarAccount.displayName);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, sEventColor);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, sCreatedCalendarAccount.email);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDAR_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, sCreatedCalendarAccount.email)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        return result != null;
    }

    public final static class CalendarEvent {
        /*标题*/
        public String title;
        /*内容*/
        public String content;
        /*开始日期*/
        public Calendar startDate;
        /*结束日期*/
        public Calendar endDate;
        /*提醒时间以分钟为单位的偏移量，例：5分钟前，15分钟前*/
        public int remindMinutes;
        /*在服务器存储的id，这个id用于和本地ContentResolver的id相关联，以实现更新日程的功能*/
        public String idInService;
        /*ContentResolver中查询到的本地id*/
        private long idInLocal = -1;

        @Override
        public String toString() {
            return "CalendarEvent{" +
                    "title='" + title + '\'' +
                    ", remindMinutes=" + remindMinutes +
                    ", idInService='" + idInService + '\'' +
                    ", idInLocal=" + idInLocal +
                    '}';
        }
    }

    @SuppressWarnings("WeakerAccess")
    public final static class CalendarAccount {
        public String name;
        public String email;
        public String displayName;

        public CalendarAccount(String name, String email, String displayName) {
            this.name = name;
            this.email = email;
            this.displayName = displayName;
        }

        public CalendarAccount() {
        }
    }
}
