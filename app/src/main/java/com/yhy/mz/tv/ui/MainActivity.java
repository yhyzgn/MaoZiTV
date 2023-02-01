package com.yhy.mz.tv.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.Group;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.azhon.appupdate.manager.DownloadManager;
import com.yhy.mz.tv.R;
import com.yhy.mz.tv.api.of.fir.FirApi;
import com.yhy.mz.tv.api.model.FirVersionInfo;
import com.yhy.mz.tv.channel.ChannelManager;
import com.yhy.mz.tv.component.adapter.ChanContentVPAdapter;
import com.yhy.mz.tv.component.base.BaseActivity;
import com.yhy.mz.tv.component.presenter.TabChanPresenter;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.utils.FileUtils;
import com.yhy.mz.tv.utils.JsonUtils;
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.mz.tv.utils.SysUtils;
import com.yhy.mz.tv.utils.ViewUtils;
import com.yhy.mz.tv.widget.ScaleConstraintLayout;
import com.yhy.mz.tv.widget.TabHorizontalGridView;
import com.yhy.mz.tv.widget.TabViewPager;
import com.yhy.router.annotation.Router;

import java.util.List;

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
public class MainActivity extends BaseActivity implements ViewTreeObserver.OnGlobalFocusChangeListener, View.OnKeyListener, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ScaleConstraintLayout sclSearch;
    private ScaleConstraintLayout sclHistory;
    private ScaleConstraintLayout sclFavor;
    private ScaleConstraintLayout sclSettings;
    private AppCompatImageView ivNet;
    private TabHorizontalGridView hgTitle;
    private TabViewPager vpContent;
    private ArrayObjectAdapter mHgTopAdapter;
    private ChanContentVPAdapter mVpAdapter;

    private boolean isFirstIn = true;
    private int mCurrentPageIndex = 0;
    private boolean isSkipTabFromViewPager = false;

    private TextView mOldTitle;

    private NetworkChangeReceiver networkChangeReceiver;

    private final OnChildViewHolderSelectedListener onChildViewHolderSelectedListener = new OnChildViewHolderSelectedListener() {
        @Override
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subPosition) {
            super.onChildViewHolderSelected(parent, child, position, subPosition);

            if (child != null && position != mCurrentPageIndex) {
                child.itemView.requestFocus();

                Log.e(TAG, "onChildViewHolderSelected: 000 isSkipTabFromViewPager" + isSkipTabFromViewPager);
                TextView currentTitle = child.itemView.findViewById(R.id.tv_scv_item);
                if (isSkipTabFromViewPager) {
                    Log.e(TAG, "onChildViewHolderSelected: 111");

                    if (mOldTitle != null) {
                        Log.e(TAG, "onChildViewHolderSelected: 222");

                        mOldTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                        Paint paint = mOldTitle.getPaint();
                        if (paint != null) {
                            paint.setFakeBoldText(false);
                            //viewpager切页标题不刷新，调用invalidate刷新
                            mOldTitle.invalidate();
                        }
                    }
                    currentTitle.setTextColor(getResources().getColor(R.color.colorBlue));
                    Paint paint = currentTitle.getPaint();
                    if (paint != null) {
                        paint.setFakeBoldText(true);
                        // viewpager切页标题不刷新，调用invalidate刷新
                        currentTitle.invalidate();
                    }
                }
                mOldTitle = currentTitle;
            }

            isSkipTabFromViewPager = false;
            Log.e(TAG, "onChildViewHolderSelected mViewPager != null: " + (vpContent != null) + " position:" + position);
            setCurrentItemPosition(position);
        }
    };

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        sclSearch = $(R.id.scl_search);
        sclHistory = $(R.id.scl_history);
        sclFavor = $(R.id.scl_favor);
        sclSettings = $(R.id.scl_settings);
        ivNet = $(R.id.iv_net);
        hgTitle = $(R.id.hg_title);
        vpContent = $(R.id.vp_content);

        hgTitle.setHorizontalSpacing(ViewUtils.dp2px(4));
        mHgTopAdapter = new ArrayObjectAdapter(new TabChanPresenter());
        ItemBridgeAdapter tempAdapter = new ItemBridgeAdapter(mHgTopAdapter);
        hgTitle.setAdapter(tempAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(tempAdapter, FocusHighlight.ZOOM_FACTOR_MEDIUM, false);

        List<Chan> chanList = ChannelManager.instance.getChanList();
        mHgTopAdapter.addAll(0, chanList);

        vpContent.setOffscreenPageLimit(2);
        mVpAdapter = new ChanContentVPAdapter(getSupportFragmentManager());
        vpContent.setAdapter(mVpAdapter);
    }

    @Override
    protected void initData() {
        initBroadCast();

        // 新版本检查
        checkNewVersion();
    }

    private void checkNewVersion() {
        FirApi.instance.versionQuery(vi -> {
            if (null != vi) {
                // 存在版本信息
                LogUtils.iTag(TAG, JsonUtils.toJson(vi));
                long versionCode = SysUtils.getVersionCode();
                if (versionCode < Long.parseLong(vi.version)) {
                    // 发现新版本
                    downloadApk(vi);
                }
            }
        });
    }

    private void downloadApk(FirVersionInfo version) {
        DownloadManager manager = new DownloadManager.Builder(this)
                .apkName(version.name + "_" + version.versionShort + ".apk")
                .apkUrl(version.directInstallUrl)
                .apkDescription(version.changeLog)
                .smallIcon(R.mipmap.ic_launcher)
                .apkSize(FileUtils.formatSize(version.binary.fSize))
                .showNotification(true)
                .forcedUpgrade(true)
                .jumpInstallPage(true)
                .showNewerToast(true)
                .showBgdToast(true)
                .build();

        manager.download();
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void initEvent() {
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        hgTitle.addOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);
        vpContent.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "onPageSelected position: " + position);
                if (isFirstIn) {
                    isFirstIn = false;
                } else {
                    isSkipTabFromViewPager = true;
                }
                if (position != mCurrentPageIndex) {
                    hgTitle.setSelectedPosition(position);
                }
            }
        });

        sclSearch.setOnClickListener(this);
        sclHistory.setOnClickListener(this);
        sclFavor.setOnClickListener(this);
        sclSettings.setOnClickListener(this);

        sclSearch.setOnKeyListener(this);
        sclHistory.setOnKeyListener(this);
        sclFavor.setOnKeyListener(this);
        sclSettings.setOnKeyListener(this);
    }

    @Override
    protected void setDefault() {
        vpContent.setCurrentItem(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scl_search:
                success("搜索");
                break;
            case R.id.scl_history:
                success("历史");
                break;
            case R.id.scl_favor:
                success("收藏");
                break;
            case R.id.scl_settings:
                success("设置");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            switch (v.getId()) {
                case R.id.scl_search:
                case R.id.scl_history:
                case R.id.scl_favor:
                case R.id.scl_settings:
                    if (hgTitle != null) {
                        hgTitle.requestFocus();
                    }
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        Log.e(TAG, "onGlobalFocusChanged newFocus: " + newFocus);
        Log.e(TAG, "onGlobalFocusChanged oldFocus: " + oldFocus);
        if (newFocus == null || oldFocus == null) {
            return;
        }
        if (newFocus.getId() == R.id.tv_scv_item && oldFocus.getId() == R.id.tv_scv_item) {
            ((TextView) newFocus).setTextColor(getResources().getColor(R.color.colorWhite));
            ((TextView) newFocus).getPaint().setFakeBoldText(true);
            ((TextView) oldFocus).setTextColor(getResources().getColor(R.color.colorWhite));
            ((TextView) oldFocus).getPaint().setFakeBoldText(false);
        } else if (newFocus.getId() == R.id.tv_scv_item && oldFocus.getId() != R.id.tv_scv_item) {
            ((TextView) newFocus).setTextColor(getResources().getColor(R.color.colorWhite));
            ((TextView) newFocus).getPaint().setFakeBoldText(true);
        } else if (newFocus.getId() != R.id.tv_scv_item && oldFocus.getId() == R.id.tv_scv_item) {
            ((TextView) oldFocus).setTextColor(getResources().getColor(R.color.colorBlue));
            ((TextView) oldFocus).getPaint().setFakeBoldText(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkChangeReceiver);
    }

    private void setCurrentItemPosition(int position) {
        if (vpContent != null && position != mCurrentPageIndex) {
            mCurrentPageIndex = position;
            vpContent.setCurrentItem(position);
        }
    }

    public View getHgTitle() {
        return hgTitle;
    }

    public Group getGroup() {
        return null;
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_ETHERNET:
                        ivNet.setImageResource(R.mipmap.ethernet);
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        ivNet.setImageResource(R.mipmap.wifi);
                        break;
                    default:
                        break;
                }
            } else {
                ivNet.setImageResource(R.mipmap.no_net);
            }
        }
    }
}
