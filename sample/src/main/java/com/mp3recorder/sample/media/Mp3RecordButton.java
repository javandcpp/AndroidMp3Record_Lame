/*
 * Copyright 2017 Huawque
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * https://github.com/javandoc/AndroidMp3Record_Lame
 *
 */

package com.mp3recorder.sample.media;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.mp3recorder.sample.R;


/**
 * Created by android on 4/12/17.
 */

public class Mp3RecordButton extends FrameLayout {
    private CircleView cirecleView_third;
    private CircleView cirecleView_second;
    private CircleView cirecleView_first;
    private final int STATE_NORMAL = 0;
    private final int STATE_SCALE_ANIMATION = 1;
    private final int STATE_SCALE_ANIMATION_FINISH = 2;
    private int currentState;
    private AnimatorSet animatorSet_three;
    private static Handler mHandler = new Handler();
    private boolean touchDown;
    private IRecordButtonTouchStateListener mRecordTouchListener;
    private long recordStartTime;
    private IShowHintTvListener iShowHintListener;
    private long startTime;

    public Mp3RecordButton(Context context) {
        super(context);
        initView();
    }

    public Mp3RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    public Mp3RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.media_record_layout, this, true);
        cirecleView_first = ((CircleView) findViewById(R.id.circle1));
        cirecleView_second = ((CircleView) findViewById(R.id.circle2));
        cirecleView_third = ((CircleView) findViewById(R.id.circle3));
    }

    private void startAnimation(Animator.AnimatorListener animatorListener) {
        animatorSet_three = new AnimatorSet();
        cirecleView_third.setVisibility(VISIBLE);
        ObjectAnimator objectAnimatorFirstScaleX = ObjectAnimator.ofFloat(cirecleView_first, "scaleX", 1.0f, 1.2f);
        ObjectAnimator objectAnimatorFirstScaleY = ObjectAnimator.ofFloat(cirecleView_first, "scaleY", 1.0f, 1.2f);
        ObjectAnimator objectAnimatorSecondScaleX = ObjectAnimator.ofFloat(cirecleView_second, "scaleX", 1.0f, 1.1f);
        ObjectAnimator objectAnimatorSecondScaleY = ObjectAnimator.ofFloat(cirecleView_second, "scaleY", 1.0f, 1.1f);
        ObjectAnimator objectAnimatorThreeScaleX = ObjectAnimator.ofFloat(cirecleView_third, "scaleX", 0.0f, cirecleView_second.getRadius() / cirecleView_third.getRadius() + 1f);
        ObjectAnimator objectAnimatorThreeScaleY = ObjectAnimator.ofFloat(cirecleView_third, "scaleY", 0.0f, cirecleView_second.getRadius() / cirecleView_third.getRadius() + 1f);
        animatorSet_three.play(objectAnimatorFirstScaleX).with(objectAnimatorFirstScaleY);
        animatorSet_three.play(objectAnimatorSecondScaleX).with(objectAnimatorSecondScaleY);
        animatorSet_three.play(objectAnimatorThreeScaleX).with(objectAnimatorThreeScaleY).after(50);
        animatorSet_three.setDuration(300);
        animatorSet_three.start();

        animatorSet_three.addListener(animatorListener);


    }

    private void resetAnimation() {
        if (null != animatorSet_three) {
            animatorSet_three.cancel();
        }
        cirecleView_third.setVisibility(GONE);
        animatorSet_three = new AnimatorSet();
        animatorSet_three.playTogether(
                ObjectAnimator.ofFloat(cirecleView_first, "scaleX", 1.0f),
                ObjectAnimator.ofFloat(cirecleView_first, "scaleY", 1.0f),
                ObjectAnimator.ofFloat(cirecleView_second, "scaleX", 1.0f),
                ObjectAnimator.ofFloat(cirecleView_second, "scaleY", 1.0f),
                ObjectAnimator.ofFloat(cirecleView_third, "scaleX", 0f),
                ObjectAnimator.ofFloat(cirecleView_third, "scaleY", 0f)
        );
        animatorSet_three.setDuration(1);
        animatorSet_three.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (currentState == STATE_NORMAL) {
                    startAnimation(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (null != mRecordTouchListener) {
                                mRecordTouchListener.touchDown();
                                recordStartTime = System.currentTimeMillis();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    touchDown = true;
                    if (null != iShowHintListener) {
                        iShowHintListener.showHintTv(false);
                    }


                }
                break;
            case MotionEvent.ACTION_UP:
                currentState = STATE_NORMAL;
                if (touchDown) {
                    resetAnimation();
                    if (null != mRecordTouchListener) {
                        mRecordTouchListener.touchUp(limitTime());
                    }
                    if (null != iShowHintListener) {
                        iShowHintListener.showHintTv(true);
                    }
                    touchDown = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("tag", "CANCEL");
                break;
        }
        return true;
    }


    private long limitTime() {
        return (System.currentTimeMillis() - recordStartTime);
    }


    public void setHintShowListener(IShowHintTvListener ishowlistener) {
        this.iShowHintListener = ishowlistener;
    }

    public void setRecordButtonTouchStateListener(IRecordButtonTouchStateListener recordButtonTouchStateListener) {
        this.mRecordTouchListener = recordButtonTouchStateListener;
    }

    public void release() {

    }


}
