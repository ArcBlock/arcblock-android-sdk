package com.arcblock.sdk.demo;

import android.app.Application;

import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.CustomTypeValue;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.sdk.demo.type.CustomType;
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
	private ABCoreKitClient mABCoreClient;
	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static DemoApplication getInstance() {
		return INSTANCE;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		INSTANCE = this;

		Stetho.initializeWithDefaults(this);
		Timber.plant(new Timber.DebugTree());

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

		CustomTypeAdapter dateCustomTypeAdapter = new CustomTypeAdapter<Date>() {
			@Override
			public Date decode(CustomTypeValue value) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000000'Z'");
					TimeZone utcZone = TimeZone.getTimeZone("UTC");
					sdf.setTimeZone(utcZone);
					return sdf.parse(value.value.toString());
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public CustomTypeValue encode(Date value) {
				return new CustomTypeValue.GraphQLString(DATE_FORMAT.format(value));
			}
		};

		mABCoreClient = ABCoreKitClient.builder(this)
				.addCustomTypeAdapter(CustomType.DATETIME, dateCustomTypeAdapter)
				.setOkHttpClient(okHttpClient)
				.setDefaultResponseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
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
