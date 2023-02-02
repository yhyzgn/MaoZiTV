package com.yhy.mz.tv.ui;

import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;
import com.yhy.mz.tv.R;
import com.yhy.mz.tv.component.base.BaseActivity;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.parser.ParserEngine;
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.router.EasyRouter;
import com.yhy.router.annotation.Autowired;
import com.yhy.router.annotation.Router;

import java.util.HashMap;

/**
 * 详情页
 * <p>
 * Created on 2023-01-29 21:10
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Router(url = "/activity/detail")
public class DetailActivity extends BaseActivity {
    private static final String TAG = "DetailActivity";

    @Autowired
    public Video mVideo;

    private boolean mIsExtracted;
    private RelativeLayout rlWvContainer;
    private StyledPlayerView pvPlayer;
    //    private VideoPlayer vdPlayer;
//    private GSYVideoOptionBuilder mBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Evtor.instance.register(this);
    }

    @Override
    protected int layout() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView() {
        rlWvContainer = $(R.id.rl_wv_container);
//        vdPlayer = $(R.id.vd_player);

        pvPlayer = $(R.id.pv_player);

//        vdPlayer.getTitleTextView().setVisibility(View.GONE);
//        vdPlayer.getBackButton().setVisibility(View.GONE);
//        vdPlayer.setShowFullAnimation(true);
//        vdPlayer.getFullscreenButton().setVisibility(View.GONE);
//        vdPlayer.getStartButton().setVisibility(View.GONE);
//        vdPlayer.previewMode();

//        mBuilder = new GSYVideoOptionBuilder()
//                .setIsTouchWiget(true)
//                .setRotateViewAuto(false)
//                .setLockLand(false)
//                .setShowFullAnimation(false)
//                .setNeedLockFull(true)
//                .setSeekRatio(1)
//                .setCacheWithPlay(false)
//                .setVideoTitle("测试视频")
//                .setVideoAllCallBack(new GSYSampleCallBack() {
//                    @Override
//                    public void onPrepared(String url, Object... objects) {
//                        super.onPrepared(url, objects);
//                        //开始播放了才能旋转和全屏
//                    }
//
//                    @Override
//                    public void onQuitFullscreen(String url, Object... objects) {
//                        super.onQuitFullscreen(url, objects);
//                        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
//                        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
//                    }
//                });

        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
    }

    @Override
    protected void initData() {
        EasyRouter.getInstance().inject(this);

        ParserEngine.instance.process(this, mVideo.pageUrl);
        // 接口获取播放链接
//        ParserApi.instance.danMu(mVideo.pageUrl, url -> {
////            LogUtils.iTag(TAG, "获取到播放链接：", url);
//            // 播放
////            play(url);
//        });
    }

    private void play(String url) {
//        vdPlayer.release();
//        mBuilder.setUrl(url)
//                .setCacheWithPlay(false)
//                .setMapHeadData(Maps.of("android-allow-cross-domain-redirect", "1"))
//                .build(vdPlayer);
//        mBuilder.build(vdPlayer);
//        vdPlayer.postDelayed(() -> vdPlayer.startPlayLogic(), 200);

//        vdPlayer.postDelayed(() -> vdPlayer.startWindowFullscreen(this, true, true), 3000);

        ExoPlayer mExoPlayer = new ExoPlayer.Builder(this).build();//创建一个播放器
        pvPlayer.setPlayer(mExoPlayer);//将播放器绑定到播放器视图上
        mExoPlayer.setPlayWhenReady(true);//设置播放器在准备好后开始播放
        mExoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);//设置播放器重复播放
        MediaItem mMediaItem = new MediaItem.Builder()
                .setUri(url)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .build();
        mExoPlayer.setMediaItem(mMediaItem);//设置播放器播放的媒体
        mExoPlayer.prepare();//准备播放器
        mExoPlayer.play();//播放播放器
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void setDefault() {
    }

    @Override
    public void onBackPressed() {
//        if (GSYVideoManager.backFromWindowFull(this)) {
//            return;
//        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        GSYVideoManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        GSYVideoManager.releaseAllVideos();
        Evtor.instance.unregister(this);
    }

    @Subscribe("extracted")
    public void extracted(String url) {
        mIsExtracted = true;
        LogUtils.iTag(TAG, "已提取到 m3u8 地址:", url);
        Evtor.instance.subscribe("onWebViewParseSuccess").emit(url);

        play(url);
    }
}
