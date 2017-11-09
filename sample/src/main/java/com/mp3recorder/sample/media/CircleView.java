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
import android.util.AttributeSet;
import android.view.View;

import com.mp3recorder.sample.R;


/**
 * Created by android on 4/12/17.
 */

public class CircleView extends View {

    private float mRadius;
    private Paint mPaint;
    private int mColor;
    private int width;
    private int height;


    public CircleView(Context context) {
        super(context);
        initView();
    }


    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirecleView);
        mColor = typedArray.getColor(R.styleable.CirecleView_bg_color, -1);
        mRadius = typedArray.getDimension(R.styleable.CirecleView_radius, 0);
        typedArray.recycle();
        initView();
    }

    public float getRadius() {
        return mRadius;
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }


    private void initView() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int verticalCenter = height / 2;
        int horizontalCenter = width / 2;
        canvas.drawCircle(horizontalCenter, verticalCenter, mRadius, mPaint);
    }
}
