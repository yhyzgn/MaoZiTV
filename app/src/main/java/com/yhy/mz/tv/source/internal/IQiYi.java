package com.yhy.mz.tv.source.internal;

import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.VideoType;
import com.yhy.mz.tv.source.Source;

import java.util.List;

/**
 * 爱奇艺
 * <p>
 * Created on 2023-01-20 03:21
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class IQiYi implements Source {
    @Override
    public String name() {
        return "爱奇艺";
    }

    @Override
    public List<Video> page(VideoType type, int page) {
        return null;
    }

    @Override
    public List<Video> search(String keyword) {
        return null;
    }

    public static IQiYi init() {
        return new IQiYi();
    }
}
