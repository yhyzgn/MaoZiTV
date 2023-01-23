package com.yhy.mz.tv.ui;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.ItemBridgeAdapter;

import com.yhy.mz.tv.R;
import com.yhy.mz.tv.component.base.BaseActivity;
import com.yhy.mz.tv.component.presenter.ItemScvPresenter;
import com.yhy.mz.tv.utils.ViewUtils;
import com.yhy.mz.tv.widget.TabHorizontalGridView;
import com.yhy.router.annotation.Router;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 主页
 * <p>
 * Created on 2023-01-20 00:59
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Router(url = "/activity/main")
public class MainActivity extends BaseActivity {
    private static final String[] TOP_TOP = new String[]{"搜索", "历史", "收藏", "设置"};

    private TabHorizontalGridView hgTop;
    private ArrayObjectAdapter mHgTopAdapter;

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        hgTop = $(R.id.hg_top);

        hgTop.setHorizontalSpacing(ViewUtils.dp2px(8));
        mHgTopAdapter = new ArrayObjectAdapter(new ItemScvPresenter());
        ItemBridgeAdapter tempAdapter = new ItemBridgeAdapter(mHgTopAdapter);
        hgTop.setAdapter(tempAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(tempAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
    }

    @Override
    protected void initData() {
//        List<String> titles = SourceCenter.instance.titles();
        mHgTopAdapter.addAll(0, Arrays.stream(TOP_TOP).collect(Collectors.toList()));
    }

    @Override
    protected void initEvent() {
    }
}
