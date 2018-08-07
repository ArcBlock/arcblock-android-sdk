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
package com.arcblock.corekit.socket;

import com.apollographql.apollo.subscription.OperationClientMessage;
import com.apollographql.apollo.subscription.SubscriptionTransport;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;

import static com.apollographql.apollo.api.internal.Utils.checkNotNull;

public class CoreKitWSSubscriptionTransport implements SubscriptionTransport {
	private final SubscriptionTransport.Callback callback;

	private final String webSocketUrl;
	private final OkHttpClient okHttpClient;


	CoreKitWSSubscriptionTransport(String webSocketUrl, OkHttpClient okHttpClient,Callback callback) {
		this.webSocketUrl = webSocketUrl;
		this.okHttpClient = okHttpClient;
		this.callback = callback;
	}

	@Override
	public void connect() {

	}

	@Override
	public void disconnect(OperationClientMessage message) {

	}

	@Override
	public void send(OperationClientMessage message) {

	}


	public static final class Factory implements SubscriptionTransport.Factory {

		private final String webSocketUrl;
		private final OkHttpClient mOkHttpClient;

		public Factory(@NotNull String webSocketUrl, @NotNull OkHttpClient okHttpClient) {
			this.webSocketUrl = webSocketUrl;
			this.mOkHttpClient = okHttpClient;
		}

		@Override
		public SubscriptionTransport create(@NotNull Callback callback) {
			checkNotNull(callback, "callback == null");
			return new CoreKitWSSubscriptionTransport(webSocketUrl, mOkHttpClient, callback);
		}
	}
}
