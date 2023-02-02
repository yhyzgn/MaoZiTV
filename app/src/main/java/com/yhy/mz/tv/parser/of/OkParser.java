package com.yhy.mz.tv.parser.of;

import androidx.appcompat.app.AppCompatActivity;

import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.AbsParser;

/**
 * Created on 2023-02-02 21:45
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class OkParser extends AbsParser {
    @Override
    public Prs prs() {
        return Prs.OK;
    }

    @Override
    public boolean isVideoUrl(String url) {
        return url.startsWith("https://api.m3u8.pw/Cache") && url.contains(".m3u8?vkey=");
    }

    @Override
    public void process(AppCompatActivity activity, String url) {
        loadWebView(activity, url, this);
    }
}
