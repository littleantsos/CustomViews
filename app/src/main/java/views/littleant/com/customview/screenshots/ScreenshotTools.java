package views.littleant.com.customview.screenshots;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import views.littleant.com.customview.MyAppication;

/**
 * Created by hou on 2017/7/25.14:13
 * introduce :
 */

public class ScreenshotTools {

    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap"
    };

    /** 读取媒体数据库时需要读取的列 */
    private static final String[] MEDIA_PROJECTIONS =  {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT
    };
    private static final String TAG = "ScreenshotActivity";

    /** 内部存储器内容观察者 */
    private ContentObserver mInternalObserver;

    /** 外部存储器内容观察者 */
    private ContentObserver mExternalObserver;

    private HandlerThread mHandlerThread;
    private Handler mHandler;


    public ScreenshotTools() {
        mHandlerThread = new HandlerThread("Screenshot_Observer");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        // 初始化
        mInternalObserver = new MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, mHandler);
        mExternalObserver = new MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mHandler);

        // 添加监听
        MyAppication.getApplication().getContentResolver().registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                false,
                mInternalObserver
        );
        MyAppication.getApplication().getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false,
                mExternalObserver
        );
    }

    /**
     * 媒体内容观察者(观察媒体数据库的改变)
     */
    private class MediaContentObserver extends ContentObserver {

        private Uri mContentUri;

        public MediaContentObserver(Uri contentUri, Handler handler) {
            super(handler);
            mContentUri = contentUri;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG, mContentUri.toString());
            handleMediaContentChange(mContentUri);
        }
    }

    private void handleMediaContentChange(Uri contentUri) {
        Cursor cursor = null;
        try {
            // 数据改变时查询数据库中最后加入的一条数据
            cursor = MyAppication.getApplication().getContentResolver().query(
                    contentUri,
                    MEDIA_PROJECTIONS,
                    null,
                    null,
                    MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1"
            );

            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                return;
            }

            // 获取各列的索引
            int dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            int dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int widthIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH);
            int heightIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT);

            // 获取行数据
            String data = cursor.getString(dataIndex);
            long dateTaken = cursor.getLong(dateTakenIndex);
            long width = cursor.getLong(widthIndex);
            long height = cursor.getLong(heightIndex);

            // 处理获取到的第一行数据
            handleMediaRowData(data, dateTaken, width,height);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * 处理监听到的资源
     */
    private void handleMediaRowData(String data, long dateTaken, long width, long height) {
        if (checkScreenShot(data, dateTaken)) {
            Log.d(TAG, data + " " + dateTaken);
            if(screenshotListener != null){
                screenshotListener.onScreenShot(new ScreenShotResponse(data,dateTaken,width,height));
            }
        } else {
            Log.d(TAG, "Not screenshot event");
        }
    }

    /**
     * 判断是否是截屏
     */
    private boolean checkScreenShot(String data, long dateTaken) {

        data = data.toLowerCase();
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (String keyWork : KEYWORDS) {
            if (data.contains(keyWork)) {
                return true;
            }
        }
        return false;
    }

    public void unregisterContentObserver(){
        MyAppication.getApplication().getContentResolver().unregisterContentObserver(mInternalObserver);
        MyAppication.getApplication().getContentResolver().unregisterContentObserver(mExternalObserver);
    }

    public interface ScreenShotListener {
        void onScreenShot(ScreenShotResponse response);
    }

    public static class ScreenShotResponse implements Parcelable{
        public String path;
        public long dataToken;
        public long width;
        public long height;

        public ScreenShotResponse(String path, long dataToken,long width,long height) {
            this.path = path;
            this.dataToken = dataToken;
            this.width = width;
            this.height = height;
        }

        protected ScreenShotResponse(Parcel in) {
            path = in.readString();
            dataToken = in.readLong();
            width = in.readLong();
            height = in.readLong();
        }

        public static final Creator<ScreenShotResponse> CREATOR = new Creator<ScreenShotResponse>() {
            @Override
            public ScreenShotResponse createFromParcel(Parcel in) {
                return new ScreenShotResponse(in);
            }

            @Override
            public ScreenShotResponse[] newArray(int size) {
                return new ScreenShotResponse[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(path);
            dest.writeLong(dataToken);
            dest.writeLong(width);
            dest.writeLong(height);
        }
    }

    private ScreenShotListener screenshotListener;
    public void setOnScreenshotListener(ScreenShotListener screenshotListener){
        this.screenshotListener = screenshotListener;
    }
}
