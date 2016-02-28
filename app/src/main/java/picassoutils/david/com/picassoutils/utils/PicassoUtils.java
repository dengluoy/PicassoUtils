package picassoutils.david.com.picassoutils.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoNetworkListener;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import picassoutils.david.com.picassoutils.R;

/**
 * @author WeiDeng
 * @date 16/2/16
 * @description picasso工具类
 */
public class PicassoUtils {

    public final String TAG = getClass().getSimpleName();

    /**
     * 由于Picasso内部这个字段是private的， 暂时先直接写死不用反射获取。
     */
    private static final String PICASSO_CACHE = "picasso-cache";

    private static final int NO_RES_ID = 0;

    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB

    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    /**
     * 用于存储图片
     */
    private static Map<Integer, Drawable> mDrawableMap;

    /** 记录图片请求开始时间，用于计算图片请求响应耗时
     *  由于图片框架使用多线程，此处需要同步操作
     */
    private static Map<String, Long> mStartTimeMap = Collections.synchronizedMap(new HashMap<String, Long>());
    private static volatile Picasso mInstance;
    private Context mContext;
    private static volatile File mDirCacheFile;

    public static void initPicasso(@NonNull Context context) {
        if(mInstance == null) {
            synchronized (PicassoUtils.class) {
                mInstance = Picasso.with(context).networkListener(mNetworkListener);
                mDrawableMap = new HashMap<>();
            }
        }
    }

    /**
     * 清除一组内存缓存
     * @param paths
     */
    public void clearGroupCache(List<String> paths) {
        int N = paths.size();
        for(int i = 0; i < N; i++) {
            clearItemCache(paths.get(i));
        }
    }

    /**
     * 清除一项内存缓存
     * @param path
     */
    public void clearItemCache(String path) {
        mInstance.invalidate(path);
    }

    /**
     * 获取缓存文件目录
     * @param context
     * @return
     */
    public static File getCacheFile(Context context) {
        if(mDirCacheFile == null) {
            mDirCacheFile = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
        }
        return mDirCacheFile;
    }

    /**
     * 返回缓存文件夹中所有文件占用的总空间大小
     * @return
     */
    public static double getDiskCacheSize(Context context) {
        double size = 0;

        if (checkDiskCache(context)) {
            File cacheFile = getCacheFile(context);
            File[] files = cacheFile.listFiles();
            int N = files.length;
            for(int i = 0;i < N;i++) {
                size += files[i].length();
            }
        }

        return size != 0 ? size / 1024 / 1024 : 0;
    }

    /**
     * 清空缓存文件
     * @param context
     * @param dirFile 需清除的缓存目录
     * @return
     */
    public static boolean clearDiskCache(Context context, File dirFile) {
        if(dirFile.isDirectory()) {
            String[] list = dirFile.list();
            int N = list.length;
            for(int i = 0; i < N; i++) {
                File file = new File(dirFile, list[i]);
                boolean succesed = clearDiskCache(context, file);
                if(!succesed) {
                    return false;
                }
            }
        }
        return dirFile.delete();
    }

    /**
     * 清空缓存目录
     * @param context
     * @return
     */
    public static boolean clearDiskCache(Context context) {
        return clearDiskCache(context, new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE));
    }


    public static boolean checkDiskCache(Context context) {
        File dirCache = getCacheFile(context);
        return dirCache.isDirectory() && dirCache.exists();
    }

    /**
     * 判断默认资源图片是否是.9
     *  该方法实现纯属针对与公司项目开发
     * @param defaultResId
     * @return
     */
    public static boolean isNinePatchResId(@DrawableRes int defaultResId) {
        if (defaultResId == R.mipmap.default_unit_big || defaultResId == R.mipmap.default_unit_middle
                || defaultResId == R.mipmap.default_unit_small) {
            return true;
        }
        return false;
    }

    public static void into(Context context, String uri, ImageView imageView, @DrawableRes int defaultResId) {

        if(isNinePatchResId(defaultResId)) imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        into(context, mInstance.load(uri), imageView, defaultResId, null);
    }

    public static void into(Context context, String uri, ImageView imageView, @DrawableRes int defaultResId, Callback callback) {
        into(context, mInstance.load(uri), imageView, defaultResId, callback);
    }

    /**
     * 加载文件缓存图片
     * @param context
     * @param uri           图片uri
     */
    public static void intoCacheFile(Context context,String uri, ImageView imageView, @DrawableRes int defaultResId) {
        into(context, mInstance.load(uri).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE), imageView, defaultResId, null);
    }

    private static void into(Context context, RequestCreator requestCreator, ImageView imageView, @DrawableRes int defaultResId, Callback callback) {

        Drawable defaultResDrawble = getDefaultResDrawble(context, defaultResId);

        //考虑到我们应用已经在Android version 4.0 以上 所以全部采用ARGB_8888,并没有解决3.0以下兼容565
        requestCreator.config(Bitmap.Config.ARGB_8888).centerCrop().tag(context).placeholder(defaultResDrawble).fit().into(imageView, callback);
    }

    public static void setPauseOnScrollListener(Context context, final ListView listView, boolean pauseOnScroll, boolean pauseOnFingli) {
        setPauseOnScrollListener(context, listView, pauseOnScroll, pauseOnFingli, null);
    }

    /**
     * 获取默认图片
     * @param context
     * @param defaultResId
     * @return
     */
    public static Drawable  getDefaultResDrawble(Context context, @DrawableRes int defaultResId) {
        Drawable defaultDrawble = mDrawableMap.get(defaultResId);
        if(defaultDrawble == null) {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                defaultDrawble = context.getDrawable(defaultResId);
            } else {
                defaultDrawble = context.getResources().getDrawable(defaultResId);
            }
            mDrawableMap.put(defaultResId, defaultDrawble);
        }

        return defaultDrawble;
    }

    /**
     * 设置滑动状态停止加载图片
     * @param context
     * @param listView              滑动列表,
     * @param pausuOnScroll         是否在滑动状态停止加载
     * @param pauseOnFingli         是否在快速滑动状态停止加载
     * @param customScrollListener  自定义Scroll监听事件
     */
    public static void setPauseOnScrollListener(Context context, final ListView listView, boolean pausuOnScroll, boolean pauseOnFingli, AbsListView.OnScrollListener customScrollListener) {
        listView.setOnScrollListener(new PauseOnScrollListene(context, mInstance, pausuOnScroll, pauseOnFingli, customScrollListener));
    }

    /**
     * 滑动状态时暂停加载图片实现类
     */
    static class PauseOnScrollListene implements AbsListView.OnScrollListener  {

        private Picasso picasso;
        private final boolean pauseOnScroll;
        private final boolean pauseonFling;
        private AbsListView.OnScrollListener externalListener;
        private Context context;

        public PauseOnScrollListene(Context context, Picasso picasso, boolean pauseOnScroll, boolean pauseonFling, AbsListView.OnScrollListener customListener) {
            this.pauseonFling = pauseonFling;
            this.pauseOnScroll = pauseOnScroll;
            this.picasso = picasso;
            this.externalListener = customListener;
            this.context = context;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            switch (scrollState) {
                case SCROLL_STATE_IDLE:
                    this.picasso.resumeTag(context);
                    break;
                case SCROLL_STATE_TOUCH_SCROLL:
                    if(this.pauseOnScroll) {
                        this.picasso.pauseTag(context);
                    }
                    break;
                case SCROLL_STATE_FLING:
                    if(this.pauseonFling) {
                        this.picasso.pauseTag(context);
                    }
                    break;
            }
            if(this.externalListener != null) {
                this.externalListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(this.externalListener != null) {
                this.externalListener.onScroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
            }
        }
    }

    static PicassoNetworkListener mNetworkListener = new PicassoNetworkListener() {
        @Override
        public void onNetworkResponseSucceed(String uri, long time, long imageSize) {
            Long startTime = mStartTimeMap.get(uri);
            startTime = startTime != null ? 0 : startTime;
            if(startTime > 0) {
                long timeInterval = time - startTime;
                mStartTimeMap.remove(uri);
                Log.d("PicassNetworkListener", "onNetworkResponseSucceed:  uri : " + uri + ", time : " + time + ", imageSize : " + imageSize + ", timeInterval: " + timeInterval);
            }


        }

        @Override
        public void onNetworkLoadFinish(String uri, long time, long imageSize) {
            Long startTime = mStartTimeMap.get(uri);
            if(startTime > 0) {
                long timeInterval = time - startTime;
                mStartTimeMap.remove(uri);
                Log.d("PicassNetworkListener", "onNetworkLoadFinish:  uri : " + uri + ", time : " + time + ", imageSize : " + imageSize + ", timeInterval:" + timeInterval );
            }


        }

        @Override
        public void onNetworkLoadStart(String uri, long time) {
            mStartTimeMap.put(uri,time);
            Log.d("PicassNetworkListener", "onNetworkLoadStart:  uri : " + uri + ", time : " + time);
        }

        @Override
        public void onNetworkLoadFail(String uri, long time, Throwable e, int errorCode) {
            Long startTime = mStartTimeMap.get(uri);
            startTime = startTime == null ? 0 : mStartTimeMap.get(uri);
            if(startTime > 0) {
                long timeInterval = time - startTime;
                mStartTimeMap.remove(uri);
                Log.d("PicassNetworkListener", "onNetworkLoadFail:  uri : " + uri + ", exception : " + e + ", errorCode : " + errorCode + ", timeInterval: " + timeInterval);
            }


        }
    };
}
