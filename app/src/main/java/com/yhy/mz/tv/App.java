package com.yhy.mz.tv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.multidex.MultiDexApplication;

import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.yhy.mz.tv.utils.ImgUtils;
import com.yhy.mz.tv.utils.JsonUtils;
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.mz.tv.utils.ToastUtils;
import com.yhy.mz.tv.utils.ViewUtils;
import com.yhy.router.BuildConfig;
import com.yhy.router.EasyRouter;
import com.yhy.router.common.JsonConverter;

import java.io.File;
import java.lang.reflect.Type;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager;

/**
 * 应用
 * <p>
 * Created on 2023-01-19 16:43
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class App extends MultiDexApplication {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        // 工具类
        ToastUtils.init(this);
        ViewUtils.init(this);
        // Log工具类
        LogUtils.getConfig().setApp(this).setLogSwitch(true).setGlobalTag(getClass().getSimpleName());
        ImgUtils.init(new ImgUtils.ImgLoader() {
            @Override
            public <T> void load(Context ctx, ImageView iv, T model) {
                if (null == model) {
                    return;
                }

                LogUtils.iTag(TAG, "使用 Picasso 加载图片", model);

                Picasso picasso = Picasso.get();
                picasso.setLoggingEnabled(true);

                RequestCreator rc;
                if (model instanceof String) {
                    String filepath = (String) model;
                    if (TextUtils.isEmpty(filepath)) {
                        return;
                    }
                    if (filepath.startsWith("/")) {
                        filepath = "file://" + filepath;
                    }
                    rc = picasso.load(filepath);
                } else if (model instanceof Integer) {
                    int resId = (Integer) model;
                    if (resId == 0) {
                        return;
                    }
                    rc = picasso.load(resId);
                } else if (model instanceof Uri) {
                    rc = picasso.load((Uri) model);
                } else if (model instanceof File) {
                    rc = picasso.load((File) model);
                } else {
                    throw new IllegalArgumentException("Unknown model [ " + model + " ] of image resource.");
                }

                Drawable drawable = iv.getDrawable();
                if (null == drawable) {
                    rc.placeholder(R.mipmap.ic_img_holder);
                    rc.error(R.mipmap.ic_img_holder);
                } else {
                    rc.placeholder(drawable)
                            .error(drawable);
                }

                rc.into(iv);
                LogUtils.iTag(TAG, "图片加载完成：", model);
            }
        });

        // 路由组件
        EasyRouter.getInstance()
                .init(this)
                .debug(BuildConfig.DEBUG)
                .jsonParser(new JsonConverter() {
                    @Override
                    public <T> T fromJson(String json, Type type) {
                        return JsonUtils.fromJson(json, type);
                    }

                    @Override
                    public <T> String toJson(T obj) {
                        return JsonUtils.toJson(obj);
                    }
                });

        // 播放器
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        GSYVideoType.setRenderType(GSYVideoType.SUFRACE);
    }
}
