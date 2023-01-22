package com.yhy.mz.tv;

import androidx.multidex.MultiDexApplication;

import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.yhy.router.BuildConfig;
import com.yhy.router.EasyRouter;
import com.yhy.router.common.JsonConverter;
import com.yhy.mz.tv.utils.JsonUtils;
import com.yhy.mz.tv.utils.ToastUtils;
import com.yhy.mz.tv.utils.ViewUtils;

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
        // 工具类
        ToastUtils.init(this);
        ViewUtils.init(this);

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
