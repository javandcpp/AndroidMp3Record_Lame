<h1>android 录音mp3</h1>

<p>AudioRecord采集lame转码</p>

Use example：

```


Mp3Recorder mp3Recorder = new MP3Recorder.Builder()
              			.withSampleRate(48000)
              			.Quality(7)
                        .withBitRate(32)
                        .withPcmFormat(PCMFormat.PCM_16BIT)
                        .build();
                        
 or:
 
 
MP3Recorder mRecorder = new MP3Recorder();
mRecorder.setRecordFile(new File(Environment.getExternalStorageDirectory(), "audio.mp3"));
mRecorder.start();
                        
```




Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.javandoc:lame-library:v1.0'
	}








Copyright 2017 Huawque

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.