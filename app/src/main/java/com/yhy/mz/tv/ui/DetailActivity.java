package com.yhy.mz.tv.ui;

import android.view.View;

import com.google.android.exoplayer2.util.MimeTypes;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
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
import com.yhy.mz.tv.widget.web.ParserWebView;
import com.yhy.mz.tv.widget.web.ParserWebViewDefault;
import com.yhy.mz.tv.widget.web.ParserWebViewX5;
import com.yhy.router.EasyRouter;
import com.yhy.router.annotation.Autowired;
import com.yhy.router.annotation.Router;

import java.util.ArrayList;
import java.util.Arrays;
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

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

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

    private boolean mIsFullScreen;

    private StandardGSYVideoPlayer vvPlayer;

    @Override
    protected int layout() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initView() {
        Evtor.instance.register(this);

        vvPlayer = $(R.id.vv_player);
        vvPlayer.getTitleTextView().setVisibility(View.GONE);
        vvPlayer.getBackButton().setVisibility(View.GONE);

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
        mProgressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    long position = vvPlayer.getCurrentPlayer().getCurrentPositionWhenPlaying();
                    if (position > 0 && null != mVideo) {
                        KV.instance.kv().putLong(mVideo.pageUrl, position);
                        LogUtils.iTag(TAG, "position = " + position);
                    }
                });
            }
        }, 0, 1);

        vvPlayer.setVideoAllCallBack(new GSYSampleCallBack() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                vvPlayer.post(() -> {
                    //vvPlayer.getCurrentPlayer().setPlayPosition((int) getPosition());
                });
            }

            @Override
            public void onPlayError(String url, Object... objects) {
                super.onPlayError(url, objects);
                LogUtils.eTag(TAG, "出错啦", Arrays.toString(objects));
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
            }
        });
    }

    @Override
    protected void setDefault() {
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        GSYVideoManager.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        GSYVideoManager.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        GSYVideoManager.releaseAllVideos();

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
        vvPlayer.release();

        String mimeType = parser.mimeType(mChan);
        if (MimeTypes.APPLICATION_M3U8.equals(mimeType)) {
            // m3u8 格式，切换成 IJK 播放器
            PlayerFactory.setPlayManager(IjkPlayerManager.class); // ijk 模式
        } else {
            // 其他格式，使用 EXO
            PlayerFactory.setPlayManager(Exo2PlayerManager.class); // EXO 模式
        }

        long position = getPosition();
        LogUtils.iTag(TAG, "setSeekOnStart", position);

        vvPlayer.setUp(url, false, "");
        vvPlayer.setPlayPosition((int) position);
        //vvPlayer.setSeekOnStart(position);
        //vvPlayer.getGSYVideoManager().seekTo(position);
        vvPlayer.startAfterPrepared();
        vvPlayer.startPlayLogic();
    }

    private long getPosition() {
        return KV.instance.kv().getLong(mVideo.pageUrl, 0);
    }
}
