package com.yhy.mz.tv.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yhy.evtor.Evtor;
import com.yhy.mz.tv.BuildConfig;
import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.Parser;
import com.yhy.mz.tv.parser.ParserEngine;
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.router.EasyRouter;
import com.yhy.router.annotation.Autowired;
import com.yhy.router.annotation.Router;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 资源嗅探
 * <p>
 * https://blog.csdn.net/chy555chy/article/details/104479040
 * <p>
 * Created on 2023-02-04 22:52
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Router(url = "/activity/detective")
public class DetectiveActivity extends AppCompatActivity {
    private static final String TAG = "DetectiveActivity";
    @Autowired
    public String mUrl;
    @Autowired
    public int mPrsCode;

    private Parser mParser;
    private MyWebView mSysWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (needCheckOrientation()) {
            boolean fixOrientation = fixOrientation();
            LogUtils.iTag(TAG, "onCreate fixOrientation when Oreo, result = " + fixOrientation);
        }
        super.onCreate(savedInstanceState);
        EasyRouter.getInstance().inject(this);

        // 1px
        onePixel();

        mParser = ParserEngine.instance.getByPrs(Prs.parse(mPrsCode));
        loadWebView();
    }

    private boolean needCheckOrientation() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating();
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (needCheckOrientation()) {
            LogUtils.iTag(TAG, "setRequestedOrientation avoid calling setRequestedOrientation when Oreo");
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        mSysWebView.stopLoading();
        mSysWebView.destroy();
        super.onDestroy();
    }

    private void onePixel() {
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

    protected void loadWebView() {
        mSysWebView = new MyWebView(this);
        configWebViewSys();
        mSysWebView.loadUrl(mUrl);
        mSysWebView.setVisibility(View.INVISIBLE);
    }

    private void configWebViewSys() {
        ViewGroup.LayoutParams layoutParams = BuildConfig.DEBUG ? new ViewGroup.LayoutParams(800, 800) : new ViewGroup.LayoutParams(1, 1);
        mSysWebView.setFocusable(false);
        mSysWebView.setFocusableInTouchMode(false);
        mSysWebView.clearFocus();
        mSysWebView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        final WebSettings settings = mSysWebView.getSettings();
        settings.setNeedInitialFocus(false);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        if (BuildConfig.DEBUG) {
            settings.setBlockNetworkImage(false);
        } else {
            settings.setBlockNetworkImage(true);
        }
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 自动播放媒体
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(true);
        }
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");

        mSysWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return false;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return true;
            }
        });
        mSysWebView.setWebViewClient(new SysWebClient(this, mParser));
        mSysWebView.setBackgroundColor(Color.TRANSPARENT);

        addContentView(mSysWebView, layoutParams);
    }

    public static class MyWebView extends WebView {
        public MyWebView(@NonNull Context context) {
            super(context);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return false;
        }
    }

    private static class SysWebClient extends WebViewClient {
        private final AppCompatActivity mActivity;
        private final Parser mParser;
        private boolean mExtracted = false;

        public SysWebClient(AppCompatActivity activity, Parser parser) {
            mActivity = activity;
            mParser = parser;
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        @Nullable
        @Override
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            LogUtils.dTag(TAG, "shouldInterceptRequest url: " + url);
            judgeExtracted(url);
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            LogUtils.dTag(TAG, "onLoadResource url: " + url);
            judgeExtracted(url);
            super.onLoadResource(view, url);
        }

        private synchronized void judgeExtracted(String url) {
            if (mParser.isVideoUrl(url) && !mExtracted) {
                mExtracted = true;
                Evtor.instance.subscribe("extracted").emit(url);
                mActivity.finish();
            }
        }
    }
}
