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

import com.guagua.mp3recorder.MP3Recorder;
import com.guagua.mp3recorder.PCMFormat;
import com.guagua.mp3recorder.util.LameUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by swordman on 4/1/17.
 * PCM录制实时转码MP3
 */
public class RecorderToMp3 {


    private static volatile RecorderToMp3 instance = null;
    private final MP3Recorder mp3Recorder;

    private RecorderToMp3() {
        mp3Recorder = new MP3Recorder.Builder()
                        .withSampleRate(48000)
                        .Quality(7)
                        .withBitRate(32)
                        .withPcmFormat(PCMFormat.PCM_16BIT)
                        .build();
    }

    public static RecorderToMp3 getInstance() {
        if (instance == null) {
            instance = new RecorderToMp3();
        }
        return instance;
    }

    /**
     * 指定文件录制MP3
     *
     * @param file
     * @throws IOException
     */
    public void startRecord(File file) throws Exception {
        if (null == file) {
            throw new IOException("file is null");
        }
        mp3Recorder.setRecordFile(file);
        mp3Recorder.start();
    }

    /**
     * 停止MP3录制
     */
    public void stopRecord() {
        mp3Recorder.stop();
    }

    /**
     * 录制写入文件完成回调
     *
     * @param lameWriteFinishCall
     */
    public void setRecorderWriteCompleteListener(LameUtil.LameWriteFinishCall lameWriteFinishCall) {
        LameUtil.setLameCallback(lameWriteFinishCall);
    }

    /**
     * 日志打印
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        LameUtil.setDebug(debug);
    }

    /**
     * 分贝值回调
     *
     * @param recordDecibelListener
     */
    public void setDecibelListener(MP3Recorder.RecordDecibelListener recordDecibelListener) {
        mp3Recorder.setRecordDecibelListener(recordDecibelListener);
    }

    /**
     * 录制时间回调
     *
     * @param recordTimeListener
     */
    public void setTimeListener(MP3Recorder.RecordTimeListener recordTimeListener) {
        mp3Recorder.setmRecordTimeListener(recordTimeListener);
    }

    public boolean isRecording() {
        return mp3Recorder.isRecording();
    }


}
