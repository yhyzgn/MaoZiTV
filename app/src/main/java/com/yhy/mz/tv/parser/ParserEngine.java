package com.yhy.mz.tv.parser;

import androidx.appcompat.app.AppCompatActivity;

import com.yhy.evtor.Evtor;
import com.yhy.evtor.annotation.Subscribe;
import com.yhy.mz.tv.parser.of.OkParser;
import com.yhy.mz.tv.parser.of.PanGuParser;
import com.yhy.mz.tv.parser.of.YeMuParser;
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
        mParserList.add(new YeMuParser());
        mParserList.add(new PanGuParser());
        mParserList.add(new OkParser());

        Evtor.instance.register(this);
    }

    public void init() {
    }

    public void process(AppCompatActivity activity, String url) {
        process(activity, url, 0);
    }

    public void process(AppCompatActivity activity, String url, int index) {
        if (index > mParserList.size() - 1) {
            throw new IllegalArgumentException("解析器下标越界");
        }
        Parser parser = mParserList.get(index);
        if (null != parser) {
            parser.process(activity, parser.prs().getUrl() + url);
            mIndexUrlMap.put(url, index);
        }
    }

    @Subscribe("onWebViewParseSuccess")
    public void onWebViewParseSuccess(String url) {
        LogUtils.iTag(TAG, url, "已成功解析");
        mIndexUrlMap.remove(url);
    }

    @Subscribe("onWebViewParseError")
    public void onWebViewParseError(AppCompatActivity activity, String url) {
        LogUtils.eTag(TAG, url, "解析失败了，轮到下一个解析器...");
        Integer index = mIndexUrlMap.get(url);
        if (null == index) {
            index = -1;
        }
        index++;
        process(activity, url, index);
    }
}
