package com.yhy.mz.tv.parser;

import android.widget.RelativeLayout;

import com.yhy.mz.tv.model.ems.Prs;

/**
 * 解析器
 * <p>
 * Created on 2023-01-29 21:38
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Parser {

    /**
     * 当前解析器
     *
     * @return 解析器
     */
    Prs prs();

    /**
     * 解析一个地址
     *
     * @param rlWvContainer WebView 容器
     * @param url           视频地址
     */
    void process(RelativeLayout rlWvContainer, String url);
}
