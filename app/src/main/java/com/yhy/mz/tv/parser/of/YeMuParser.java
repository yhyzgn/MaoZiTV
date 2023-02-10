package com.yhy.mz.tv.parser.of;

import com.google.android.exoplayer2.util.MimeTypes;
import com.yhy.mz.tv.internal.Lists;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.AbsParser;

import java.util.List;

/**
 * Created on 2023-02-10 10:53
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class YeMuParser extends AbsParser {
    @Override
    public Prs prs() {
        return Prs.YE_MU;
    }

    @Override
    public List<Chan> supportedChanList() {
        return Lists.of(Chan.YI_FILM, Chan.YI_EPISODE, Chan.XUN_FILM, Chan.XUN_EPISODE);
    }

    @Override
    public String mimeType(Chan chan) {
        return MimeTypes.APPLICATION_M3U8;
    }

    @Override
    public boolean isVideoUrl(String url) {
        // https://110.42.2.115:9092/c/m3u8_301/d14977a5a27cbea7ce0d6dd3f6d917c9.m3u8?vkey=a0bbS1KqJPXR4z0pL9RyFJiQORD7bLEq2GQLOwrQ
        return url.contains(".m3u8?vkey=");
    }
}
