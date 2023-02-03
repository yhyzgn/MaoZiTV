package com.yhy.mz.tv.parser;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
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
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.mz.tv.utils.ToastUtils;
import com.yhy.mz.tv.utils.XWalkUtils;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

/**
 * https://github.com/takagen99/Box/blob/main/app/src/main/java/com/github/tvbox/osc/ui/activity/PlayActivity.java
 * <p>
 * Created on 2023-01-29 21:48
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbsParser implements Parser {

    private static final String TAG = "AbsParser";
    private AppCompatActivity mActivity;
    private MyXWalkView mXwalkWebView;
    private XWalkWebClient mX5WebClient;
    private MyWebView mSysWebView;
    private SysWebClient mSysWebClient;
    private Parser mParser;

    protected void loadWebView(AppCompatActivity activity, String url, Parser parser) {
        mActivity = activity;
        mParser = parser;
        loadWebView();
        LogUtils.iTag(TAG, "url = " + url);
        mSysWebView.loadUrl(url);
        mSysWebView.setVisibility(View.INVISIBLE);
    }

    protected void loadXwalkWebView(AppCompatActivity activity, String url, Parser parser) {
        mActivity = activity;
        mParser = parser;

        XWalkUtils.tryUseXWalk(activity, new XWalkUtils.XWalkState() {
            @Override
            public void success() {
                loadXwalkWebView();
            }

            @Override
            public void fail() {
                ToastUtils.shortT("XWalkView 不兼容，已替换为系统自带 WebView");
            }

            @Override
            public void ignore() {
                ToastUtils.shortT("XWalkView 运行组件未下载，已替换为系统自带 WebView");
            }
        });
    }

    private void loadWebView() {
        mSysWebView = new MyWebView(mActivity);
        configWebViewSys(mSysWebView);
    }

    private void loadXwalkWebView() {
        mXwalkWebView = new MyXWalkView(mActivity);
        configWebViewX5(mXwalkWebView);
    }

    private void configWebViewSys(WebView webView) {
        if (webView == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = BuildConfig.DEBUG
                ? new ViewGroup.LayoutParams(800, 800) :
                new ViewGroup.LayoutParams(1, 1);
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);
        webView.clearFocus();
        webView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mActivity.addContentView(webView, layoutParams);
        /* 添加webView配置 */
        final WebSettings settings = webView.getSettings();
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
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        /* 添加webView配置 */
        //设置编码
        settings.setDefaultTextEncodingName("utf-8");
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//        settings.setUserAgentString(webView.getSettings().getUserAgentString());
        // settings.setUserAgentString(ANDROID_UA);

        webView.setWebChromeClient(new WebChromeClient() {
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
        mSysWebClient = new SysWebClient(mParser);
        webView.setWebViewClient(mSysWebClient);
        webView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void configWebViewX5(XWalkView webView) {
        if (webView == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = BuildConfig.DEBUG ? new ViewGroup.LayoutParams(800, 400) : new ViewGroup.LayoutParams(1, 1);
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);
        webView.clearFocus();
        webView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mActivity.addContentView(webView, layoutParams);
        /* 添加webView配置 */
        final XWalkSettings settings = webView.getSettings();
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        if (BuildConfig.DEBUG) {
            settings.setBlockNetworkImage(false);
        } else {
            settings.setBlockNetworkImage(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(false);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // settings.setUserAgentString(ANDROID_UA);

        webView.setBackgroundColor(Color.BLACK);
        webView.setUIClient(new XWalkUIClient(webView) {
            @Override
            public boolean onConsoleMessage(XWalkView view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
                return false;
            }

            @Override
            public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                return true;
            }

            @Override
            public boolean onJsConfirm(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                return true;
            }

            @Override
            public boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue, XWalkJavascriptResult result) {
                return true;
            }
        });
        mX5WebClient = new XWalkWebClient(webView);
        webView.setResourceClient(mX5WebClient);
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

    public static class MyXWalkView extends XWalkView {

        public MyXWalkView(Context context) {
            super(context);
        }

        public MyXWalkView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void setOverScrollMode(int overScrollMode) {
            super.setOverScrollMode(overScrollMode);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return false;
        }
    }

    private class SysWebClient extends WebViewClient {
        private Parser mParser;

        public SysWebClient(Parser parser) {
            mParser = parser;
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            String click=sourceBean.getClickSelector();
//            LOG.i("onPageFinished url:" + url);
//            if(!click.isEmpty()){
//                String selector;
//                if(click.contains(";")){
//                    if(!url.contains(click.split(";")[0]))return;
//                    selector=click.split(";")[1];
//                }else {
//                    selector=click.trim();
//                }
//                String js="$(\""+ selector+"\").click();";
//                mSysWebView.loadUrl("javascript:"+js);
//            }
        }

//        WebResourceResponse checkIsVideo(String url, HashMap<String, String> headers) {
//            if (url.endsWith("/favicon.ico")) {
//                if (url.startsWith("http://127.0.0.1")) {
//                    return new WebResourceResponse("image/x-icon", "UTF-8", null);
//                }
//                return null;
//            }
//
////            boolean isFilter = VideoParseRuler.isFilter(webUrl, url);
////            if (isFilter) {
////                LOG.i( "shouldInterceptLoadRequest filter:" + url);
////                return null;
////            }
////
////            boolean ad;
////            if (!loadedUrls.containsKey(url)) {
////                ad = AdBlocker.isAd(url);
////                loadedUrls.put(url, ad);
////            } else {
////                ad = loadedUrls.get(url);
////            }
////
////            if (!ad) {
////                if (checkVideoFormat(url)) {
////                    loadFoundVideoUrls.add(url);
////                    loadFoundVideoUrlsHeader.put(url, headers);
////                    LOG.i("loadFoundVideoUrl:" + url );
////                    if (loadFoundCount.incrementAndGet() == 1) {
////                        url = loadFoundVideoUrls.poll();
////                        mHandler.removeMessages(100);
////                        String cookie = CookieManager.getInstance().getCookie(url);
////                        if(!TextUtils.isEmpty(cookie))headers.put("Cookie", " " + cookie);//携带cookie
////                        playUrl(url, headers);
////                        stopLoadWebView(false);
////                    }
////                }
////            }
////
////            return ad || loadFoundCount.get() > 0 ?
////                    AdBlocker.createEmptyResource() :
////                    null;
//            return null;
//        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        @Nullable
        @Override
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            LogUtils.iTag(TAG, "shouldInterceptRequest url: " + url);
            if (mParser.isVideoUrl(url)) {
                Evtor.instance.subscribe("extracted").emit(url);
            }
//            HashMap<String, String> webHeaders = new HashMap<>();
//            Map<String, String> hds = request.getRequestHeaders();
//            if (hds != null && hds.keySet().size() > 0) {
//                for (String k : hds.keySet()) {
//                    if (k.equalsIgnoreCase("user-agent")
//                            || k.equalsIgnoreCase("referer")
//                            || k.equalsIgnoreCase("origin")) {
//                        webHeaders.put(k," " + hds.get(k));
//                    }
//                }
//            }
//            if (url.endsWith("/favicon.ico")) {
//                if (url.startsWith("http://127.0.0.1")) {
//                    return new WebResourceResponse("image/x-icon", "UTF-8", null);
//                }
//            }
            return super.shouldInterceptRequest(view, request);
//            return checkIsVideo(url, webHeaders);
        }

        @Override
        public void onLoadResource(WebView webView, String url) {
            super.onLoadResource(webView, url);
        }
    }

    private class XWalkWebClient extends XWalkResourceClient {
        public XWalkWebClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onDocumentLoadedInFrame(XWalkView view, long frameId) {
            super.onDocumentLoadedInFrame(view, frameId);
        }

        @Override
        public void onLoadStarted(XWalkView view, String url) {
            super.onLoadStarted(view, url);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
        }

        @Override
        public void onProgressChanged(XWalkView view, int progressInPercent) {
            super.onProgressChanged(view, progressInPercent);
        }

        @Override
        public XWalkWebResourceResponse shouldInterceptLoadRequest(XWalkView view, XWalkWebResourceRequest request) {
            String url = request.getUrl().toString();
            LogUtils.iTag(TAG, "shouldInterceptLoadRequest url: " + url);
            if (mParser.isVideoUrl(url)) {
                Evtor.instance.subscribe("extracted").emit(url);
            }
            // suppress favicon requests as we don't display them anywhere
//            if (url.endsWith("/favicon.ico")) {
//                if (url.startsWith("http://127.0.0.1")) {
//                    return createXWalkWebResourceResponse("image/x-icon", "UTF-8", null);
//                }
//            }

            return super.shouldInterceptLoadRequest(view, request);

//            boolean isFilter = VideoParseRuler.isFilter(webUrl, url);
//            if (isFilter) {
//                LogUtils.i("shouldInterceptLoadRequest filter:" + url);
//                return null;
//            }
//
//            boolean ad;
//            if (!loadedUrls.containsKey(url)) {
//                ad = AdBlocker.isAd(url);
//                loadedUrls.put(url, ad);
//            } else {
//                ad = loadedUrls.get(url);
//            }
//            if (!ad) {
//                if (checkVideoFormat(url)) {
//                    HashMap<String, String> webHeaders = new HashMap<>();
//                    Map<String, String> hds = request.getRequestHeaders();
//                    if (hds != null && hds.keySet().size() > 0) {
//                        for (String k : hds.keySet()) {
//                            if (k.equalsIgnoreCase("user-agent")
//                                    || k.equalsIgnoreCase("referer")
//                                    || k.equalsIgnoreCase("origin")) {
//                                webHeaders.put(k, " " + hds.get(k));
//                            }
//                        }
//                    }
//                    loadFoundVideoUrls.add(url);
//                    loadFoundVideoUrlsHeader.put(url, webHeaders);
//                    LogUtils.i("loadFoundVideoUrl:" + url);
//                    if (loadFoundCount.incrementAndGet() == 1) {
//                        mHandler.removeMessages(100);
//                        url = loadFoundVideoUrls.poll();
//                        String cookie = CookieManager.getInstance().getCookie(url);
//                        if (!TextUtils.isEmpty(cookie)) webHeaders.put("Cookie", " " + cookie);//携带cookie
//                        playUrl(url, webHeaders);
//                        stopLoadWebView(false);
//                    }
//                }
//            }
//            return ad || loadFoundCount.get() > 0 ?
//                    createXWalkWebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes())) :
//                    null;
        }

        @Override
        public boolean shouldOverrideUrlLoading(XWalkView view, String s) {
            return false;
        }

        @Override
        public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
            callback.onReceiveValue(true);
        }
    }

//    @SuppressLint("SetJavaScriptEnabled")
//    protected void loadAsync(RelativeLayout rlWvContainer, String url) {
//        WebView wv = new WebView(rlWvContainer.getContext());
//        wv.setSoundEffectsEnabled(true);
//        WebSettings settings = wv.getSettings();
//
//
////        mWv = new ParserWebView(rlWvContainer.getContext());
////        mWv.setSoundEffectsEnabled(true);
////        WebSettings settings = mWv.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setDomStorageEnabled(true);
//        settings.setAllowFileAccess(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//
//        settings.setSupportMultipleWindows(true);// 新加//我就是没有这一行，死活不出来。MD，硬是没有人写这一句！
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
////        }
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
////            settings.setMediaPlaybackRequiresUserGesture(true);
////        }
////        if (Build.VERSION.SDK_INT >= 16) {
////            settings.setAllowFileAccessFromFileURLs(true);
////            settings.setAllowUniversalAccessFromFileURLs(true);
////        }
////        settings.setJavaScriptCanOpenWindowsAutomatically(true);
////        settings.setLoadsImagesAutomatically(true);
////        settings.setDatabaseEnabled(true);
////        settings.setGeolocationEnabled(true);
//        settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
////        CookieManager instance = CookieManager.getInstance();
////        if (Build.VERSION.SDK_INT < 21) {
////            CookieSyncManager.createInstance(rlWvContainer.getContext());
////        }
////        instance.setAcceptCookie(true);
////        if (Build.VERSION.SDK_INT >= 21) {
////            instance.setAcceptThirdPartyCookies(mWv, true);
////        }
//
//        wv.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onReceivedSslError(WebView webView, SslErrorHandler handler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
//                handler.proceed();
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
//                String method = request.getMethod();
//                Uri uri = request.getUrl();
//                String url = uri.toString();
//                Map<String, String> headers = request.getRequestHeaders();
//                String lowerUrl = url.toLowerCase();
//
//                if ("GET".equals(method.toUpperCase(Locale.getDefault())) && (url.startsWith("http://") || url.startsWith("https://")) && (lowerUrl.contains(".m3u8?") || lowerUrl.contains(".avi") ||
//                        lowerUrl.contains(".mov") || lowerUrl.contains(".mkv") || lowerUrl.contains(".flv") ||
//                        lowerUrl.contains(".f4v") || lowerUrl.contains(".rmvb"))) {
//                    LogUtils.iTag(TAG, url);
//                    ParserApi.instance.parse(url, headers, ok -> {
//                        if (ok) {
//                            // m3u8 地址提取成功
//                            Evtor.instance.subscribe("extracted").emit(url);
//                        }
//                    });
//                    return true;
//                }
//                return super.shouldOverrideUrlLoading(webView, request);
//            }
//        });
//
//        rlWvContainer.removeAllViews();
//        rlWvContainer.addView(wv);
////
//        Prs prs = prs();
//        LogUtils.iTag(TAG, "解析器：" + prs.getName());
//        // url 拼接
//        url = prs().getUrl() + url;
//
//        // 加载
//        wv.loadUrl(url);
//    }
}
