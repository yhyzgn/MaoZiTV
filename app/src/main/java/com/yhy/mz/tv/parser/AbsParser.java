package com.yhy.mz.tv.parser;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.widget.RelativeLayout;

import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yhy.evtor.Evtor;
import com.yhy.mz.tv.api.of.parser.ParserApi;
import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.utils.LogUtils;

import java.util.Locale;
import java.util.Map;

/**
 * https://github.com/takagen99/Box/blob/main/app/src/main/java/com/github/tvbox/osc/ui/activity/PlayActivity.java
 *
 * Created on 2023-01-29 21:48
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbsParser implements Parser {

    private static final String TAG = "AbsParser";
//    protected ParserWebView mWv;

    @SuppressLint("SetJavaScriptEnabled")
    protected void loadAsync(RelativeLayout rlWvContainer, String url) {
        WebView wv = new WebView(rlWvContainer.getContext());
        wv.setSoundEffectsEnabled(true);
        WebSettings settings = wv.getSettings();


//        mWv = new ParserWebView(rlWvContainer.getContext());
//        mWv.setSoundEffectsEnabled(true);
//        WebSettings settings = mWv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        settings.setSupportMultipleWindows(true);// 新加//我就是没有这一行，死活不出来。MD，硬是没有人写这一句！
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            settings.setMediaPlaybackRequiresUserGesture(true);
//        }
//        if (Build.VERSION.SDK_INT >= 16) {
//            settings.setAllowFileAccessFromFileURLs(true);
//            settings.setAllowUniversalAccessFromFileURLs(true);
//        }
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setLoadsImagesAutomatically(true);
//        settings.setDatabaseEnabled(true);
//        settings.setGeolocationEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
//        CookieManager instance = CookieManager.getInstance();
//        if (Build.VERSION.SDK_INT < 21) {
//            CookieSyncManager.createInstance(rlWvContainer.getContext());
//        }
//        instance.setAcceptCookie(true);
//        if (Build.VERSION.SDK_INT >= 21) {
//            instance.setAcceptThirdPartyCookies(mWv, true);
//        }

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler handler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                String method = request.getMethod();
                Uri uri = request.getUrl();
                String url = uri.toString();
                Map<String, String> headers = request.getRequestHeaders();
                String lowerUrl = url.toLowerCase();

                if ("GET".equals(method.toUpperCase(Locale.getDefault())) && (url.startsWith("http://") || url.startsWith("https://")) && (lowerUrl.contains(".m3u8?") || lowerUrl.contains(".avi") ||
                        lowerUrl.contains(".mov") || lowerUrl.contains(".mkv") || lowerUrl.contains(".flv") ||
                        lowerUrl.contains(".f4v") || lowerUrl.contains(".rmvb"))) {
                    LogUtils.iTag(TAG, url);
                    ParserApi.instance.parse(url, headers, ok -> {
                        if (ok) {
                            // m3u8 地址提取成功
                            Evtor.instance.subscribe("extracted").emit(url);
                        }
                    });
                    return true;
                }
                return super.shouldOverrideUrlLoading(webView, request);
            }
        });

        rlWvContainer.removeAllViews();
        rlWvContainer.addView(wv);
//
        Prs prs = prs();
        LogUtils.iTag(TAG, "解析器：" + prs.getName());
        // url 拼接
        url = prs().getUrl() + url;

        // 加载
        wv.loadUrl(url);
    }
}
