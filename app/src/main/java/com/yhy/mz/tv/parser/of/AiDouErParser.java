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
public class AiDouErParser extends AbsParser {
    @Override
    public Prs prs() {
        return Prs.AI_DOU_ER;
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
        // https://jx.aidouer.net/playurl/ts/43756c52596e503742766e4c4d7736625253634747773d3d.m3u8?vid=85d9D_Q8fUwvKYaMhmjm3PpC2lLr_53yFLhnV9hxReDauWak5GtxXaxl-MYbubRcgP33-dZfPX1PQhgqquKo940s4q0Vdukh_p-SpmOOkqAWuTls4_I&type=m3u8&client_netip=116.249.125.231&sid=e897ef33527325ae9abb54118ea1d21d&app_ver=VERSION&url=https%3A%2F%2Fwww.iqiyi.com%2Fv_g4yclmg2ug.html&media_type=ts&ts=475045594553464d663659414a706c45795972735a673d3d&sign=bUxrTExKcWdaZ2UzQkZnOER2b010NWFCS01uQWVHYmhTVDZDby9ueFFLNlpDRmZKUjZ2VlJ6aHh2TExCVlorZm5wWVV0bENoMTh0OU5rQ09ZaTVVWklXMTZVQmJpYkVqUWNNTVBMNzFDbGR1bGUrWWt2OVdUUTZ0WGp0bmdUMHk3RExhSWFFY2ZOYlU4Q3RDVjhnMTNVaEpkdjBxaVQwOTQvakZzOU5Kd0NueHA5aDhXeWVWQUZLT2NGRWpzc2d5KzJIV1pFOEd3VkttS1FyQWxRQUpDbS8vM0NZeXB2dDB3Nkw2dmhUcVhHelJIbjV2M2o1d3I2MXdVeDdTVHBvMXFTcEF3N3hLaTA0ejhCV3kwbWlFa0Q4OFdSWDVJblYxZGR6bEtUWWZ2S1dvR2hsN3hDVkpwYU1yQ1ZtUnJkL2tibmFISDNLdmlyRUV1N1FCclpYYUYvMTcrc2lmV2I2Q3cwSUhING1OREthZU5iVDM1N0l2TEtCL1pWbGROZEc4VVptcWFkTkc5V3VjcVFHYi9VNktHaEVnZ2JCNmh2a21lQWd4WStNMnkzRDloUHRuYTdFdWpPUUt4N1EzTFdHNA==
        return url.contains(".m3u8?vid=");
    }
}
