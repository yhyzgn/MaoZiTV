package com.yhy.mz.tv.parser;

import android.widget.RelativeLayout;

import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;
import com.yhy.mz.tv.parser.of.PanGuParser;
import com.yhy.mz.tv.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析引擎
 * <p>
 * Created on 2023-01-29 21:34
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParserEngine {
    private static final String TAG = "ParserEngine";
    public static final ParserEngine instance = new ParserEngine();

    private final List<Parser> mParserList = new ArrayList<>();
    private final Map<String, Integer> mIndexUrlMap = new HashMap<>();

    private ParserEngine() {
        mParserList.add(new PanGuParser());

        Evtor.instance.register(this);
    }

    public void init() {
    }

    public void process(RelativeLayout rlWvContainer, String url) {
        process(rlWvContainer, url, 0);
    }

    private void process(RelativeLayout rlWvContainer, String url, int index) {
        if (index > mParserList.size() - 1) {
            throw new IllegalArgumentException("解析器下标越界");
        }
        Parser parser = mParserList.get(index);
        if (null != parser) {
            parser.process(rlWvContainer, url);
            mIndexUrlMap.put(url, index);
        }
    }

    @Subscribe("onWebViewParseSuccess")
    public void onWebViewParseSuccess(String url) {
        LogUtils.iTag(TAG, url, "已成功解析");
        mIndexUrlMap.remove(url);
    }

    @Subscribe("onWebViewParseError")
    public void onWebViewParseError(RelativeLayout rlWvContainer, String url) {
        LogUtils.eTag(TAG, url, "解析失败了，轮到下一个解析器...");
        Integer index = mIndexUrlMap.get(url);
        if (null == index) {
            index = -1;
        }
        index++;
        process(rlWvContainer, url, index);
    }
}
