package com.balloon.project;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.balloon.project.utils.PixelHelper;



public class Balloon extends android.support.v7.widget.AppCompatImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {


    private ValueAnimator mAnimator;
    private BalloonListener mListener;
    private boolean mPopped;

    public Balloon(Context context) {
        super(context);
    }

    public Balloon(Context context,int color ,int rawHeight,int id) {
        super(context);

        mListener = (BalloonListener) context;


        this.setImageResource(R.drawable.balloon);
        this.setColorFilter(color);

        this.setId(id);

        int rawWidth = rawHeight / 3;

        int dpHeight = PixelHelper.pixelsToDp(rawHeight-100, context);
        int dpWidth = PixelHelper.pixelsToDp(rawWidth, context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth, dpHeight);
        this.setLayoutParams(params);

    }

    public void releaseBalloon(int screenHeight, int duration,int max) {

        mAnimator = new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeight, max);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {

    }

    @Override
    public void onAnimationCancel(Animator animator) {
        if (!mPopped) {
            mListener.popBalloon(this, false);
        }
    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        setY((Float) valueAnimator.getAnimatedValue());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mPopped && event.getAction() == MotionEvent.ACTION_DOWN) {
            mListener.popBalloon(this, true);
            mPopped = true;

        }

        return true;
    }

    public void setPopped(boolean popped) {
        mPopped = popped;
        if(popped){
            mAnimator.cancel();
        }
    }



    public interface BalloonListener {
        void popBalloon(Balloon balloon, boolean userTouch);
    }
}
