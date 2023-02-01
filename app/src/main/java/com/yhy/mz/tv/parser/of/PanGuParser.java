package com.yhy.mz.tv.parser.of;

import android.widget.RelativeLayout;

import com.yhy.mz.tv.model.ems.Prs;
import com.yhy.mz.tv.parser.AbsParser;

/**
 * 盘古解析
 * <p>
 * Created on 2023-01-29 21:39
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class PanGuParser extends AbsParser {
    private static final String TAG = "PanGuParser";

    @Override
    public Prs prs() {
        return Prs.PAN_GU;
    }

    @Override
    public void process(RelativeLayout rlWvContainer, String url) {
        loadAsync(rlWvContainer, url);
    }
}
