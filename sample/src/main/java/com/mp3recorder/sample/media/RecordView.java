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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mp3recorder.sample.R;


/**
 * Created by android on 4/18/17.
 */

public class RecordView extends FrameLayout implements IShowHintTvListener {
    private AudioWaveView mAudioWaveView;
    private TextView mTvHint;
    private TextView mTvRecordTime;
    private Mp3RecordButton recordButton;
    private ImageButton btnBack;

    public RecordView(Context context) {
        super(context);
        initView();
    }


    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {

        LayoutInflater.from(getContext()).inflate(R.layout.mp3_record_view, this, true);
        mAudioWaveView = ((AudioWaveView) findViewById(R.id.waveView));
        mTvRecordTime = ((TextView) findViewById(R.id.tvRecordTime));
        mTvHint = ((TextView) findViewById(R.id.tvHint));
        recordButton = (Mp3RecordButton) findViewById(R.id.mp3RecordView);
        btnBack = ((ImageButton) findViewById(R.id.btnBack));
        recordButton.setHintShowListener(this);
    }


    private void hideHintView() {
        if (mTvHint.getVisibility() == VISIBLE) {
            mTvHint.setVisibility(GONE);
        }
    }

    public ImageButton getBackButton() {
        return btnBack;
    }

    private void showHintView() {
        if (mTvHint.getVisibility() == GONE) {
            mTvHint.setVisibility(VISIBLE);
        }
    }

    public AudioWaveView getAudioWaveView() {
        return mAudioWaveView;
    }

    public TextView getTvRecordTimeView() {
        return mTvRecordTime;
    }

    public Mp3RecordButton getRecordButton() {
        return recordButton;
    }

    @Override
    public void showHintTv(boolean show) {
        if (show) {
            showHintView();
        } else {
            hideHintView();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            mTvRecordTime.setText("00:00/05:00");
        }
    }


    public void updateTvRecordTime(long time) {

        if (time <= 0) {
            return;
        }
        long hour = 0;
        long minute = 0;
        long second = 0;

        if (time <= 0)
            mTvRecordTime.setText("00:00/05:00");
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                mTvRecordTime.setText(unitFormat(minute) + ":" + unitFormat(second) + "/05:00");
            } else {
                hour = minute / 60;
                if (hour > 99)
                    mTvRecordTime.setText("99:59:59/05:00");
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                mTvRecordTime.setText(unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second) + "/05:00");
            }
        }
    }


    private String unitFormat(long i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


}


