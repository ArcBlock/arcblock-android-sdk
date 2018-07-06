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
package com.arcblock.corekit;

import android.content.Context;

import com.arcblock.corekit.data.db.DatabaseManager;
import com.facebook.stetho.Stetho;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class ABCoreKit {

	private static ABCoreKit INSTANCE = null;
	private ABCoreKitClient mABCoreClient;

	private ABCoreKit() {
	}

	public static ABCoreKit getInstance() {
		if (INSTANCE == null) {
			synchronized (ABCoreKit.class) {
				if (INSTANCE == null) {
					INSTANCE = new ABCoreKit();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * @param context
	 * @param isDebug
	 */
	public void init(Context context, final boolean isDebug) {
		DatabaseManager.getInstance().createDB(context);
		if (isDebug) {
			Stetho.initializeWithDefaults(context);
			Timber.plant(new Timber.DebugTree());
		}

		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
			@Override
			public void log(String message) {
				Timber.tag("ABCorekit-Okhttp").d(message);
			}
		});

		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.build();

		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		mABCoreClient = ABCoreKitClient.builder(context)
				.setOkHttpClient(okHttpClient)
				.build();
	}

	@NotNull
	public ABCoreKitClient abCoreKitClient() {
		if (mABCoreClient == null) {
			throw new RuntimeException("Please init corekit first.");
		}
		return mABCoreClient;
	}

}
