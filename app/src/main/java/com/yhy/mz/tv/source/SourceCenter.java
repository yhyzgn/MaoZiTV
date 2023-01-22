package com.yhy.mz.tv.source;

import com.yhy.mz.tv.source.internal.IQiYi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 源中心
 * <p>
 * Created on 2023-01-20 02:37
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class SourceCenter {
    public static final SourceCenter instance = new SourceCenter();

    private final List<Source> mSourceList = new ArrayList<>();

    private SourceCenter() {
    }

    public void init() {
        mSourceList.add(IQiYi.init());
    }

    public List<String> titles() {
        return mSourceList.stream().map(Source::name).collect(Collectors.toList());
    }
}
