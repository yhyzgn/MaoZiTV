package com.yhy.mz.tv.channel.some;

import com.yhy.mz.tv.api.of.chan.xun.XunApi;
import com.yhy.mz.tv.channel.Channel;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.model.ems.VideoType;

import java.util.List;

/**
 * 江湖艺
 * <p>
 * Created on 2023-01-23 23:41
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class XunFilmChannel implements Channel {
    @Override
    public Chan chan() {
        return Chan.XUN_FILM;
    }

    @Override
    public List<Video> page(int page) {
        try {
            return XunApi.instance.page(page, VideoType.FILM, 11);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
