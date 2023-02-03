package com.yhy.mz.tv.parser.of;

import androidx.appcompat.app.AppCompatActivity;

import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.AbsParser;

/**
 * 盘古解析
 * <p>
 * Created on 2023-01-29 21:39
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class PanGuParser extends AbsParser {
    private static final String TAG = "PanGuParser";

    @Override
    public Prs prs() {
        return Prs.PAN_GU;
    }

    @Override
    public boolean isVideoUrl(String url) {
        return url.contains("/PlayVideo.php?url=") && url.endsWith("=.m3u8");
    }

    @Override
    public void process(AppCompatActivity activity, String url) {
        loadWebView(activity, url, this);
    }
}