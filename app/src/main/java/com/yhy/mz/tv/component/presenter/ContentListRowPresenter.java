package com.yhy.mz.tv.component.presenter;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.leanback.widget.BaseOnItemViewClickedListener;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.RowPresenter;

import com.yhy.mz.tv.R;
import com.yhy.mz.tv.component.base.BaseListRowPresenter;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.utils.ToastUtils;
import com.yhy.mz.tv.utils.ViewUtils;

/**
 * Created on 2023-01-24 19:13
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class ContentListRowPresenter extends BaseListRowPresenter {

    private static final String TAG = "ContentListRowPresenter";

    @SuppressLint("RestrictedApi")
    @Override
    protected void initializeRowViewHolder(final RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);

        final ListRowPresenter.ViewHolder rowViewHolder = (ListRowPresenter.ViewHolder) holder;
        rowViewHolder.getGridView().setHorizontalSpacing(ViewUtils.dp2px(12));
        RowHeaderPresenter.ViewHolder headerViewHolder = holder.getHeaderViewHolder();
        final TextView tv = headerViewHolder.view.findViewById(R.id.row_header);
        tv.setTextColor(tv.getContext().getResources().getColor(R.color.colorWhite));
        tv.setPadding(0, 20, 0, 20);
        tv.setTextSize(ViewUtils.dp2px(12));
        rowViewHolder.getGridView().setFocusScrollStrategy(HorizontalGridView.FOCUS_SCROLL_ITEM);

        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder1, row) -> {
            if (item instanceof Video) {
                ToastUtils.shortT("播放" + ((Video) item).title);
//                    tv.getContext().startActivity(new Intent(tv.getContext(), VideoDetailActivity.class));
            }
        });
    }
}
