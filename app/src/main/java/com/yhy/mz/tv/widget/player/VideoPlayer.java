package com.yhy.mz.tv.widget.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.yhy.mz.tv.R;

/**
 * Created on 2023-02-01 23:04
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class VideoPlayer extends StandardGSYVideoPlayer {
    public VideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }

    @Override
    protected void touchDoubleUp(MotionEvent e) {
        //super.touchDoubleUp();
        //不需要双击暂停
    }
}
