package com.yhy.mz.tv.ui;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.lodz.android.mmsplayer.ijk.setting.IjkPlayerSetting;
import com.lodz.android.mmsplayer.impl.MmsVideoView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;
import com.yhy.mz.tv.R;
import com.yhy.mz.tv.cache.KV;
import com.yhy.mz.tv.component.base.BaseActivity;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.parser.Parser;
import com.yhy.mz.tv.parser.ParserEngine;
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.mz.tv.utils.ViewUtils;
import com.yhy.mz.tv.widget.SilenceTimeBar;
import com.yhy.mz.tv.widget.web.ParserWebView;
import com.yhy.mz.tv.widget.web.ParserWebViewDefault;
import com.yhy.mz.tv.widget.web.ParserWebViewX5;
import com.yhy.router.EasyRouter;
import com.yhy.router.annotation.Autowired;
import com.yhy.router.annotation.Router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tv.danmaku.ijk.media.player.IMediaPlayer;

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

    private ExecutorService mParserService;

    @Autowired
    public Video mVideo;
    @Autowired
    public int mChanCode;

    private Chan mChan;

    private final List<ParserWebView> mWvList = new ArrayList<>();

    /**
     * 解析器黑名单，不可用的自动进入该列表
     */
    private final List<Parser> mParserBlackList = new ArrayList<>();

    /**
     * 记录解析器错误次数
     */
    private final Map<Parser, Integer> mParserErrorCountMap = new HashMap<>();

    /**
     * 当前已成功解析的解析器
     */
    private Parser mParser;


    private Timer mProgressTimer;

    private StyledPlayerView pvPlayer;
    private ExoPlayer mExoPlayer;
    private AppCompatTextView tvParsingLog;
    private ProgressBar pbLoading;

    private boolean mIsFullScreen;
    private ConstraintLayout clPlayerContainer;
    private SilenceTimeBar tbProgressBottom;


    private MmsVideoView vvPlayer;

    @Override
    protected int layout() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView() {
        Evtor.instance.register(this);

        clPlayerContainer = $(R.id.cl_player_container);
        pvPlayer = $(R.id.pv_player);
        tvParsingLog = $(R.id.tv_parsing_log);
        pbLoading = $(R.id.pb_loading);
        tbProgressBottom = $(R.id.tb_progress);

        // 创建一个播放器
        mExoPlayer = new ExoPlayer.Builder(this).build();
        pvPlayer.setUseController(false);
        pvPlayer.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_NEVER);
        pvPlayer.setPlayer(mExoPlayer);// 将播放器绑定到播放器视图上
        mExoPlayer.setPlayWhenReady(true);// 设置播放器在准备好后开始播放
        mExoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_OFF);// 设置播放器重复播放

        vvPlayer = $(R.id.vv_player);
        IjkPlayerSetting setting = IjkPlayerSetting.getDefault();
        setting.renderViewType = IjkPlayerSetting.RenderViewType.SURFACE_VIEW;
        setting.isUsingOpenSLES = false;
        vvPlayer.init(setting);

        mProgressTimer = new Timer();
    }

    @Override
    protected void initData() {
        EasyRouter.getInstance().inject(this);
        mChan = Chan.parse(mChanCode);

        if (mChan == Chan.KU_FILM || mChan == Chan.KU_EPISODE) {
            OkGo.<String>get(mVideo.pageUrl).execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    // <meta itemprop="url" content="https://v.youku.com/v_show/id_XNTk0MTM4OTgzNg==.html?s=fcdf63ffd2a841b795e2"/>
                    String html = response.body().replaceAll("[\t\n\\s]", "");
                    Pattern pattern = Pattern.compile("^.*?<metaitemprop=\"url\"content=\"(.*?)\".*$");
                    Matcher matcher = pattern.matcher(html);

                    System.out.println(html);

                    if (matcher.matches()) {
                        String pageUrl = matcher.group(1);
                        LogUtils.iTag(TAG, "优酷播放源提取到视频地址", pageUrl);
                        mVideo.pageUrl = pageUrl;

                        startParsing();
                    } else {
                        error("未匹配到视频源地址");
                    }
                }
            });
        } else {
            startParsing();
        }
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
                        tvParsingLog.setVisibility(View.GONE);
                        tbProgressBottom.setVisibility(View.VISIBLE);
                        tbProgressBottom.setDuration(mExoPlayer.getDuration());
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
                pbLoading.setVisibility(View.VISIBLE);
                // 播放出错啦，多半是视频源解析出错，或者解析站的 Token 过期之类的，需要重新解析或者换个源即可
                // 播放错误次数超过 x 次的解析器自动进入黑名单，下一次解析将其直接失效
                int errorCount = Optional.ofNullable(mParserErrorCountMap.getOrDefault(mParser, 1)).orElse(1);
                if (errorCount >= 2) {
                    //LogUtils.iTag(TAG, "解析器【" + mParser.prs().getName() + "】自动进入黑名单");
                    mParserBlackList.add(mParser);
                } else {
                    errorCount++;
                    //LogUtils.iTag(TAG, "解析器【" + mParser.prs().getName() + "】播放出错次数：" + errorCount);
                    mParserErrorCountMap.put(mParser, errorCount);
                }

                //startParsing();
            }
        });

        mProgressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    long position = mExoPlayer.getCurrentPosition();
                    if (position > 0 && null != mVideo) {
                        tbProgressBottom.setPosition(position);
                        tbProgressBottom.setBufferedPosition(mExoPlayer.getBufferedPosition());
                        KV.instance.kv().putLong(mVideo.pageUrl, position);
                    }
                });
            }
        }, 0, 1);

        vvPlayer.setListener(new MmsVideoView.Listener() {
            @Override
            public void onPrepared() {
                LogUtils.iTag(TAG, "IjkPlayer", "已就绪");
                // 准备就绪就自动开始播放
                //vvPlayer.start();
            }

            @Override
            public void onBufferingStart() {
                LogUtils.iTag(TAG, "IjkPlayer", "开始缓冲");
            }

            @Override
            public void onBufferingEnd() {
                LogUtils.iTag(TAG, "IjkPlayer", "缓冲完成");
            }

            @Override
            public void onCompletion() {
                LogUtils.iTag(TAG, "IjkPlayer", "播放完成");
            }

            @Override
            public void onError(int errorType, String msg) {
                LogUtils.eTag(TAG, "IjkPlayer", "播放异常", errorType, msg);
            }

            @Override
            public void onMediaPlayerCreated(@NonNull IMediaPlayer mediaPlayer) {
                LogUtils.iTag(TAG, "IjkPlayer", "播放器创建完成");
            }
        });
    }

    @Override
    protected void setDefault() {
    }

    @Override
    public void onBackPressed() {
        if (mIsFullScreen) {
            toggleScreen(false);
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
        stopParsing(true);

        super.onDestroy();
    }

    @Subscribe("extracted")
    public void extracted(Parser parser, String url) {
        stopParsing(false);
        LogUtils.iTag(TAG, "已提取到 m3u8 地址", parser.prs().getName(), url);
        play(parser, url);
    }

    @Subscribe("parsingLog")
    public void parsingLog(String tag, String parserName, String url) {
        LogUtils.iTag(tag, parserName, url);
        tvParsingLog.setText(parserName + "：" + url);
    }

    private void startParsing() {
        if (null == mParserService) {
            mParserService = Executors.newSingleThreadExecutor();
        }

        List<Parser> parserList = ParserEngine.instance.getParserList(mChan);
        if (parserList.isEmpty()) {
            error("未匹配到解析器");
            return;
        }

        mWvList.clear();
        // 自动过滤黑名单中的解析器
        parserList.stream().filter(it -> !mParserBlackList.contains(it)).forEach(it -> {
            String url = it.prs().getUrl() + mVideo.pageUrl;
            ParserWebView wv = mApp.isX5Already() ? new ParserWebViewX5(this) : new ParserWebViewDefault(this);
            wv.attach(this, it, url);
            mWvList.add(wv);
        });

        mWvList.forEach(it -> mParserService.execute(it::start));

        tvParsingLog.setVisibility(View.VISIBLE);
        tbProgressBottom.setVisibility(View.GONE);
    }

    private void stopParsing(boolean destroy) {
        if (null != mParserService) {
            mParserService.shutdown();
            mParserService = null;
        }
        if (!mWvList.isEmpty()) {
            mWvList.forEach(it -> it.stop(destroy));
            mWvList.clear();
        }
    }

    private void play(Parser parser, String url) {
        String mimeType = parser.mimeType(mChan);
        MediaItem mMediaItem = new MediaItem.Builder()
                .setUri(url)
                .setMimeType(mimeType)
                .build();
        long position = KV.instance.kv().getLong(mVideo.pageUrl, 0);
        mExoPlayer.setMediaItem(mMediaItem, position);
        mExoPlayer.prepare();
        mExoPlayer.play();

        tbProgressBottom.setBufferedPosition(mExoPlayer.getBufferedPosition());
        tbProgressBottom.setPosition(position);
        tbProgressBottom.hideScrubber(true);

        vvPlayer.setVideoPath(url);
        vvPlayer.seekAndStart(position);

        //pvPlayer.postDelayed(() -> toggleScreen(true), 3000);
    }

    private void toggleScreen(boolean fullscreen) {
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
