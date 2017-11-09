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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.guagua.mp3recorder.MP3Recorder;
import com.guagua.mp3recorder.util.LameUtil;
import com.mp3recorder.sample.R;

import java.io.File;
import java.io.IOException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by android on 4/10/17.
 */

public class RecordDialog extends Dialog implements IRecordButtonTouchStateListener, IAudioDialogClickListener, MP3Recorder.RecordDecibelListener, LameUtil.LameWriteFinishCall, MP3Recorder.RecordTimeListener {
    private final String TAG = RecordDialog.this.getClass().getSimpleName();
    private ViewGroup mRootView;
    private RecordView recordView;
    private RecorderToMp3 recorderToMp3Instance;
    private Runnable recordTask;

    private static Handler mHandler = new Handler();
    private long recordTotalTime = 0;
    private AudioWaveView audioWaveView;
    private AudioRecordConfirm mListener;
    private String mAudilFileName;

    /**
     * @param context
     */
    public RecordDialog(Context context) {
        super(context, R.style.RecordDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.record_dialog_layout);
        initView();
    }

    private void initView() {
        recordView = ((RecordView) findViewById(R.id.recordButton));
        audioWaveView = recordView.getAudioWaveView();
        recordView.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        recordView.getRecordButton().setRecordButtonTouchStateListener(this);
        recordView.setVisibility(View.VISIBLE);


        try {
            MediaConfig.isHasPermission();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     */
    @Override
    public void touchDown() {
        if (MediaConfig.getAvailableInternalMemorySize(getContext()) > 2) {
            recordTotalTime = 0;
            audioWaveView.beginWave();
            try {
                getMediaRecordObject().startRecord(MediaConfig.getMediaRecordDestFile());

            } catch (IOException e) {

            } catch (Exception e) {
                if (getMediaRecordObject().isRecording()) {
                    getMediaRecordObject().stopRecord();
                }
                audioWaveView.stopWave();
                Toast.makeText(getContext(), "录音失败,请检查权限!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "内存不足", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 停止录音
     */
    @Override
    public void touchUp(long recordTime) {
        if (getMediaRecordObject().isRecording()) {
            getMediaRecordObject().stopRecord();
            audioWaveView.stopWave();
        }
    }


    /**
     * @param recordV
     * @param completeV
     */
    private void switchDisplay(int recordV, int completeV) {
        recordView.setVisibility(recordV);
    }

    /**
     * 录音完成,确认
     */
    @Override
    public void positive() {
        dismiss();
        if (null != mListener) {
            mListener.audioConfirm(mAudilFileName, recordTotalTime);
        }
    }


    public void setAudioConfirmListener(AudioRecordConfirm audioConfirmListener) {
        this.mListener = audioConfirmListener;

    }


    public interface AudioRecordConfirm {
        void audioConfirm(String mAudilFileName, long recordTotalTime);
    }

    /**
     * 录音完成,取消
     */
    @Override
    public void negative() {
        switchDisplay(VISIBLE, GONE);
    }

    private RecorderToMp3 getMediaRecordObject() {
        if (null == recorderToMp3Instance) {
            recorderToMp3Instance = RecorderToMp3.getInstance();
            recorderToMp3Instance.setDebug(false);//logcat
            recorderToMp3Instance.setTimeListener(this);
            recorderToMp3Instance.setDecibelListener(this);
            recorderToMp3Instance.setRecorderWriteCompleteListener(this);
        }
        return recorderToMp3Instance;

    }

    /**
     * 分贝回调
     *
     * @param v
     */
    @Override
    public void decibelValueCallback(double v) {
        audioWaveView.updateDeceibelValue(v);
    }

    @Override
    public void lameWriteCallBack(final String s) {
        //写入目标文件完成
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (recordTotalTime >= 1) {
                    File mediaFile = new File(MediaConfig.getMediaSourceDir(), s);
                    mAudilFileName = mediaFile.getAbsolutePath();
                    Toast.makeText(getContext(), "record complete", Toast.LENGTH_SHORT).show();
                } else {
                    File dstFile = new File(MediaConfig.getMediaSourceDir(), s);
                    if (dstFile.exists()) {
                        dstFile.delete();
                    }
                }
            }
        });


    }

    @Override
    public void lameWriteCallBack(boolean b) {

    }

    @Override
    public void timeCallback(final long l) {
        recordTotalTime = (l / 1000 - 1);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                recordView.updateTvRecordTime(l / 1000 - 1);
            }
        });
        if (recordTotalTime == 290) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "录音最长5分钟噢", Toast.LENGTH_LONG).show();
                }
            });
        } else if (recordTotalTime == 300) {
            touchUp(recordTotalTime);
        }
    }
}
