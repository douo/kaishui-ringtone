/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.dourok.kaishuiringtone;


import android.content.Intent;
import android.os.Bundle;
import java.util.Iterator;
import java.util.Set;

/**
 * Helper methods that make logging more consistent throughout the app.
 */
public class LogUtils {

    private LogUtils() {
    }


	public static String dumpIntent(Intent i) {
		StringBuilder sb = new StringBuilder();
		if (i == null) {
			sb.append(i);
		} else {
			Bundle bundle = i.getExtras();
			if (bundle != null) {
				Set<String> keys = bundle.keySet();
				Iterator<String> it = keys.iterator();
				sb.append("Dumping Intent start");
				sb.append("\n");
				sb.append("Data:" + i.getData());
				sb.append("\n");
				while (it.hasNext()) {
					String key = it.next();
					sb.append("[" + key + "=" + bundle.get(key) + "]");
					sb.append("\n");
				}
				sb.append("Dumping Intent end");
			}
		}
		return sb.toString();

	}

	public static String getMethodName() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

		StackTraceElement e = stacktrace[3];
		String methodName = e.getMethodName();
		return methodName;
	}
}
