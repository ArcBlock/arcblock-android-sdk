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

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.CacheKeyResolver;
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy;
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper;
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory;
import com.arcblock.corekit.data.db.DatabaseManager;
import com.facebook.stetho.Stetho;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class ABCoreKit {

	private static final String BASE_URL = "http://ocap.arcblock.io/api/btc";
	//private static final String SUBSCRIPTION_BASE_URL = "wss://api.githunt.com/subscriptions";
	private static ABCoreKit INSTANCE = null;
	private static final String SQL_CACHE_NAME = "arcblock_core_kit_db";
	private ApolloClient apolloClient;

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
	public void init(Context context, boolean isDebug) {
		DatabaseManager.getInstance().createDB(context);
		if (isDebug) {
			Stetho.initializeWithDefaults(context);
		}
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
			@Override public void log(String message) {
				Timber.tag("ABCorekit-Okhttp").d(message);
			}
		});
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.build();
		ApolloSqlHelper apolloSqlHelper = new ApolloSqlHelper(context, SQL_CACHE_NAME);
		NormalizedCacheFactory normalizedCacheFactory = new LruNormalizedCacheFactory(EvictionPolicy.NO_EVICTION)
				.chain(new SqlNormalizedCacheFactory(apolloSqlHelper));
		CacheKeyResolver cacheKeyResolver = new CacheKeyResolver() {
			@NotNull
			@Override
			public CacheKey fromFieldRecordSet(@NotNull ResponseField field, @NotNull Map<String, Object> recordSet) {
				String typeName = (String) recordSet.get("__typename");
				if ("User".equals(typeName)) {
					String userKey = typeName + "." + recordSet.get("login");
					return CacheKey.from(userKey);
				}
				if (recordSet.containsKey("id")) {
					String typeNameAndIDKey = recordSet.get("__typename") + "." + recordSet.get("id");
					return CacheKey.from(typeNameAndIDKey);
				}
				return CacheKey.NO_KEY;
			}
			// Use this resolver to customize the key for fields with variables: eg entry(repoFullName: $repoFullName).
			// This is useful if you want to make query to be able to resolved, even if it has never been run before.
			@NotNull
			@Override
			public CacheKey fromFieldArguments(@NotNull ResponseField field, @NotNull Operation.Variables variables) {
				return CacheKey.NO_KEY;
			}
		};
		apolloClient = ApolloClient.builder()
				.serverUrl(BASE_URL)
				.okHttpClient(okHttpClient)
				.normalizedCache(normalizedCacheFactory, cacheKeyResolver)
				//.subscriptionTransportFactory(new WebSocketSubscriptionTransport.Factory(SUBSCRIPTION_BASE_URL, okHttpClient))
				.build();
	}

	@NotNull
	public ApolloClient apolloClient() {
		if (apolloClient == null) {
			throw new RuntimeException("Please init corekit first.");
		}
		return apolloClient;
	}

}
