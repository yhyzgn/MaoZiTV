package com.yhy.mz.tv.component.presenter;

import android.annotation.SuppressLint;
import android.widget.TextView;
import android.widget.Toast;

import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.RowPresenter;

import com.yhy.mz.tv.R;
import com.yhy.mz.tv.component.base.BaseListRowPresenter;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.utils.ViewUtils;

/**
 * Created on 2023-01-24 19:19
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class TypeZeroListRowPresenter extends BaseListRowPresenter {
    @SuppressLint("RestrictedApi")
    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);
        final ListRowPresenter.ViewHolder rowViewHolder = (ListRowPresenter.ViewHolder) holder;

        rowViewHolder.getGridView().setHorizontalSpacing(ViewUtils.dp2px(12));
        rowViewHolder.getGridView().setFocusScrollStrategy(HorizontalGridView.FOCUS_SCROLL_ITEM);
        RowHeaderPresenter.ViewHolder vh = rowViewHolder.getHeaderViewHolder();
        TextView textView = vh.view.findViewById(R.id.row_header);
        textView.setTextSize(ViewUtils.dp2px(7));
        textView.setTextColor(textView.getContext().getResources().getColor(R.color.colorWhite));
        textView.setPadding(0, 0, 0, 20);
        setOnItemViewClickedListener((itemViewHolder, item, rowViewHolder1, row) -> {
            if (item instanceof Video) {
                Toast.makeText(((ViewHolder) rowViewHolder1).getGridView().getContext(), "位置:" + ((ViewHolder) rowViewHolder1).getGridView().getSelectedPosition(), Toast.LENGTH_SHORT).show();
//                ((ViewHolder) rowViewHolder1).getGridView().getContext().startActivity(new Intent(((ViewHolder) rowViewHolder1).getGridView().getContext(), VideoDetailActivity.class));
            }
        });

    }
}
