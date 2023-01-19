package com.yhy.thank.tv.component.base;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        beforeLayout();
        setContentView(layout());
    }

    protected void beforeLayout(){}

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
