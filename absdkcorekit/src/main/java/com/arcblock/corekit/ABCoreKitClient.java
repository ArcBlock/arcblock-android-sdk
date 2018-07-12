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
import com.apollographql.apollo.ApolloMutationCall;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ScalarType;
import com.apollographql.apollo.api.Subscription;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.CacheKeyResolver;
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper;
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory;
import com.apollographql.apollo.fetcher.ResponseFetcher;
import com.apollographql.apollo.response.CustomTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;

import static com.apollographql.apollo.fetcher.ApolloResponseFetchers.CACHE_FIRST;

public class ABCoreKitClient {
	private ApolloClient mApolloClient;
	private static final String SQL_CACHE_NAME = "arcblock_core_kit_db";
	private static final String BASE_URL = "https://ocap.arcblock.io/api/btc";
	//private static final String SUBSCRIPTION_BASE_URL = "wss://api.githunt.com/subscriptions";

	private ABCoreKitClient(Builder builder) {

		OkHttpClient.Builder okHttpClientBuilder;
		if (builder.mOkHttpClient == null) {
			okHttpClientBuilder = new OkHttpClient.Builder();
		} else {
			okHttpClientBuilder = builder.mOkHttpClient.newBuilder();
		}

		OkHttpClient okHttpClient = okHttpClientBuilder.build();

		ApolloClient.Builder apolloClientBuilder = ApolloClient.builder()
				.serverUrl(BASE_URL)
				.okHttpClient(okHttpClient)
				.normalizedCache(builder.mNormalizedCacheFactory, builder.mResolver);

		for (ScalarType scalarType : builder.customTypeAdapters.keySet()) {
			apolloClientBuilder.addCustomTypeAdapter(scalarType, builder.customTypeAdapters.get(scalarType));
		}

		if (builder.mDispatcher != null) {
			apolloClientBuilder.dispatcher(builder.mDispatcher);
		}

		if (builder.mDefaultResponseFetcher != null) {
			apolloClientBuilder.defaultResponseFetcher(builder.mDefaultResponseFetcher);
		}

		mApolloClient = apolloClientBuilder.build();

	}

	public static class Builder {

		NormalizedCacheFactory mNormalizedCacheFactory;
		CacheKeyResolver mResolver;
		final Map<ScalarType, CustomTypeAdapter> customTypeAdapters = new LinkedHashMap<>();
		Executor mDispatcher;
		OkHttpClient mOkHttpClient;
		ResponseFetcher mDefaultResponseFetcher = CACHE_FIRST;
		Context mContext;

		private Builder(Context context) {
			this.mContext = context;
		}

		public Builder setNormalizedCacheFactory(NormalizedCacheFactory normalizedCacheFactory) {
			mNormalizedCacheFactory = normalizedCacheFactory;
			return this;
		}

		public Builder setResolver(CacheKeyResolver resolver) {
			mResolver = resolver;
			return this;
		}

		public Builder setDispatcher(Executor dispatcher) {
			mDispatcher = dispatcher;
			return this;
		}

		public Builder setOkHttpClient(OkHttpClient okHttpClient) {
			mOkHttpClient = okHttpClient;
			return this;
		}

		public Builder setDefaultResponseFetcher(ResponseFetcher defaultResponseFetcher) {
			mDefaultResponseFetcher = defaultResponseFetcher;
			return this;
		}

		public <T> Builder addCustomTypeAdapter(@NotNull ScalarType scalarType,
															 @NotNull final CustomTypeAdapter<T> customTypeAdapter) {
			customTypeAdapters.put(scalarType, customTypeAdapter);
			return this;
		}

		public ABCoreKitClient build() {
			if (mNormalizedCacheFactory == null) {
				ApolloSqlHelper appSyncSqlHelper = ApolloSqlHelper.create(mContext, SQL_CACHE_NAME);
				mNormalizedCacheFactory = new SqlNormalizedCacheFactory(appSyncSqlHelper);
			}

			if (mResolver == null) {
				mResolver = new CacheKeyResolver() {
					@Nonnull
					@Override
					public CacheKey fromFieldRecordSet(@Nonnull ResponseField field, @Nonnull Map<String, Object> recordSet) {
						return formatCacheKey((String) recordSet.get("id"));
					}

					@Nonnull
					@Override
					public CacheKey fromFieldArguments(@Nonnull ResponseField field, @Nonnull Operation.Variables variables) {

						return formatCacheKey((String) field.resolveArgument("id", variables));
					}

					private CacheKey formatCacheKey(String id) {
						if (id == null || id.isEmpty()) {
							return CacheKey.NO_KEY;
						} else {
							return CacheKey.from(id);
						}
					}
				};
			}
			return new ABCoreKitClient(this);
		}
	}

	public static Builder builder(Context context) {
		return new Builder(context);
	}

	public <D extends Query.Data, T, V extends Query.Variables> ApolloQueryCall<T> query(@Nonnull Query<D, T, V> query) {
		return mApolloClient.query(query);
	}

	public <D extends Mutation.Data, T, V extends Mutation.Variables> ApolloMutationCall<T> mutate(@Nonnull Mutation<D, T, V> mutation) {
		return mApolloClient.mutate(mutation);
	}

	public <D extends Mutation.Data, T, V extends Mutation.Variables> ApolloMutationCall<T> mutate(@Nonnull Mutation<D, T, V> mutation, @Nonnull D withOptimisticUpdates) {
		return mApolloClient.mutate(mutation, withOptimisticUpdates);
	}

	public <D extends Subscription.Data, T, V extends Subscription.Variables> ApolloSubscriptionCall<T> subscribe(@Nonnull Subscription<D, T, V> subscription) {
		return mApolloClient.subscribe(subscription);
	}
}
