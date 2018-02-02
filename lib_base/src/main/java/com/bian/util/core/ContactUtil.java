package com.bian.util.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/7/4 19:59
 * 类描述：
 */

public class ContactUtil {
    private ContactUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 打开系统自带联系人界面
     */
    public static void startContactActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static List<Contact> getContact(Context context, Intent data) {
        List<Contact> contacts = new ArrayList<>();
        Uri uri = data.getData();
        if (uri != null) {
            Cursor cursor = context.getContentResolver()
                    .query(uri,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                            null, null, null);
            if (cursor == null) {
                return contacts;
            }
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                String name = cursor.getString(1);
                contacts.add(new Contact(name, number));
            }
            cursor.close();
        }
        return contacts;
    }

    public static class Contact {
        private String name;
        private String phone;

        public Contact(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
