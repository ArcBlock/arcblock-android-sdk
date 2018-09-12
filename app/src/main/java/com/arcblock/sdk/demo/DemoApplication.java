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
package com.arcblock.sdk.demo;

import android.app.Application;

import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.CustomTypeValue;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.sdk.demo.btc.type.CustomType;
import com.facebook.stetho.Stetho;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class DemoApplication extends Application {

    public static DemoApplication INSTANCE = null;
    private ABCoreKitClient mABCoreClientBtc;
    private ABCoreKitClient mABCoreClientEth;
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static DemoApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        // set abcorekitclient is_debug
        ABCoreKitClient.IS_DEBUG = true;

        Stetho.initializeWithDefaults(this);
        Timber.plant(new Timber.DebugTree());

        initBtcClient();
        initEthClient();
    }

    private void initBtcClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.tag("ABCorekit-Okhttp-Btc").d(message);
            }
        });

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        CustomTypeAdapter dateCustomTypeAdapter = new CustomTypeAdapter<Date>() {
            @Override
            public Date decode(CustomTypeValue value) {
                try {
                    SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000000'Z'");
                    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//时区定义并进行时间获取
                    Date gpsUTCDate = utcFormat.parse(value.value.toString());
                    return gpsUTCDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public CustomTypeValue encode(Date value) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000000'Z'");
                return new CustomTypeValue.GraphQLString(sdf.format(value));
            }
        };

        mABCoreClientBtc = ABCoreKitClient.builder(this, CoreKitConfig.ApiType.API_TYPE_BTC)
                .addCustomTypeAdapter(CustomType.DATETIME, dateCustomTypeAdapter)
                .setOkHttpClient(okHttpClient)
                .setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
                .build();
    }

    private void initEthClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.tag("ABCorekit-Okhttp-Eth").d(message);
            }
        });

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        mABCoreClientEth = ABCoreKitClient.builder(this, CoreKitConfig.ApiType.API_TYPE_ETH)
                .setOkHttpClient(okHttpClient)
                .setOpenSocket(true)
                .setDefaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
                .build();
    }

    @NotNull
    public ABCoreKitClient abCoreKitClientBtc() {
        if (mABCoreClientBtc == null) {
            throw new RuntimeException("Please init corekit first.");
        }
        return mABCoreClientBtc;
    }

    @NotNull
    public ABCoreKitClient abCoreKitClientEth() {
        if (mABCoreClientEth == null) {
            throw new RuntimeException("Please init corekit first.");
        }
        return mABCoreClientEth;
    }
}
