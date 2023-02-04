package com.yhy.mz.tv.parser;

import androidx.appcompat.app.AppCompatActivity;

import com.yhy.evtor.Evtor;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.of.OkParser;
import com.yhy.mz.tv.parser.of.PanGuParser;
import com.yhy.mz.tv.parser.of.YeMuParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private final Map<Chan, Integer> mChanParserIndexMap = new HashMap<>();

    private ParserEngine() {
        mParserList.add(new YeMuParser());
        mParserList.add(new PanGuParser());
        mParserList.add(new OkParser());

        Evtor.instance.register(this);
    }

    private Parser nextParser(Chan chan) {
        Integer start = mChanParserIndexMap.getOrDefault(chan, -1);
        if (null == start || start >= mParserList.size() - 1) {
            start = -1;
        }
        start++;
        for (int i = start; i < mParserList.size(); i++) {
            Parser parser = mParserList.get(i);
            if (parser.supportedChanList().contains(chan)) {
                mChanParserIndexMap.put(chan, i);
                return parser;
            }
        }
        return mParserList.get(start);
    }

    private Parser currentParser(Chan chan) {
//        if (!mChanParserIndexMap.containsKey(chan)) {
//            return nextParser(chan);
//        }
//        Integer index = mChanParserIndexMap.getOrDefault(chan, 0);
//        if (null == index || index > mParserList.size() - 1) {
//            index = 0;
//        }
        return mParserList.get(0);
    }

    public Parser getByPrs(Prs prs) {
        return mParserList.stream().filter(it -> Objects.equals(it.prs(), prs)).findFirst().orElse(mParserList.get(0));
    }

    public void process(AppCompatActivity activity, Chan chan, String url) {
        Parser parser = currentParser(chan);
        if (null != parser) {
            parser.process(activity, parser.prs().getUrl() + url);
        }
    }

    public String mimeType(Chan chan) {
        return currentParser(chan).mimeType(chan);
    }
}
