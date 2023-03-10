package com.yhy.mz.tv.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FocusHighlightHelper;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yhy.mz.tv.R;
import com.yhy.mz.tv.channel.ChannelManager;
import com.yhy.mz.tv.component.base.BaseLazyLoadFragment;
import com.yhy.mz.tv.component.presenter.PageVideoPresenter;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.Chan;
import com.yhy.mz.tv.ui.MainActivity;
import com.yhy.mz.tv.utils.LogUtils;
import com.yhy.mz.tv.utils.ViewUtils;
import com.yhy.mz.tv.widget.TabVerticalGridView;
import com.yhy.router.EasyRouter;

import java.util.List;

/**
 * Created on 2023-01-24 01:19
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class VpMainFragment extends BaseLazyLoadFragment {
    private static final String TAG = "VpMainFragment";
    private final static String BUNDLE_KEY_POSITION = "bundleKeyPosition";
    private final static String BUNDLE_KEY_CHAN_CODE = "bundleKeyChanCode";
    private View mRootView;
    private MainActivity mActivity;
    private ProgressBar pbLoading;
    private TabVerticalGridView hgContent;
    private int mCurrentTabPosition;
    private int mCurrentChanCode;
    private PageVideoPresenter mPresenter;
    private ArrayObjectAdapter mAdapter;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            List<Video> list = (List<Video>) msg.obj;
            if (null != list && !list.isEmpty()) {
                mAdapter.addAll(0, list);
                hgContent.setVisibility(View.VISIBLE);
                pbLoading.setVisibility(View.GONE);
            }
        }
    };

    private final Thread mThread = new Thread(() -> {
        try {
            List<Video> list = ChannelManager.instance.getChannel(Chan.parse(mCurrentChanCode)).page(1);
            Message msg = new Message();
            msg.what = 1;
            msg.obj = list;

            mHandler.sendMessageDelayed(msg, 200);
        } catch (Exception e) {
            LogUtils.eTag(TAG, e.getMessage());
        }
    });

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                case RecyclerView.SCROLL_STATE_SETTLING:
                    Glide.with(mActivity).pauseRequests();
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    Glide.with(mActivity).resumeRequests();
            }
        }
    };

    private final OnChildViewHolderSelectedListener onSelectedListener = new OnChildViewHolderSelectedListener() {
        @Override
        public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subPosition) {
            super.onChildViewHolderSelected(parent, child, position, subPosition);
            Log.e(TAG, "onChildViewHolderSelected: " + position);
            if (hgContent == null) {
                return;
            }
            Log.e(TAG, "onChildViewHolderSelected: " + "　isPressUp:" + hgContent.isPressUp() + " isPressDown:" + hgContent.isPressDown());
        }
    };

    public static VpMainFragment newInstance(int position, Chan chan) {
        Bundle args = new Bundle();
        args.putInt(BUNDLE_KEY_POSITION, position);
        args.putInt(BUNDLE_KEY_CHAN_CODE, chan.getCode());

        VpMainFragment fragment = new VpMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG + " pos:", "onCreate: ");
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mCurrentTabPosition = getArguments().getInt(BUNDLE_KEY_POSITION);
        mCurrentChanCode = getArguments().getInt(BUNDLE_KEY_CHAN_CODE);
        Log.e(TAG + " pos:" + mCurrentTabPosition, " chanCode: " + mCurrentChanCode);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_content, container, false);
            initView();
            initListener();
        }
        return mRootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void fetchData() {
        loadData();
    }

    private void initView() {
        pbLoading = mRootView.findViewById(R.id.pb_loading);
        hgContent = mRootView.findViewById(R.id.hg_content);
        hgContent.setTabView(mActivity.getHgTitle());
        hgContent.setNumColumns(8);
        hgContent.setHorizontalSpacing(ViewUtils.dp2px(8));
        hgContent.setVerticalSpacing(ViewUtils.dp2px(4));
        mPresenter = new PageVideoPresenter();
        mAdapter = new ArrayObjectAdapter(mPresenter);
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(mAdapter);
        hgContent.setAdapter(itemBridgeAdapter);
        FocusHighlightHelper.setupBrowseItemFocusHighlight(itemBridgeAdapter, FocusHighlight.ZOOM_FACTOR_SMALL, false);
    }

    private void initListener() {
        hgContent.addOnScrollListener(onScrollListener);
        hgContent.addOnChildViewHolderSelectedListener(onSelectedListener);

        mPresenter.setOnItemClickListener((view, video) -> {
            EasyRouter.getInstance()
                    .with(this)
                    .to("/activity/detail")
                    .param("mVideo", video)
                    .param("mChanCode", mCurrentChanCode)
                    .go();
        });
    }

    private void loadData() {
        pbLoading.setVisibility(View.VISIBLE);
        hgContent.setVisibility(View.INVISIBLE);
        mThread.start();
    }

    private void scrollToTop() {
        if (hgContent != null) {
            hgContent.scrollToPosition(0);
            if (mActivity.getGroup() != null && mActivity.getGroup().getVisibility() != View.VISIBLE) {
                mActivity.getGroup().setVisibility(View.VISIBLE);
            }
        }
    }
}
