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
import android.widget.ImageView;

import com.mp3recorder.sample.R;


/**
 * Created by android on 4/12/17.
 */

public class AudioWaveView extends FrameLayout {

    private int decebleValue;
    private ImageView[] imageViews;
    private com.mp3recorder.sample.media.WaveLine audioWaveView;

    public AudioWaveView(Context context) {
        super(context);
        initView();

    }

    public AudioWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AudioWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.audio_record_wave_view_layout, this, true);
        audioWaveView = ((com.mp3recorder.sample.media.WaveLine) findViewById(R.id.ivWave0));

    }

    public void beginWave(){
        audioWaveView.drawWaveLine();
    }

    public void updateDeceibelValue(double decebleValue){
        audioWaveView.updateDecibelValue(decebleValue);
    }

    public void stopWave(){
        audioWaveView.stopDrawWave();
    }


}
