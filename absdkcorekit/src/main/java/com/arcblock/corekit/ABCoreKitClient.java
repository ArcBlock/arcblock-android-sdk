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
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.apollographql.apollo.fetcher.ResponseFetcher;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.ScalarTypeAdapters;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.corekit.socket.CoreKitSocket;
import com.arcblock.corekit.socket.CoreKitSocketStatusCallBack;
import com.arcblock.corekit.socket.IErrorCallback;
import com.arcblock.corekit.socket.ISocketCloseCallback;
import com.arcblock.corekit.socket.ISocketOpenCallback;
import com.arcblock.corekit.utils.CoreKitCommonUtils;
import com.arcblock.corekit.utils.CoreKitLogUtils;
import com.blankj.utilcode.util.MetaDataUtils;
import com.blankj.utilcode.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import timber.log.Timber;

import static com.apollographql.apollo.fetcher.ApolloResponseFetchers.CACHE_FIRST;

public class ABCoreKitClient {

    public static boolean IS_DEBUG = false;
    private ApolloClient mApolloClient;
    private static final String SQL_CACHE_NAME = "arcblock_core_kit_db";
    private OkHttpClient mOkHttpClient;
    private CoreKitSocket mCoreKitSocket;
    private ScalarTypeAdapters scalarTypeAdapters;
    private List<CoreKitSocketStatusCallBack> mCoreKitSocketStatusCallBacks = new ArrayList<>();

    private ABCoreKitClient(Builder builder) {
        OkHttpClient.Builder okHttpClientBuilder;
        if (builder.mOkHttpClient == null) {
            okHttpClientBuilder = new OkHttpClient.Builder();
        } else {
            okHttpClientBuilder = builder.mOkHttpClient.newBuilder();
        }

        if (builder.enableHMAC) {
            final String accessKey = MetaDataUtils.getMetaDataInApp("ArcBlock_Access_Key");
            final String accessSecret = MetaDataUtils.getMetaDataInApp("ArcBlock_Access_Secret");
            if (!TextUtils.isEmpty(accessKey) && !TextUtils.isEmpty(accessSecret)) {
                okHttpClientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        RequestBody requestBody = request.body();
                        Buffer buffer = new Buffer();
                        requestBody.writeTo(buffer);
                        String oldParamsJson = buffer.readUtf8();

                        HashMap<String, Object> rootMap = JSON.parseObject(oldParamsJson, HashMap.class);  //原始参数

                        if (rootMap != null && rootMap.containsKey("query") && rootMap.containsKey("variables") && rootMap.containsKey("operationName")) {
                            String query = (String) rootMap.get("query");
                            String operationName = (String) rootMap.get("operationName");

                            // use fastjson for sort the json object by field names
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("query", query);
                            jsonObject.put("variables", rootMap.get("variables"));
                            jsonObject.put("operationName", operationName);

                            String expectStr = URLEncoder.encode(jsonObject.toJSONString(), "UTF-8")
                                    .replaceAll("\\+", "%20")
                                    .replaceAll("%21", "!")
                                    .replaceAll("%28", "(")
                                    .replaceAll("%29", ")");

                            long timestamp = System.currentTimeMillis() / 1000;
                            String sigInput = "accessKey=" + accessKey + "&query=" + expectStr + "&timestamp=" + timestamp;
                            String signature = CoreKitCommonUtils.sha256HMACAndBase64(sigInput, accessSecret);
                            request = request.newBuilder().header("Authorization", "AB1-HMAC-SHA256 access_key=" + accessKey + ",timestamp=" + timestamp + ",signature=" + signature)
                                    .build();

                        }
                        return chain.proceed(request);
                    }
                });
            } else {
                CoreKitLogUtils.e("Please set the ArcBlock_Access_Key and ArcBlock_Access_Secret correctly.");
            }
        }

        if (builder.openOkHttpLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Timber.tag("ABCorekitClient-OkHttp").d(message);
                }
            });
            mOkHttpClient = okHttpClientBuilder.addInterceptor(loggingInterceptor).build();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            mOkHttpClient = okHttpClientBuilder.build();
        }
        if (builder.openSocket) {
            initCoreKitSocket(builder);
        }

        String serverUrl = builder.apiType == CoreKitConfig.ApiType.API_TYPE_CUSTOM ? builder.serverUrl : CoreKitConfig.getApiUrl(builder.apiType);

        if (TextUtils.isEmpty(serverUrl)) {
            throw new RuntimeException("Please set the server url correct.");
        }

        ApolloClient.Builder apolloClientBuilder = ApolloClient.builder()
                .serverUrl(serverUrl)
                .okHttpClient(mOkHttpClient)
                .normalizedCache(builder.mNormalizedCacheFactory, builder.mResolver);
        for (ScalarType scalarType : builder.customTypeAdapters.keySet()) {
            apolloClientBuilder.addCustomTypeAdapter(scalarType, builder.customTypeAdapters.get(scalarType));
        }

        scalarTypeAdapters = new ScalarTypeAdapters(builder.customTypeAdapters);

        if (builder.mDispatcher != null) {
            apolloClientBuilder.dispatcher(builder.mDispatcher);
        }
        if (builder.mDefaultResponseFetcher != null) {
            apolloClientBuilder.defaultResponseFetcher(builder.mDefaultResponseFetcher);
        }
        mApolloClient = apolloClientBuilder.build();
    }

    public ScalarTypeAdapters getScalarTypeAdapters() {
        return scalarTypeAdapters;
    }

    public void addSocketStatusCallBack(CoreKitSocketStatusCallBack socketStatusCallBack) {
        if (socketStatusCallBack != null && mCoreKitSocketStatusCallBacks.indexOf(socketStatusCallBack) == -1) {
            mCoreKitSocketStatusCallBacks.add(socketStatusCallBack);
        }
    }

    public void doManualReconnect() {
        if (mCoreKitSocket != null && !mCoreKitSocket.isConnected() && !mCoreKitSocket.isOpening()) {
            mCoreKitSocket.manualReconnect();
        }
    }

    public static class Builder {

        private NormalizedCacheFactory mNormalizedCacheFactory;
        private CacheKeyResolver mResolver;
        private Map<ScalarType, CustomTypeAdapter> customTypeAdapters = new LinkedHashMap<>();
        private Executor mDispatcher;
        private OkHttpClient mOkHttpClient;
        private ResponseFetcher mDefaultResponseFetcher = CACHE_FIRST;
        private Context mContext;
        private String dbName;
        private CoreKitConfig.ApiType apiType;
        private boolean openSocket;
        private boolean openOkHttpLog;
        private String serverUrl;
        private String subscriptionServerUrl;
        private boolean enableHMAC;

        private Builder(Context context, CoreKitConfig.ApiType apiType) {
            this.mContext = context;
            Utils.init(context);
            this.apiType = apiType;
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

        public Builder setDbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public Builder setOpenSocket(boolean openSocket) {
            this.openSocket = openSocket;
            return this;
        }

        public Builder setOpenOkHttpLog(boolean openOkHttpLog) {
            this.openOkHttpLog = openOkHttpLog;
            return this;
        }

        public Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public Builder setSubscriptionServerUrl(String subscriptionServerUrl) {
            this.subscriptionServerUrl = subscriptionServerUrl;
            return this;
        }

        public <T> Builder addCustomTypeAdapter(@NotNull ScalarType scalarType,
                                                @NotNull final CustomTypeAdapter<T> customTypeAdapter) {
            customTypeAdapters.put(scalarType, customTypeAdapter);
            return this;
        }

        public Builder setEnableHMAC(boolean enableHMAC) {
            this.enableHMAC = enableHMAC;
            return this;
        }

        public ABCoreKitClient build() {
            if (mNormalizedCacheFactory == null) {
                ApolloSqlHelper appSyncSqlHelper = ApolloSqlHelper.create(mContext, TextUtils.isEmpty(dbName) ? SQL_CACHE_NAME : dbName);
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
                        return formatCacheKey((String) field.resolveArgument("cursor", variables));
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

    public static Builder builder(Context context, CoreKitConfig.ApiType apiType) {
        return new Builder(context, apiType);
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

    private void initCoreKitSocket(Builder builder) {
        CoreKitLogUtils.e("initCoreKitSocket=>" + Thread.currentThread().getName());

        String subscriptionUrl = builder.apiType == CoreKitConfig.ApiType.API_TYPE_CUSTOM ? builder.subscriptionServerUrl : CoreKitConfig.getSubUrl(builder.apiType);

        if (TextUtils.isEmpty(subscriptionUrl)) {
            throw new RuntimeException("Please set the subscription url correct.");
        }

        if (mCoreKitSocket == null) {
            // sub url set by apiType
            mCoreKitSocket = new CoreKitSocket(subscriptionUrl, mOkHttpClient);
        }

        synchronized (this) {
            if (!mCoreKitSocket.isConnected() && !mCoreKitSocket.isOpening()) {
                mCoreKitSocket.setOpening(true);
                mCoreKitSocket.onOpen(new ISocketOpenCallback() {
                    @Override
                    public void onOpen() {
                        Observable.just(1)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Integer integer) {
                                        for (CoreKitSocketStatusCallBack callBack : mCoreKitSocketStatusCallBacks) {
                                            callBack.onOpen();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }
                }).onClose(new ISocketCloseCallback() {
                    @Override
                    public void onClose() {
                        Observable.just(1)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Integer integer) {
                                        for (CoreKitSocketStatusCallBack callBack : mCoreKitSocketStatusCallBacks) {
                                            callBack.onClose();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }).onError(new IErrorCallback() {
                    @Override
                    public void onError(final String reason) {
                        Observable.just(1)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Integer integer) {
                                        for (CoreKitSocketStatusCallBack callBack : mCoreKitSocketStatusCallBacks) {
                                            callBack.onError();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }).connect();
            }
        }
    }

    public CoreKitSocket getCoreKitSocket() {
        if (mCoreKitSocket == null) {
            throw new RuntimeException("The mCoreKitSocket can not be null.");
        }
        return mCoreKitSocket;
    }

    public static ABCoreKitClient mABCoreKitClientEth;
    public static ABCoreKitClient mABCoreKitClientBtc;

    public static ABCoreKitClient defaultInstance(Context context, CoreKitConfig.ApiType apiType) {
        if (apiType == CoreKitConfig.ApiType.API_TYPE_BTC) {
            if (mABCoreKitClientBtc == null) {
                mABCoreKitClientBtc = ABCoreKitClient.builder(context, CoreKitConfig.ApiType.API_TYPE_BTC).setOpenOkHttpLog(true).setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK).build();
            }
            return mABCoreKitClientBtc;
        } else {
            if (mABCoreKitClientEth == null) {
                mABCoreKitClientEth = ABCoreKitClient.builder(context, CoreKitConfig.ApiType.API_TYPE_ETH).setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK).build();
            }
            return mABCoreKitClientEth;
        }
    }


}
