package com.yhy.mz.tv.channel.some;

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
public class XunEpisodeChannel implements Channel {
    @Override
    public Chan chan() {
        return Chan.XUN_EPISODE;
    }

    @Override
    public List<Video> load(VideoType type, int page) {
        return null;
    }
}
