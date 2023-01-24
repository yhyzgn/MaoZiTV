package com.yhy.mz.tv.source;

import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.VideoType;

import java.util.List;

/**
 * 源
 * <p>
 * Created on 2023-01-20 02:40
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Source {

    /**
     * 源名称
     *
     * @return 原名称
     */
    String name();

    /**
     * 加载推荐页面数据
     *
     * @param type 视频类型
     * @param page 页码，从 1 开始
     * @return 视频信息
     */
    List<Video> page(VideoType type, int page);

    /**
     * 搜索
     *
     * @param keyword 关键字
     * @return 视频信息
     */
    List<Video> search(String keyword);
}
