package com.yhy.mz.tv.ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;
import com.yhy.mz.tv.R;
import com.yhy.mz.tv.cache.KV;
import com.yhy.mz.tv.component.base.BaseActivity;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.parser.ParserEngine;
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.mz.tv.utils.ViewUtils;
import com.yhy.router.EasyRouter;
import com.yhy.router.annotation.Autowired;
import com.yhy.router.annotation.Router;

import java.util.Timer;
import java.util.TimerTask;

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

    private Timer mProgressTimer;

    private StyledPlayerView pvPlayer;
    private ExoPlayer mExoPlayer;
    private ProgressBar pbLoading;

    private boolean mIsFullScreen;
    private ConstraintLayout clPlayerContainer;
    private DefaultTimeBar tbProgress;

    @Override
    protected int layout() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView() {
        Evtor.instance.register(this);

        clPlayerContainer = $(R.id.cl_player_container);
        pvPlayer = $(R.id.pv_player);
        pbLoading = $(R.id.pb_loading);
        tbProgress = $(R.id.tb_progress);

        //创建一个播放器
        mExoPlayer = new ExoPlayer.Builder(this).build();
        pvPlayer.setUseController(false);
        pvPlayer.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_NEVER);
        pvPlayer.setPlayer(mExoPlayer);//将播放器绑定到播放器视图上
        mExoPlayer.setPlayWhenReady(true);//设置播放器在准备好后开始播放
        mExoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_OFF);//设置播放器重复播放

        mProgressTimer = new Timer();
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
        MediaItem mMediaItem = new MediaItem.Builder()
                .setUri(url)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .build();
        long position = KV.instance.kv().getLong(mVideo.pageUrl, 0);
        mExoPlayer.setMediaItem(mMediaItem, position);
        mExoPlayer.prepare();
        mExoPlayer.play();

        tbProgress.setBufferedPosition(mExoPlayer.getBufferedPosition());
        tbProgress.setPosition(position);
        tbProgress.hideScrubber(true);

        pvPlayer.postDelayed(() -> performFullScreenOperations(true), 3000);
    }

    @Override
    protected void initEvent() {
        mExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                if (isLoading) {
                    // 加载中
                    LogUtils.iTag(TAG, "加载中");
                    return;
                }

                LogUtils.iTag(TAG, "加载完成");
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_BUFFERING:
                        // 缓冲中
                        LogUtils.iTag(TAG, "缓冲中");
                        pbLoading.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_READY:
                        // 准备完成
                        LogUtils.iTag(TAG, "准备完成");
                        tbProgress.setDuration(mExoPlayer.getDuration());
                        pbLoading.setVisibility(View.GONE);
                        break;
                    case Player.STATE_ENDED:
                        // 播放完成
                        LogUtils.iTag(TAG, "播放完成");
                        break;
                    case Player.STATE_IDLE:
                        // 播放器已卸载
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    LogUtils.iTag(TAG, "正在播放");
                    return;
                }

                // 未播放
                LogUtils.iTag(TAG, "停止中");
            }

            @Override
            public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
                if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                    long position = player.getCurrentPosition();
                    LogUtils.iTag(TAG, "播放状态改变了", position);
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                LogUtils.eTag(TAG, "出错啦", error.getLocalizedMessage());
                // 尝试再次播放
                mExoPlayer.play();
            }
        });

        mProgressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    long position = mExoPlayer.getCurrentPosition();
                    if (position > 0 && null != mVideo) {
                        tbProgress.setPosition(position);
                        tbProgress.setBufferedPosition(mExoPlayer.getBufferedPosition());
                        KV.instance.kv().putLong(mVideo.pageUrl, position);
                    }
                });
            }
        }, 0, 1);
    }

    @Override
    protected void setDefault() {
    }

    @Override
    public void onBackPressed() {
        if (mIsFullScreen) {
            performFullScreenOperations(false);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (null != mExoPlayer) {
            mExoPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (null != mExoPlayer) {
            mExoPlayer.play();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (null != mExoPlayer) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
        if (null != mProgressTimer) {
            mProgressTimer.cancel();
        }
        Evtor.instance.unregister(this);
        super.onDestroy();
    }

    @Subscribe("extracted")
    public void extracted(String url) {
        LogUtils.iTag(TAG, "已提取到 m3u8 地址:", url);
        Evtor.instance.subscribe("onWebViewParseSuccess").emit(url);
        play(url);
    }

    public void performFullScreenOperations(boolean fullscreen) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ViewGroup.LayoutParams params = clPlayerContainer.getLayoutParams();
        if (!fullscreen) {
            params.width = ViewUtils.dp2px(160);
            params.height = ViewUtils.dp2px(90);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        clPlayerContainer.setLayoutParams(params);
        clPlayerContainer.setKeepScreenOn(fullscreen);
        mIsFullScreen = fullscreen;
    }
}
