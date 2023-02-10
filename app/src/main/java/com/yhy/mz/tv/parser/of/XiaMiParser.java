package com.yhy.mz.tv.parser.of;

import com.google.android.exoplayer2.util.MimeTypes;
import com.yhy.mz.tv.internal.Lists;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.AbsParser;

import java.util.List;

/**
 * Created on 2023-02-10 14:38
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class XiaMiParser extends AbsParser {
    @Override
    public Prs prs() {
        return Prs.XIA_MI;
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
        // https://json.xmflv.cc:4433/Cache/qiyi/75c7f676ca5b573e3be83882b7395edf.m3u8?vkey=1882AQgIBAIBAFJRCVVWVlABAFQBB1ALC1QAVA8AV1NUUgAADAoD
         return url.contains(".m3u8?vkey=");
    }
}
