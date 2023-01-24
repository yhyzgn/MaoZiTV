package com.yhy.mz.tv.widget.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Js 提取的 WebView
 * <p>
 * Created on 2023-01-25 02:32
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class JsExtraWebView extends WebView {
    private String mExpression;
    private OnExtraListener mOnExtraListener;

    public JsExtraWebView(@NonNull Context context) {
        this(context, null);
    }

    public JsExtraWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JsExtraWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public JsExtraWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new JsBridge(), "jsExtra");
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadUrl("javascript:window.jsExtra.onSuccess(" + mExpression + ")");
            }
        });
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = 1;
        lp.height = 1;
        MarginLayoutParams mlp = new MarginLayoutParams(lp);
        mlp.leftMargin = -2;
        mlp.topMargin = -2;
        setLayoutParams(mlp);
    }

    public void setJsExpression(String expression) {
        mExpression = expression;
    }

    public void setOnExtraListener(OnExtraListener listener) {
        mOnExtraListener = listener;
    }

    public interface OnExtraListener {

        void onExtra(String value);
    }

    public class JsBridge {

        @JavascriptInterface
        public void onSuccess(String value) {
            if (null != mOnExtraListener) {
                mOnExtraListener.onExtra(value);
            }
        }
    }
}
