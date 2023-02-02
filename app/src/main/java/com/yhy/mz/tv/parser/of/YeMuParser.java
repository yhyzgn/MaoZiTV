package com.yhy.mz.tv.parser.of;

import androidx.appcompat.app.AppCompatActivity;

import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.AbsParser;

/**
 * Created on 2023-02-02 22:55
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class YeMuParser extends AbsParser {
    @Override
    public Prs prs() {
        return Prs.M3U8_TV;
    }

    @Override
    public boolean isVideoUrl(String url) {
        // https://storage.360buyimg.com/satisfaction/b10c1ac1-bbc2-4ea6-a529-c19a4430e022.jpg?Expires=1675424024&AccessKey=j6SFUpPYU982lF3x&Signature=gqJUtGcrgNqlwMoVBwafkAhgPCQ%3D
        // https://110.42.2.115:9092/c/ali_301/3becc22bebba6503e3324cc1a38029d7.m3u8?vkey=c9c4rkmyDfph2eh4bGr2PUvRvICvILkqbXsy3MYT
        return url.contains("zh188.net") && url.contains("=.m3u8");
    }

    @Override
    public void process(AppCompatActivity activity, String url) {
        loadWebView(activity, url, this);
    }
}
