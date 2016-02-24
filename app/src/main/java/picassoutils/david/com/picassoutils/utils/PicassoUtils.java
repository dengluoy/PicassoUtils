package picassoutils.david.com.picassoutils.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
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

    /**
     * 用于存储图片
     */
    private static Map<Integer, Drawable> mDrawableMap;

    private static volatile Picasso mInstance;
    private Context mContext;
    private static volatile File mDirCacheFile;

    public static void initPicasso(@NonNull Context context) {
        if(mInstance == null) {
            synchronized (PicassoUtils.class) {
                mInstance = Picasso.with(context);
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

<<<<<<< 5bd553e84a3bd0a4c5a349eb744e3955c0178bd7

=======
    /**
     * 返回uri对应的图片缓存所在的路径
     * @return  图片绝对路径
     */
    public static String getCachePath(Context context, String uri) {

        String dirCache = context.getApplicationContext().getCacheDir() + File.separator + PICASSO_CACHE;

        //后续跟上。
        return dirCache;
    }
>>>>>>> ls

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

        into(context, mInstance.load(uri), imageView,defaultResId, null);
    }

    public static void into(Context context, String uri, ImageView imageView, @DrawableRes int defaultResId, Callback callback) {
        into(context, mInstance.load(uri), imageView, defaultResId,callback);
    }

    /**
     * 加载文件缓存图片
     * @param context
     * @param uri           图片uri
     */
    public static void intoCacheFile(Context context,String uri, ImageView imageView, @DrawableRes int defaultResId) {
        into(context,mInstance.load(uri).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE),imageView, defaultResId, null);
    }

    private static void into(Context context, RequestCreator requestCreator, ImageView imageView, @DrawableRes int defaultResId, Callback callback) {

        Drawable defaultDrawble = mDrawableMap.get(defaultResId);
        if(defaultDrawble == null) {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                defaultDrawble = context.getDrawable(defaultResId);
            } else {
                defaultDrawble = context.getResources().getDrawable(defaultResId);
            }
            mDrawableMap.put(defaultResId, defaultDrawble);
        }
        //考虑到我们应用已经在Android version 4.0 以上 所以全部采用ARGB_8888,并没有解决3.0以下兼容565
        requestCreator.config(Bitmap.Config.ARGB_8888).centerCrop().tag(context).placeholder(defaultDrawble).fit().into(imageView, callback);
    }

    public static void setPauseOnScrollListener(Context context, final ListView listView, boolean pauseOnScroll, boolean pauseOnFingli) {
        setPauseOnScrollListener(context, listView, pauseOnScroll, pauseOnFingli, null);
    }

    /**
     * 设置滑动状态停止加载图片
     * @param context
     * @param listView              滑动列表
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

}
