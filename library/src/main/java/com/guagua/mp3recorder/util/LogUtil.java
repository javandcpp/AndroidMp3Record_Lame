package com.guagua.mp3recorder.util;

import android.util.Log;

/**
 * Created by android on 3/29/17.
 */

public class LogUtil {
    public static void LOG_D(String tag,String message){
        if (LameUtil.getDebug()) {
            Log.d(tag, message);
        }
    }
}
