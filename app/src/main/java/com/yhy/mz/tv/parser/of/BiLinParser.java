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
public class BiLinParser extends AbsParser {
    @Override
    public Prs prs() {
        return Prs.BI_LIN;
    }

    @Override
    public List<Chan> supportedChanList() {
        return Lists.of(Chan.YI_FILM, Chan.YI_EPISODE, Chan.XUN_FILM, Chan.XUN_EPISODE, Chan.KU_FILM, Chan.KU_EPISODE);
    }

    @Override
    public String mimeType(Chan chan) {
        return MimeTypes.APPLICATION_M3U8;
    }

    @Override
    public boolean isVideoUrl(String url) {
        // https://vip.ffzy-play5.com/20230205/6644_48b726fa/index.m3u8
        return url.endsWith(".m3u8");
    }
}
