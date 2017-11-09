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

import android.widget.TextView;

/**
 * Created by android on 4/17/17.
 */

public class TimeFormatter {
    enum TimeFormat {
        NORMAL//00:00:00
        , DIGIT//0'0"
    }

    public static void updateTvRecordTime(long time, TextView tvRecordTime, TimeFormat mode) {

        long hour = 0;
        long minute = 0;
        long second = 0;

        if (time <= 0) {
            if (mode == TimeFormat.DIGIT) {
                tvRecordTime.setText("0\"");
            } else if (mode == TimeFormat.NORMAL) {
                tvRecordTime.setText("0:00");
            }
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                if (mode == TimeFormat.NORMAL) {
                    tvRecordTime.setText(unitFormatMin(minute, mode) + ":" + unitFormat(second, mode) );
                } else if (mode == TimeFormat.DIGIT) {
                    if (minute <= 0) {
                        tvRecordTime.setText(unitFormatMin(second, mode) + "\"");
                    } else {
                        tvRecordTime.setText(unitFormatMin(minute, mode) + "'" + unitFormat(second, mode) + "\"");

                    }
                }
            } else {
                hour = minute / 60;
                if (hour > 99)
                    tvRecordTime.setText("99:59:59/05:00");
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                if (mode == TimeFormat.DIGIT) {
                    tvRecordTime.setText(unitFormat(hour, mode) + "h" + unitFormat(minute, mode) + "'" + unitFormat(second, mode) + "\"");
                } else if (mode == TimeFormat.NORMAL) {
                    tvRecordTime.setText(unitFormat(hour, mode) + ":" + unitFormat(minute, mode) + ":" + unitFormat(second, mode));
                }
            }
        }
    }

    private static String unitFormatMin(long i, TimeFormat timeFormat) {
        String retStr = null;
//        if (i >= 0 && i < 10) {
//            if (timeFormat == TimeFormat.DIGIT) {
//                retStr = "" + i;
//            } else if (timeFormat == TimeFormat.NORMAL) {
//                retStr = "0" + i;
//            }
//        } else {
            retStr = "" + i;
//        }
        return retStr;
    }

    private static String unitFormat(long i, TimeFormat timeFormat) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            if (timeFormat == TimeFormat.DIGIT) {
                retStr = "" + i;
            } else if (timeFormat == TimeFormat.NORMAL) {
                retStr = "0" + i;
            }
        } else {
            retStr = "" + i;
        }
        return retStr;
    }

}
