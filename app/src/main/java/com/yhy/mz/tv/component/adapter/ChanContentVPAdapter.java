package com.yhy.mz.tv.component.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yhy.mz.tv.channel.ChannelManager;
import com.yhy.mz.tv.model.ems.Chan;

import java.util.List;

/**
 * 频道页面适配器
 * <p>
 * Created on 2023-01-24 00:40
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class ChanContentVPAdapter extends SmartFragmentStatePagerAdapter {
    private static final String TAG = "ContentViewPagerAdapter";

    private final List<Chan> chanList;

    public ChanContentVPAdapter(@NonNull FragmentManager fm) {
        super(fm);
        chanList = ChannelManager.instance.getChanList();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Chan chan = chanList.get(position);
        return ChannelManager.instance.getChannel(chan).fragment(position);
    }

    @Override
    public int getCount() {
        return chanList == null ? 0 : chanList.size();
    }
}
