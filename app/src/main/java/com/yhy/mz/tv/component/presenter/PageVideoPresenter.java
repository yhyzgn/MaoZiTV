package com.yhy.mz.tv.component.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.leanback.widget.Presenter;

import com.yhy.mz.tv.R;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.utils.ImgUtils;

/**
 * Created on 2023-01-24 19:37
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class PageVideoPresenter extends Presenter {

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
        return new TabChanPresenter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        if (item instanceof Video) {
            PageVideoPresenter.ViewHolder vh = (PageVideoPresenter.ViewHolder) viewHolder;
            Video vd = (Video) item;
            ImgUtils.load(viewHolder.view.getContext(), vh.ivCover, vd.imgCover);
            vh.tvScore.setText(vd.score + "");
            vh.tvName.setText(vd.title);
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    public static class ViewHolder extends Presenter.ViewHolder {

        private final AppCompatImageView ivCover;
        private final AppCompatTextView tvScore;
        private final AppCompatTextView tvName;

        ViewHolder(View view) {
            super(view);
            ivCover = view.findViewById(R.id.iv_cover);
            tvScore = view.findViewById(R.id.tv_score);
            tvName = view.findViewById(R.id.tv_name);
        }
    }
}
