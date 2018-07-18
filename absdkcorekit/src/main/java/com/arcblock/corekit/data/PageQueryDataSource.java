package com.arcblock.corekit.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBeanMapper;
import com.arcblock.corekit.bean.PageData;
import com.arcblock.corekit.bean.PageInput;
import com.arcblock.corekit.bean.PageQuery;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PageQueryDataSource<T, D extends PageData<K>, K> extends PageKeyedDataSource<String, K> {

	private ABCoreKitClient mABCoreKitClient;
	private PageQuery mQuery;
	private CoreKitBeanMapper<Response<T>, D> mCoreKitBeanMapper;
	public final MutableLiveData loadState;

	public PageQueryDataSource(ABCoreKitClient aBCoreKitClient, PageQuery query, CoreKitBeanMapper coreKitBeanMapper) {
		mABCoreKitClient = aBCoreKitClient;
		mQuery = query;
		mCoreKitBeanMapper = coreKitBeanMapper;
		loadState = new MutableLiveData<DataLoadState>();
	}

	@Override
	public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, K> callback) {
		loadState.postValue(DataLoadState.LOADING);
		mQuery.setPageInput(Input.optional(null));
		Rx2Apollo.from(mABCoreKitClient.query(mQuery))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Response<T>>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Response<T> t) {
						Log.e("onNext",PageQueryDataSource.this.toString());
						if (t != null) {
							D temp = mCoreKitBeanMapper.map(t);
							if (temp != null) {
								if (!t.fromCache()) {
									if (temp.getPage() != null && temp.getPage().isNext()) {
										callback.onResult(temp.data, null, temp.getPage().getCursor());
										loadState.postValue(DataLoadState.LOADED);
									} else {
										callback.onResult(temp.data, null, null);
										loadState.postValue(DataLoadState.ALL_LOADED);
									}
								} else {
									callback.onResult(temp.data, null, null);
									loadState.postValue(DataLoadState.LOADED);
								}
							} else {
								callback.onResult(null, null, null);
								loadState.postValue(DataLoadState.LOADED);
							}
						} else {
							callback.onResult(null, null, null);
							loadState.postValue(DataLoadState.LOADED);
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

	@Override
	public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, K> callback) {
		// ignored, since we only ever append to our initial load
	}

	@Override
	public void loadAfter(@NonNull LoadParams<String> params, @NonNull final LoadCallback<String, K> callback) {
		mQuery.setPageInput(Input.optional(PageInput.builder().cursor(params.key).build()));
		Rx2Apollo.from(mABCoreKitClient.query(mQuery))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Response<T>>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public synchronized void onNext(Response<T> t) {
						Log.e("onNext",PageQueryDataSource.this.toString());
						if (t != null) {
							D temp = mCoreKitBeanMapper.map(t);
							if (temp != null) {
								if (!t.fromCache()) {
									if (temp.getPage() != null && temp.getPage().isNext()) {
										callback.onResult(temp.data, temp.getPage().getCursor());
										loadState.postValue(DataLoadState.LOADED);
									} else {
										callback.onResult(temp.data, null);
										loadState.postValue(DataLoadState.ALL_LOADED);
									}
								} else {
									callback.onResult(temp.data, null);
									loadState.postValue(DataLoadState.LOADED);
								}
							} else {
								callback.onResult(null, null);
								loadState.postValue(DataLoadState.LOADED);
							}
						} else {
							callback.onResult(null, null);
							loadState.postValue(DataLoadState.LOADED);
						}
					}

					@Override
					public void onError(Throwable e) {
						loadState.postValue(DataLoadState.FAILED);
					}

					@Override
					public void onComplete() {

					}
				});
	}
}
