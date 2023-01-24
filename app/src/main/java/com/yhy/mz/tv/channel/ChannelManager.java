package com.yhy.mz.tv.channel;

import com.yhy.mz.tv.channel.some.KuEpisodeChannel;
import com.yhy.mz.tv.channel.some.KuFilmChannel;
import com.yhy.mz.tv.channel.some.RecommendChannel;
import com.yhy.mz.tv.channel.some.XunEpisodeChannel;
import com.yhy.mz.tv.channel.some.XunFilmChannel;
import com.yhy.mz.tv.channel.some.YiEpisodeChannel;
import com.yhy.mz.tv.channel.some.YiFilmChannel;
import com.yhy.mz.tv.model.ems.Chan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 频道管理中心
 * <p>
 * Created on 2023-01-24 00:15
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class ChannelManager {
    public final static ChannelManager instance = new ChannelManager();

    private final List<Channel> channelList = new ArrayList<>();
    private final Map<Chan, Channel> channelMap = new HashMap<>();

    private ChannelManager() {
        channelList.add(new RecommendChannel());
        channelList.add(new YiFilmChannel());
        channelList.add(new YiEpisodeChannel());
        channelList.add(new XunFilmChannel());
        channelList.add(new XunEpisodeChannel());
        channelList.add(new KuFilmChannel());
        channelList.add(new KuEpisodeChannel());

        channelMap.putAll(channelList.stream().collect(Collectors.toMap(Channel::chan, v -> v)));
    }

    public List<Chan> getChanList() {
        return channelList.stream().map(Channel::chan).collect(Collectors.toList());
    }

    public Channel getChannel(Chan chan) {
        return channelMap.get(chan);
    }
}
