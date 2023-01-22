package com.yhy.mz.tv.ui;

import android.annotation.SuppressLint;

import com.github.ybq.android.spinkit.SpinKitView;
import com.yhy.router.EasyRouter;
import com.yhy.router.annotation.Router;
import com.yhy.mz.tv.R;
import com.yhy.mz.tv.component.base.BaseActivity;
import com.yhy.mz.tv.component.router.FinishRouterCallback;
import com.yhy.mz.tv.source.SourceCenter;

/**
 * 闪屏页
 * <p>
 * Created on 2023-01-19 16:38
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressLint("CustomSplashScreen")
@Router(url = "/activity/splash")
public class SplashActivity extends BaseActivity {

    private SpinKitView skvLoading;

    @Override
    protected int layout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        skvLoading = $(R.id.skv_loading);
    }

    @Override
    protected void initData() {
        // 源中心初始化
        SourceCenter.instance.init();

        // 进入主页
        skvLoading.postDelayed(() -> {
            EasyRouter.getInstance()
                    .with(this)
                    .to("/activity/main")
                    .go(FinishRouterCallback.get(this));
        }, 1200);
    }

    @Override
    protected void initEvent() {
    }
}
