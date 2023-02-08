package com.yhy.mz.tv.parser.of;

import com.google.android.exoplayer2.util.MimeTypes;
import com.yhy.mz.tv.internal.Lists;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.AbsParser;

import java.util.List;

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
    @Override
    public Prs prs() {
        return Prs.PAN_GU;
    }

    @Override
    public List<Chan> supportedChanList() {
        return Lists.of(Chan.YI_FILM, Chan.YI_EPISODE);
    }

    @Override
    public String mimeType(Chan chan) {
        return MimeTypes.APPLICATION_M3U8;
    }

    @Override
    public boolean isVideoUrl(String url) {
        return url.contains("/key.php?token=") && url.endsWith("=.m3u8");
    }
}
