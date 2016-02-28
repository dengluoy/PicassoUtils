package com.squareup.picasso;

/**
 * @author WeiDeng
 * @FileName com.squareup.picasso.PicassoNetworkListener.java
 * @date 2016-02-29 00:04
 * @describe
 */
public interface PicassoNetworkListener {

    void onNetworkResponseSucceed(String uri, long time, long imageSize);

    void onNetworkLoadFinish(String uri, long time, long imageSize);

    void onNetworkLoadStart(String uri, long time);

    void onNetworkLoadFail(String uri, long time, Throwable e, int errorCode);
}
