package com.yhy.mz.tv.channel;

import com.yhy.mz.tv.component.base.BaseLazyLoadFragment;
import com.yhy.mz.tv.component.fragment.VpMainFragment;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.model.ems.VideoType;

import java.util.List;

/**
 * 频道接口
 * <p>
 * Created on 2023-01-23 23:17
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Channel {

    /**
     * 频道
     *
     * @return 频道
     */
    Chan chan();

    /**
     * 加载数据
     *
     * @param type 视频类型
     * @param page 分页页码
     * @return 影视数据
     */
    List<Video> load(VideoType type, int page);

    /**
     * 创建页面
     *
     * @param position 下标
     * @return 页面实例
     */
    default BaseLazyLoadFragment fragment(int position) {
        return VpMainFragment.newInstance(position, chan());
    }
}
