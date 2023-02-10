package com.yhy.mz.tv.parser;

import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.parser.of.JsonPlayerParser;
import com.yhy.mz.tv.parser.of.M3U8TvParser;
import com.yhy.mz.tv.parser.of.OkParser;
import com.yhy.mz.tv.parser.of.PanGuParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public static final ParserEngine instance = new ParserEngine();

    private final List<Parser> mParserList = new ArrayList<>();

    private ParserEngine() {
        mParserList.add(new JsonPlayerParser());
        mParserList.add(new OkParser());
        mParserList.add(new PanGuParser());
        mParserList.add(new M3U8TvParser());
    }

    public List<Parser> getParserList(Chan chan) {
        return mParserList.stream().filter(it -> it.supportedChanList().contains(chan)).collect(Collectors.toList());
    }
}
