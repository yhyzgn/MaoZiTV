package com.yhy.mz.tv.component.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.yhy.mz.tv.R;
import com.yhy.mz.tv.model.ems.Chan;

/**
 * 标题适配器
 * <p>
 * Created on 2023-01-20 03:35
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class TabChanPresenter extends Presenter {

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        if (item instanceof Chan) {
            ViewHolder vh = (ViewHolder) viewHolder;
            vh.tvScvItem.setText(((Chan) item).getName());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    public static class ViewHolder extends Presenter.ViewHolder {

        private final TextView tvScvItem;

        ViewHolder(View view) {
            super(view);
            tvScvItem = view.findViewById(R.id.tv_scv_item);
        }
    }
}
