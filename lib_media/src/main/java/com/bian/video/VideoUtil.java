package com.bian.video;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bian.video.compress.OnVideoCompressListener;
import com.bian.video.compress.VideoCompressorImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/6/5 14:44
 * desc ${TODO}
 */

public class VideoUtil {
    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    private static final int VIDEO_RECORD_REQUEST = 0x13;
    private VideoConfig videoConfig;
    private OnVideoDataChangedListener onVideoDataChangedListener;

    private VideoUtil() {
        videoConfig = new VideoConfig();
        videoConfig.setLengthLimit(60);
        videoConfig.setSizeLimit(200);
        videoConfig.setRequestCode(VIDEO_RECORD_REQUEST);
    }

    public static VideoUtil getInstance() {
        return Holder.sINSTANCE;
    }

    public static List<VideoInfo> queryVideo(Context context) {
        List<VideoInfo> videoInfoList = new ArrayList<>();
        String[] mediaColumns = new String[]{MediaStore.MediaColumns.DATA,
                BaseColumns._ID, MediaStore.MediaColumns.TITLE, MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.DURATION, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_ADDED};
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns,
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    VideoInfo videoInfo = VideoInfo.createByCursor(cursor, context);
                    videoInfoList.add(videoInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return videoInfoList;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    /**
     * 通过Intent打开手机上可以播放视频的软件来播放
     */
    public static boolean playByOtherApp(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "video/*";
        Uri uri = Uri.parse(url);
        intent.setDataAndType(uri, type);
        boolean isIntentExist = intent.resolveActivity(context.getPackageManager()) != null;
        if (isIntentExist) {
            context.startActivity(intent);
        } else {
            Log.e("lib_media", "No activity found to handle Intent.ACTION_VIEW");
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setData(uri);
            try {
                context.startActivity(intent1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isIntentExist;
    }

    public void openVideoRecord(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// 创建一个请求视频的意图
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);// 设置视频的质量，值为0-1，
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, videoConfig.getLengthLimit());// 设置视频的录制长度，s为单位
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, videoConfig.getSizeLimit());// 设置视频文件大小，字节为单位
        activity.startActivityForResult(intent, videoConfig.getRequestCode());// 设置请求码，在onActivityResult()方法中接收结果
    }

    public void openVideoChoose(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, videoConfig.getRequestCode());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (onVideoDataChangedListener == null) {
            return;
        }
        if (requestCode == videoConfig.getRequestCode() && resultCode == Activity.RESULT_OK) {
            Uri data1 = data.getData();
            onVideoDataChangedListener.onVideoDataChanged(data1);
        }
    }

    public void release() {
        if (onVideoDataChangedListener != null) {
            onVideoDataChangedListener = null;
        }
    }

    public void setOnVideoDataChangedListener(OnVideoDataChangedListener onVideoDataChangedListener) {
        this.onVideoDataChangedListener = onVideoDataChangedListener;
    }

    public void compressVideo(VideoInfo videoInfo, String resultPath, Context context, @NonNull OnVideoCompressListener onVideoCompressListener) {
        VideoCompressorImpl.getCompressor(context).compress(videoInfo, resultPath, onVideoCompressListener);
    }

    public void compressVideo(String filePath, String resultDir, Context context, @NonNull OnVideoCompressListener onVideoCompressListener) {
        VideoInfo videoInfo = VideoInfo.createByFilePath(filePath, context);
        VideoCompressorImpl.getCompressor(context).compress(videoInfo, resultDir, onVideoCompressListener);
    }

    public interface OnVideoDataChangedListener {
        void onVideoDataChanged(Uri data1);
    }

    private static class Holder {
        final static VideoUtil sINSTANCE = new VideoUtil();
    }

}
