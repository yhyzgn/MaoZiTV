package com.yhy.thank.tv;

import androidx.multidex.MultiDexApplication;

import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.yhy.router.BuildConfig;
import com.yhy.router.EasyRouter;
import com.yhy.router.common.JsonConverter;

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

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        // 路由组件
        EasyRouter.getInstance()
                .init(this)
                .debug(BuildConfig.DEBUG)
                .jsonParser(new JsonConverter() {
                    final Gson gson = new Gson();

                    @Override
                    public <T> T fromJson(String json, Type type) {
                        return gson.fromJson(json, type);
                    }

                    @Override
                    public <T> String toJson(T obj) {
                        return gson.toJson(obj);
                    }
                });

        // 播放器
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        CacheFactory.setCacheManager(ExoPlayerCacheManager.class);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        GSYVideoType.setRenderType(GSYVideoType.SUFRACE);
    }
}
