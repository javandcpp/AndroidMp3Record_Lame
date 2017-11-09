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
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;


public class MediaConfig {


    public static File getMediaRecordDestFile() {
        File dir = new File(Environment.getExternalStorageDirectory(), "media_dir");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, System.currentTimeMillis() + ".mp3");
    }

    public static File getMediaSourceDir() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "media_dir");
    }


    public static long getAvailableInternalMemorySize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = 0;
        long blockSizeLong = 0;

        if (Build.VERSION.SDK_INT >= 18) {
            availableBlocksLong = statFs.getAvailableBlocksLong();
            blockSizeLong = statFs.getBlockSizeLong();
        } else {
            statFs.getAvailableBlocks();
            statFs.getBlockSize();
        }
        return availableBlocksLong * blockSizeLong / 1024 / 1024;
    }


    /**
     * SDK22尝试录制检测权限
     *
     * @return
     */
    public static boolean isHasPermission() throws Exception {
        int sampleRateInHz = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        audioRecord.startRecording();
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        return true;
    }

}
