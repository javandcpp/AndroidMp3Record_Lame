package com.guagua.mp3recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.guagua.mp3recorder.util.LameUtil;
import com.guagua.mp3recorder.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MP3Recorder {
    //=======================AudioRecord Default Settings=======================
    private int DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    /**
     * 以下三项为默认配置参数。Google Android文档明确表明只有以下3个参数是可以在所有设备上保证支持的。
     */
    private int DEFAULT_SAMPLING_RATE = 48000;//模拟器仅支持从麦克风输入8kHz采样率
    private int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 下面是对此的封装
     * private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
     */
    private PCMFormat DEFAULT_AUDIO_FORMAT = PCMFormat.PCM_16BIT;

    //======================Lame Default Settings=====================
    private int DEFAULT_LAME_MP3_QUALITY = 7;
    /**
     * 与DEFAULT_CHANNEL_CONFIG相关，因为是mono单声，所以是1
     */
    private int DEFAULT_LAME_IN_CHANNEL = 1;
    /**
     * Encoded bit rate. MP3 file will be encoded with bit rate 32kbps
     */
    private int DEFAULT_LAME_MP3_BIT_RATE = 32;

    //==================================================================

    /**
     * 自定义 每160帧作为一个周期，通知一下需要进行编码
     */
    private static final int FRAME_COUNT = 160;
    private RecordDecibelListener mRecordDecibelListener;
    private RecordTimeListener mRecordTimeListener;
    private AudioRecord mAudioRecord = null;
    private int mBufferSize;
    private short[] mPCMBuffer;
    private DataEncodeThread mEncodeThread;
    private boolean mIsRecording = false;
    private File mRecordFile;
    private TimerTask task;
    private Timer timer = new Timer();
    private boolean isPausing;

    /**
     * 录音开始时间值
     *
     * @param mRecordTimeListener
     */
    public void setmRecordTimeListener(RecordTimeListener mRecordTimeListener) {
        this.mRecordTimeListener = mRecordTimeListener;
    }

    /**
     *
     */
    public MP3Recorder() {

    }

    /**
     *
     * @param SampleRate
     * @param bitRate
     * @param quality
     * @param pcmFormat
     * @param mRecordFile
     * @param recordDecibelListener
     * @param recordTimeListener
     */
    private MP3Recorder(int SampleRate, int bitRate, int quality, PCMFormat pcmFormat,File mRecordFile,RecordDecibelListener recordDecibelListener,RecordTimeListener recordTimeListener) {
        this.DEFAULT_SAMPLING_RATE = SampleRate;
        this.DEFAULT_LAME_MP3_BIT_RATE = bitRate;
        this.DEFAULT_LAME_MP3_QUALITY = quality;
        this.DEFAULT_AUDIO_FORMAT = pcmFormat;
        this.mRecordFile=mRecordFile;
        this.mRecordDecibelListener=recordDecibelListener;
        this.mRecordTimeListener=recordTimeListener;
    }


    /**
     * Default constructor. Setup recorder with default sampling rate 1 channel,
     * 16 bits pcm
     *
     * @param recordFile target file
     */
    public MP3Recorder(File recordFile) {
        mRecordFile = recordFile;
    }

    /**
     * 目标文件,与分贝回调
     *
     * @param recordFile
     * @param recordDecibelListener
     */
    public MP3Recorder(File recordFile, RecordDecibelListener recordDecibelListener) {
        this.mRecordFile = recordFile;
        this.mRecordDecibelListener = recordDecibelListener;
    }

    /**
     * 设置写入目标文件
     *
     * @param recordFile
     */
    public void setRecordFile(File recordFile) {
        this.mRecordFile = recordFile;
    }

    /**
     * 录音分贝值回调
     *
     * @param recordDecibelListener
     */
    public void setRecordDecibelListener(RecordDecibelListener recordDecibelListener) {
        this.mRecordDecibelListener = recordDecibelListener;
    }

    /**
     * 录音暂停
     */
    public void pause() {
        isPausing = true;
    }


    /**
     * 从暂停状态,恢复继续录音
     */
    public void resume() {
        isPausing = false;
    }

    /**
     * Start recording. Create an encoding thread. Start record from this
     * thread.
     *
     * @throws IOException initAudioRecorder throws
     */
    public void start() throws IOException {
        if (mIsRecording) {
            return;
        }
        mIsRecording = true; // 提早，防止init或startRecording被多次调用
        initAudioRecorder();
        mAudioRecord.startRecording();
        new Thread() {
            @Override
            public void run() {
                //设置线程权限
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                while (mIsRecording) {
                    int readSize = mAudioRecord.read(mPCMBuffer, 0, mBufferSize);
                    if (isPausing) {
                        LogUtil.LOG_D("recorder", "pause");
                        continue;
                    }
                    LogUtil.LOG_D("recorder", "readSize:" + readSize);
                    if (readSize > 0) {
                        mEncodeThread.addTask(mPCMBuffer, readSize);
                        calculateRealVolume(mPCMBuffer, readSize);
                    }
                }
                // release and finalize audioRecord
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
                // stop the encoding thread and try to wait
                // until the thread finishes its job
                mEncodeThread.sendStopMessage();
            }

            /**
             * 此计算方法来自samsung开发范例
             *
             * @param buffer buffer
             * @param readSize readSize
             */
            private void calculateRealVolume(short[] buffer, int readSize) {
                double sum = 0;
                for (int i = 0; i < readSize; i++) {
                    // 这里没有做运算的优化，为了更加清晰的展示代码
                    sum += buffer[i] * buffer[i];
                }
                if (readSize > 0) {
                    double amplitude = sum / readSize;
                    mVolume = (int) Math.sqrt(amplitude);
                    double decibelValue = 10 * Math.log10(mVolume);
                    if (null != mRecordDecibelListener) {
                        mRecordDecibelListener.decibelValueCallback(decibelValue);//分贝回调
                    }
                }
            }
        }.start();
    }

    private int mVolume;

    /**
     * 获取真实的音量。 [算法来自三星]
     *
     * @return 真实音量
     */
    public int getRealVolume() {
        return mVolume;
    }

    /**
     * 获取相对音量。 超过最大值时取最大值。
     *
     * @return 音量
     */
    public int getVolume() {
        if (mVolume >= MAX_VOLUME) {
            return MAX_VOLUME;
        }
        return mVolume;
    }

    private static final int MAX_VOLUME = 2000;


    /**
     * 根据资料假定的最大值。 实测时有时超过此值。
     *
     * @return 最大音量值。
     */
    public int getMaxVolume() {
        return MAX_VOLUME;
    }

    public void stop() {
        mIsRecording = false;
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public boolean isPausing() {
        return isPausing;
    }

    /**
     * Initialize audio recorder
     */
    private void initAudioRecorder() throws IOException {
        mBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLING_RATE,
                DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat());

        int bytesPerFrame = DEFAULT_AUDIO_FORMAT.getBytesPerFrame();
        /* Get number of samples. Calculate the buffer size
         * (round up to the factor of given frame size)
		 * 使能被整除，方便下面的周期性通知
		 * */
        int frameSize = mBufferSize / bytesPerFrame;
        if (frameSize % FRAME_COUNT != 0) {
            frameSize += (FRAME_COUNT - frameSize % FRAME_COUNT);
            mBufferSize = frameSize * bytesPerFrame;
        }

		/* Setup audio recorder */
        mAudioRecord = new AudioRecord(DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT.getAudioFormat(),
                mBufferSize);

        mPCMBuffer = new short[mBufferSize];
        /*
         * Initialize lame buffer
		 * mp3 sampling rate is the same as the recorded pcm sampling rate 
		 * The bit rate is 32kbps
		 * 
		 */
        LameUtil.init(DEFAULT_SAMPLING_RATE, DEFAULT_LAME_IN_CHANNEL, DEFAULT_SAMPLING_RATE, DEFAULT_LAME_MP3_BIT_RATE, DEFAULT_LAME_MP3_QUALITY);
        // Create and run thread used to encode data
        // The thread will
        final long[] i = {0};
        task = new TimerTask() {
            @Override
            public void run() {
                if (null != mRecordTimeListener) {
                    ++i[0];
                    mRecordTimeListener.timeCallback(i[0] * 1000);
                }
            }
        };
        mEncodeThread = new DataEncodeThread(timer, task, mRecordFile, mBufferSize);
        mEncodeThread.start();
        mAudioRecord.setRecordPositionUpdateListener(mEncodeThread, mEncodeThread.getHandler());
        mAudioRecord.setPositionNotificationPeriod(FRAME_COUNT);

    }

    public interface RecordDecibelListener {
        void decibelValueCallback(double decibelValueCallback);
    }

    public interface RecordTimeListener {
        void timeCallback(long startTime);
    }


    /**
     * Builder
     */
    public static class Builder {
        //sample rate
        private int SampleRate = 48000;
        //bit rate
        private int bitRate = 32;
        //channels
        private int Channels = 2;
        //bit
        private PCMFormat pcmformat;
        //0(high) - 9(low)
        private int Quality = 7;
        private RecordTimeListener mTimeListener;
        private RecordDecibelListener mDecibelValueListener;
        private File mFile;

        /**
         * 采样率(44100,48000...)
         *
         * @param sampleRate
         * @return
         */
        public Builder withSampleRate(int sampleRate) {
            this.SampleRate = sampleRate;
            return this;
        }

        /**
         * 比特率 (32 64 96...)
         *
         * @param bitRate
         * @return
         */
        public Builder withBitRate(int bitRate) {
            this.bitRate = bitRate;
            return this;
        }

        /**
         * 声音质量[0(high)-9(low)]
         *
         * @param quality
         * @return
         */
        public Builder Quality(int quality) {
            this.Quality = quality;
            return this;
        }

        /**
         * PCM源数据位数
         *
         * @param pcmformat
         * @return
         */
        public Builder withPcmFormat(PCMFormat pcmformat) {
            this.pcmformat = pcmformat;
            return this;
        }

        public Builder withDestFile(File file){
            this.mFile=file;
            return this;
        }

        /**
         * 分贝值回调
         * @param listener
         * @return
         */
        public Builder withRecordDecibelListener(RecordDecibelListener listener){

            this.mDecibelValueListener=listener;
            return this;
        }

        /**
         * 录制时间监听
         * @param listener
         * @return
         */
        public Builder withRecordTimeListener(RecordTimeListener listener){
            this.mTimeListener=listener;
            return this;
        }
        /**
         * Build Mp3Record instance
         *
         * @return
         */
        public MP3Recorder build() {
            return new MP3Recorder(this.SampleRate, bitRate, Quality, pcmformat,mFile,mDecibelValueListener,mTimeListener);

        }


    }

}
