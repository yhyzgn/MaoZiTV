package com.yhy.mz.tv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.multidex.MultiDexApplication;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.yhy.mz.tv.api.RandHeaderInterceptor;
import com.yhy.mz.tv.rand.IpRand;
import com.yhy.mz.tv.rand.UserAgentRand;
import com.yhy.mz.tv.utils.FileUtils;
import com.yhy.mz.tv.utils.ImgUtils;
import com.yhy.mz.tv.utils.JsonUtils;
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.mz.tv.utils.SysUtils;
import com.yhy.mz.tv.utils.ToastUtils;
import com.yhy.mz.tv.utils.ViewUtils;
import com.yhy.router.EasyRouter;
import com.yhy.router.common.JsonConverter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

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
//        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
//            @Override
//            public void onCoreInitFinished() {
//                // 内核初始化完成，可能为系统内核，也可能为系统内核
//                Map<String, Object> map = new HashMap<>();
//                map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
//                map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
//                QbSdk.initTbsSettings(map);
//            }
//
//            /**
//             * 预初始化结束
//             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
//             * @param isX5 是否使用X5内核
//             */
//            @Override
//            public void onViewInitFinished(boolean isX5) {
//
//            }
//        });

        init();
    }

    private void init() {
        initUtils();
        initRouter();
        initOkGo();
        initPlayer();
    }

    private void initUtils() {
        // 工具类
        ToastUtils.init(this);
        ViewUtils.init(this);
        SysUtils.init(this);
        FileUtils.init(this);
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
    }

    private void initRouter() {
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
    }

    private void initPlayer() {
//        ExoSourceManager.setExoMediaSourceInterceptListener(new ExoMediaSourceInterceptListener() {
//            @Override
//            public MediaSource getMediaSource(String dataSource, boolean preview, boolean cacheEnable, boolean isLooping, File cacheDir) {
//                //如果返回 null，就使用默认的
//                return null;
//            }
//
//            /**
//             * 通过自定义的 HttpDataSource ，可以设置自签证书或者忽略证书
//             * demo 里的 GSYExoHttpDataSourceFactory 使用的是忽略证书
//             * */
//            @Override
//            public DataSource.Factory getHttpDataSourceFactory(String userAgent, @Nullable TransferListener listener, int connectTimeoutMillis, int readTimeoutMillis,
//                                                               Map<String, String> mapHeadData, boolean allowCrossProtocolRedirects) {
//                //如果返回 null，就使用默认的
//                GSYExoHttpDataSourceFactory factory = new GSYExoHttpDataSourceFactory(userAgent, listener,
//                        connectTimeoutMillis,
//                        readTimeoutMillis, allowCrossProtocolRedirects);
//                factory.setDefaultRequestProperties(mapHeadData);
//                return factory;
//            }
//        });

        // 播放器
//        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
//        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
    }

    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("MZ");
        // log打印级别，决定了 log 显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        // log颜色级别，决定了 log 在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        // 全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        // 全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        // 全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        // interceptor
        builder.addInterceptor(new RandHeaderInterceptor());
        builder.addNetworkInterceptor(new RandHeaderInterceptor());

        // 全局请求头
        HttpHeaders headers = new HttpHeaders();
        headers.put("User-Agent", UserAgentRand.get());
        headers.put("X-Forwarded-For", IpRand.get());

        OkGo.getInstance()
                .init(this)                               // 必须调用初始化
                .setOkHttpClient(builder.build())               // 建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               // 全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   // 全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               // 全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers);                     // 全局公共头;
    }
}
