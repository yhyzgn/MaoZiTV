package com.yhy.mz.tv.parser;

import androidx.appcompat.app.AppCompatActivity;

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
     * 判断一个 url 是否是视频地址
     *
     * @param url 待判定的地址
     * @return 是否是视频
     */
    boolean isVideoUrl(String url);

    /**
     * 解析一个地址
     *
     * @param activity 当前 Activity
     * @param url      视频地址
     */
    void process(AppCompatActivity activity, String url);
}
