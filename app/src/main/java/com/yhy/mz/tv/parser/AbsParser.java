package com.yhy.mz.tv.parser;

import androidx.appcompat.app.AppCompatActivity;

import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.router.EasyRouter;

/**
 * https://github.com/takagen99/Box/blob/main/app/src/main/java/com/github/tvbox/osc/ui/activity/PlayActivity.java
 * <p>
 * Created on 2023-01-29 21:48
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbsParser implements Parser {

    private static final String TAG = "AbsParser";

    protected void loadWebView(AppCompatActivity activity, String url, Parser parser) {
        LogUtils.iTag(TAG, "url = " + url);

        EasyRouter.getInstance()
                .with(activity)
                .to("/activity/detective")
                .param("mUrl", url)
                .param("mPrsCode", parser.prs().getCode())
                .go();
    }
}
