package com.yhy.mz.tv.widget.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yhy.evtor.Evtor;
import com.yhy.mz.tv.api.of.parser.ParserApi;
import com.yhy.mz.tv.utils.LogUtils;

import java.util.Locale;
import java.util.Map;

/**
 * 视频解析提取器
 * <p>
 * Created on 2023-01-29 21:20
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParserWebView extends WebView {

    private static final String TAG = "ParserWebView";

    public ParserWebView(@NonNull Context context) {
        this(context, null);
    }

    public ParserWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParserWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ParserWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWebViewClient(new WebViewClient() {
            @SuppressLint("WebViewClientOnReceivedSslError")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
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
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                Evtor.instance.subscribe("finished").emit(url);
            }
        });
    }
}
