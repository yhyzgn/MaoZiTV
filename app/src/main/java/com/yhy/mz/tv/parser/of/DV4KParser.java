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
public class DV4KParser extends AbsParser {
    @Override
    public Prs prs() {
        return Prs.DV4K;
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
        // https://cdn.sm.cn/1b9393ba91e5cc2dd348cd6939c142a1/feedback/e2df24c138.m3u8
        return url.contains("cdn.sm.cn") && url.endsWith(".m3u8");
    }
}
