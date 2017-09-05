package com.bian.video;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.MediaStore;


/**
 * author 边凌
 * date 2017/6/5 15:07
 * desc ${TODO}
 */

public class VideoInfo implements Parcelable {
    private String filePath;
    private String mimeType;
    private Bitmap b;
    private String title;
    private String duration;
    private String size;
    private String createDate;


    static VideoInfo createByCursor(Cursor cursor, Context context) {
        VideoInfo info = new VideoInfo();

        info.setFilePath(cursor.getString(cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
        info.setMimeType(cursor.getString(cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)));
        info.setTitle(cursor.getString(cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)));

        CharSequence time = getDurationFromCursor(cursor);
        info.setDuration(time.toString());

        String sizeStr = getStorageSizeFormCursor(cursor);
        info.setSize(sizeStr);

        String createDate = getCreateDateFromCursor(cursor);
        info.setCreateDate(createDate);

        int id = cursor.getInt(cursor
                .getColumnIndexOrThrow(BaseColumns._ID));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        info.setB(MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), id,
                MediaStore.Images.Thumbnails.MICRO_KIND, options));

        return info;
    }

    private static String getCreateDateFromCursor(Cursor cursor) {
        long createDateSeconds = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED));
//        return TimeUtil.hourMinute(createDateSeconds * 1000);
        return String.valueOf(createDateSeconds);
    }

    private static String getStorageSizeFormCursor(Cursor cursor) {
        long size = cursor
                .getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
//        return UnitUtil.formatStorageUnit(size);
        return String.valueOf(size);
    }

    private static CharSequence getDurationFromCursor(Cursor cursor) {
        int duration = cursor.getInt(cursor
                .getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
//        return TimeUtil.formatTime("HH:mm:ss", duration + 16 * 60 * 60 * 1000);
        return String.valueOf(duration);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Bitmap getB() {
        return b;
    }

    public void setB(Bitmap b) {
        this.b = b;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Uri uri(){
        return Uri.parse(filePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeString(this.mimeType);
        dest.writeParcelable(this.b, flags);
        dest.writeString(this.title);
        dest.writeString(this.duration);
        dest.writeString(this.size);
        dest.writeString(this.createDate);
    }

    public VideoInfo() {
    }

    protected VideoInfo(Parcel in) {
        this.filePath = in.readString();
        this.mimeType = in.readString();
        this.b = in.readParcelable(Bitmap.class.getClassLoader());
        this.title = in.readString();
        this.duration = in.readString();
        this.size = in.readString();
        this.createDate = in.readString();
    }

    public static final Parcelable.Creator<VideoInfo> CREATOR = new Parcelable.Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel source) {
            return new VideoInfo(source);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    public static VideoInfo createByFilePath(String filePath, Context context){
        VideoInfo videoInfo=new VideoInfo();
        videoInfo.setFilePath(filePath);
        return videoInfo;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "filePath='" + filePath + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", title='" + title + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}
