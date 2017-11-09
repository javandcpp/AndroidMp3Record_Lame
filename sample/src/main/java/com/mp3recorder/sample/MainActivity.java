
package com.mp3recorder.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.guagua.mp3recorder.MP3Recorder;
import com.guagua.mp3recorder.util.LameUtil;
import com.mp3recorder.sample.media.RecordDialog;

import java.io.File;
import java.io.IOException;


public class MainActivity extends Activity implements LameUtil.LameWriteFinishCall, MP3Recorder.RecordDecibelListener, MP3Recorder.RecordTimeListener, Handler.Callback {


    private MP3Recorder mRecorder = new MP3Recorder();
    private Handler mhandler;
    private TextView tvTime;
    private TextView tvState;
    int i = 0;
    public boolean isRecording;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Button startButton = (Button) findViewById(R.id.StartButton);
        final Button pauseButton = (Button) findViewById(R.id.PauseButton);
        final Button recordWave = (Button) findViewById(R.id.recordWave);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvState = (TextView) findViewById(R.id.tvState);
        tvState.setText("idle");
        tvTime.setText("00:00");
        LameUtil.setLameCallback(this);
        mhandler = new Handler(this);

        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    mRecorder.setRecordFile(new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "---audio.mp3"));
                    if (mRecorder.isRecording()) {
                        isRecording = true;
                        mRecorder.stop();
                        startButton.setText("start");
                    }else{
                        startButton.setText("stop");
                        mRecorder.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mRecorder.isRecording()) {
                    try {
                        if (!mRecorder.isPausing()) {
                            mRecorder.pause();
                            pauseButton.setText("resume");
                            Log.d("recorder","pause");
                        } else {
                            mRecorder.resume();
                            Log.d("recorder","resume");
                            pauseButton.setText("pause");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"录音未开始",Toast.LENGTH_LONG).show();
                }
            }
        });
        recordWave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordDialog di=new RecordDialog(MainActivity.this);
                di.show();
            }
        });
        LameUtil.setDebug(true);
        mRecorder.setmRecordTimeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecorder.stop();
    }

    @Override
    public void lameWriteCallBack(String fileName) {
        Log.d("activity", "lame write callback:" + ",fileName:" + fileName);
        Message message = mhandler.obtainMessage();
        message.what = 2;
        mhandler.sendMessage(message);
    }

    @Override
    public void lameWriteCallBack(boolean status) {
        Log.d("activity", "lame write callback:" + status);
    }

    @Override
    public void decibelValueCallback(double decibelValueCallback) {
        Log.d("activity", "decibel value:" + decibelValueCallback);
    }

    @Override
    public void timeCallback(long startTime) {
        Log.d("time", "total mills:" + secToTime(startTime / 1000));
        Message message = mhandler.obtainMessage();
        message.obj = secToTime(startTime / 1000);
        message.what = 1;
        mhandler.sendMessage(message);
    }

    public String secToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public String unitFormat(long i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case 1:
                tvTime.setText((String) msg.obj);
                if (mRecorder.isRecording()) {
                    int progress = i % 5;
                    Log.d("tag", progress + "");
                    if (progress == 0) {
                        tvState.setText("->recording----->");
                    } else if (progress == 1) {
                        tvState.setText("-->recording---->");
                    } else if (progress == 2) {
                        tvState.setText("--->recording--->");
                    } else if (progress == 3) {
                        tvState.setText("---->recording-->");
                    } else if (progress == 4) {
                        tvState.setText("------>recording->");
                    }
                    i++;
                }
                break;
            case 2:
                tvState.setText("idle");
                break;
        }
        return false;
    }
}
