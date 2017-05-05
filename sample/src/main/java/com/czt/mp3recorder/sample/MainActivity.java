
package com.czt.mp3recorder.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.guagua.mp3recorder.MP3Recorder;
import com.guagua.mp3recorder.util.LameUtil;

import java.io.File;
import java.io.IOException;


public class MainActivity extends Activity implements LameUtil.LameWriteFinishCall, MP3Recorder.RecordDecibelListener, MP3Recorder.RecordTimeListener {



	private MP3Recorder mRecorder = new MP3Recorder();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button startButton = (Button) findViewById(R.id.StartButton);
		LameUtil.setLameCallback(this);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mRecorder.setRecordFile(new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+"---audio.mp3"));
					mRecorder.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		Button stopButton = (Button) findViewById(R.id.StopButton);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecorder.stop();
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
		Log.d("activity","lame write callback:"+",fileName:"+fileName);
	}

	@Override
	public void lameWriteCallBack(boolean status) {
		Log.d("activity","lame write callback:"+status);
	}

	@Override
	public void decibelValueCallback(double decibelValueCallback) {
		Log.d("activity","decibel value:"+decibelValueCallback);
	}

	@Override
	public void timeCallback(long startTime) {
		Log.d("time","total mills:"+secToTime(startTime/1000));
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
}
