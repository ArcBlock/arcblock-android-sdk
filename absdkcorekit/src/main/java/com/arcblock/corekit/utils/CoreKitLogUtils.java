/*
 * Copyright (c) 2017-present ArcBlock Foundation Ltd <https://www.arcblock.io/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.arcblock.corekit.utils;

import android.util.Log;

import com.arcblock.corekit.ABCoreKitClient;

public class CoreKitLogUtils {

    private static final String TAG = "CoreKitSocket";

    public static void v(String log) {
        if (ABCoreKitClient.IS_DEBUG) {
            Log.v(TAG, log);
        }
    }

    public static void d(String log) {
        if (ABCoreKitClient.IS_DEBUG) {
            Log.d(TAG, log);
        }
    }

    public static void i(String log) {
        if (ABCoreKitClient.IS_DEBUG) {
            Log.i(TAG, log);
        }
    }

    public static void w(String log) {
        if (ABCoreKitClient.IS_DEBUG) {
            Log.w(TAG, log);
        }
    }

    public static void e(String log) {
        if (ABCoreKitClient.IS_DEBUG) {
            Log.e(TAG, log);
        }
    }

}
