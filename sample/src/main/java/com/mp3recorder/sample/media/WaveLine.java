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


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.mp3recorder.sample.R;
import com.mp3recorder.sample.utils.Utils;

import java.util.Random;


public class WaveLine extends View {
    private final String TAG = WaveLine.this.getClass().getSimpleName();
    private int lineWidth;
    private Random random;
    private Paint mPaint;
    private boolean startDraw;
    private int mRandomNumber = 1;
    private RectF rectLine;
    private final int lineNumber = 19;
    private boolean isDrawing;
    private Thread drawLineThread;
    private final float MAX_DBVALUE = 140;//默认DB最大值
    private float mDecibelValue;
    private int reverseValue = 0;
    private int peakValue;
    private boolean defaultMode = true;
    private int mColor;

    private Handler mHandler = new Handler();

    public WaveLine(Context context) {
        super(context);
        initView();
    }

    public WaveLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveLine);
        mColor = typedArray.getColor(R.styleable.WaveLine_line_color, -1);
        typedArray.recycle();
        initView();
    }

    public WaveLine(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
        initView();
    }


    private void initView() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        lineWidth = Utils.dip2px(getContext(), 0.7f);
        mPaint.setStrokeWidth(0.7f);
        random = new Random();
        rectLine = new RectF();


    }

    /**
     * @param decibelValue 0-100
     */
    public void updateDecibelValue(double decibelValue) {
        this.mDecibelValue = (float) decibelValue;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        float widthPerLine = getWidth() / lineNumber;
        float height = getHeight();
        int round = Math.round(lineNumber / 2);
        double heightRatio = 0;

        for (int i = 0; i <= (lineNumber - 1); i++) {
            float randomValue = 0;
            heightRatio = getHeightRatio(round, i);

            if (defaultMode) {
                randomValue = mRandomNumber + peakValue;
                mDecibelValue = 1;
            } else {
                randomValue = random.nextInt(mRandomNumber <= 0 ? 1 : mRandomNumber) + mDecibelValue + peakValue;
            }
            int drawHeight = (int) ((randomValue / 100) * height);
            float startX = ((widthPerLine - lineWidth) / 2) + (widthPerLine * i);
            float startY = (float) (height - (drawHeight * heightRatio)) / 2;
            float stopX = ((widthPerLine + lineWidth) / 2) + (widthPerLine * i);
            float stopY = (float) (height + (drawHeight * heightRatio)) / 2;
            rectLine.setEmpty();
            rectLine.set(startX, startY, stopX, stopY);
            canvas.drawRect(rectLine, mPaint);
        }
    }

    private double getHeightRatio(int round, int i) {
        double heightRatio;
        if (i < round) {
            mRandomNumber = 15;//动画幅度
            heightRatio = (i % 2 == 0 ? 0.3 : 0.5);//绘制高度比
            peakValue = i * 12;//峰值
        } else if (i == round) {
            mRandomNumber = 27;
            peakValue = 45;
            heightRatio = 0.8;
        } else {
            mRandomNumber = 15;
            if (reverseValue == (lineNumber - 1)) {//反向
                reverseValue = 0;
            }
            reverseValue += 2;
            heightRatio = ((i - reverseValue) % 2 == 0 ? 0.3 : 0.5);
            peakValue = (i - reverseValue) * 12;

        }
        return heightRatio;
    }

    public void stopDrawWave() {
        defaultMode = true;
        isDrawing = false;
    }

    public void drawWaveLine() {
        defaultMode = false;
        isDrawing = true;
        drawLineThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isDrawing) {
                    try {
                        //根据DB值动态调整给制速度
                        int speedRatio = (int) (mDecibelValue / MAX_DBVALUE * 100);
                        Thread.sleep(180 - (int) (1.5 * speedRatio));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    postInvalidate();
                }
                mRandomNumber = 1;
                postInvalidate();

            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawLineThread.start();
            }
        }, 350);
    }
}
