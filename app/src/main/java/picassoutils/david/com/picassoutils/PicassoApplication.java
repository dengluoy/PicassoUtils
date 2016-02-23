package picassoutils.david.com.picassoutils;

import android.app.Application;

import picassoutils.david.com.picassoutils.utils.PicassoUtils;

/**
 * @author WeiDeng
 * @date 16/2/23
 * @description
 */
public class PicassoApplication extends Application {

    @Override
    public void onCreate() {

        PicassoUtils.initPicasso(this);

        super.onCreate();
    }
}
