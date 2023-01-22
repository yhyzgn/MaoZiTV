package com.yhy.mz.tv.component.base;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yhy.mz.tv.App;

/**
 * Activity 基类
 * <p>
 * Created on 2023-01-19 16:38
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class BaseActivity extends AppCompatActivity {

    public App mApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mApp = (App) getApplication();

        beforeLayout();
        setContentView(layout());

        initView();
        initData();
        initEvent();
    }

    protected void beforeLayout() {
    }

    @LayoutRes
    protected abstract int layout();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initEvent();

    public <T extends View> T $(@IdRes int id) {
        return findViewById(id);
    }

    public void success(String text) {
    }

    public void warning(String text) {
    }

    public void error(String text) {
    }
}
