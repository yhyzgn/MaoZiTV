package com.yhy.mz.tv.channel.some;

import com.yhy.mz.tv.channel.Channel;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.model.ems.VideoType;

import java.util.List;

/**
 * 推荐频道
 * <p>
 * Created on 2023-01-23 23:23
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class RecommendChannel implements Channel {
    @Override
    public Chan chan() {
        return Chan.RECOMMEND;
    }

    @Override
    public List<Video> page(int page) {
        return null;
    }
}
