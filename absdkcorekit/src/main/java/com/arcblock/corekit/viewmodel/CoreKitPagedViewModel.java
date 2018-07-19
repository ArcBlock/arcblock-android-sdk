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
package com.arcblock.corekit.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.bean.CoreKitBeanMapper;
import com.arcblock.corekit.bean.CoreKitPagedBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoreKitPagedViewModel<T, K> extends ViewModel {

	private ABCoreKitClient mABCoreKitClient;
	private MutableLiveData<CoreKitPagedBean<List<K>>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();
	private MutableLiveData<CoreKitPagedBean<List<K>>> mCleanDatasMutableLiveData = new MutableLiveData<>();
	private CoreKitBeanMapper<Response<T>, List<K>> mCoreKitBeanMapper;
	private boolean isRefresh;
	private boolean isLoadMore;
	private List<K> resultDatas = new ArrayList<>();

	public CoreKitPagedViewModel(CoreKitBeanMapper<Response<T>, List<K>> mapper, Context context) {
		this.mCoreKitBeanMapper = mapper;
		this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context);

	}

	public CoreKitPagedViewModel(CoreKitBeanMapper<Response<T>, List<K>> mapper, ABCoreKitClient aBCoreKitClient) {
		this.mCoreKitBeanMapper = mapper;
		this.mABCoreKitClient = aBCoreKitClient;
	}

	/**
	 * @param query
	 * @return a livedata object with D
	 */
	public MutableLiveData<CoreKitPagedBean<List<K>>> getQueryDatas(Query query) {
		doQuery(query);
		return mCoreKitBeanMutableLiveData;
	}

	/**
	 * @param query
	 * @return a livedata object with D
	 */
	public MutableLiveData<CoreKitPagedBean<List<K>>> getQueryCleanDatas(Query query) {
		doQuery(query);
		return mCleanDatasMutableLiveData;
	}

	/**
	 * fetch data by this method
	 */
	public void doQuery(Query pageQuery) {
		Rx2Apollo.from(mABCoreKitClient.query(pageQuery).watcher())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Response<T>>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Response<T> t) {
						Log.e("onNext=>", "onNext=>" + t.fromCache());
						makeData(t);
					}

					@Override
					public void onError(Throwable e) {
						mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, e.toString(), CoreKitPagedBean.DATA_TYPE_NONE));
					}

					@Override
					public void onComplete() {

					}
				});
	}

	private synchronized void makeData(Response<T> t) {
		if (t != null) {
			List<K> temp = mCoreKitBeanMapper.map(t);

			if (temp == null) {
				mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty.", CoreKitPagedBean.DATA_TYPE_NONE));
				return;
			}

			if (!t.fromCache()) {
				isLoadMore = false;
				isRefresh = false;
			}
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(temp, CoreKitBean.SUCCESS_CODE, "", isLoadMore ? CoreKitPagedBean.DATA_TYPE_LOAD_MORE : CoreKitPagedBean.DATA_TYPE_REFRESH));

			// handle list for repeated data
			for (int i = 0; i < temp.size(); i++) {
				if (isNotInBlocks(temp.get(i))) {
					resultDatas.add(temp.get(i));
				}
			}
			mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(resultDatas, CoreKitBean.SUCCESS_CODE, "", isLoadMore ? CoreKitPagedBean.DATA_TYPE_LOAD_MORE : CoreKitPagedBean.DATA_TYPE_REFRESH));
		} else {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty.", CoreKitPagedBean.DATA_TYPE_NONE));
		}
	}

	/**
	 * load next page data
	 */
	public void loadMore(Query pageQuery) {
		if (isRefresh) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do loadMore when refreshing.", CoreKitPagedBean.DATA_TYPE_NONE));
			return;
		}
		doQuery(pageQuery);
	}

	/**
	 * do refresh
	 * reset query pageInput
	 * reset page
	 */
	public void refresh(Query pageQuery) {
		if (isLoadMore) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do refresh when loadMore.", CoreKitPagedBean.DATA_TYPE_NONE));
			return;
		}
		resultDatas.clear();
		isRefresh = true;
		doQuery(pageQuery);
	}

	/**
	 * @param k
	 * @return if the item k is not in the listDatas
	 */
	private boolean isNotInBlocks(K k) {
		for (int i = 0; i < resultDatas.size(); i++) {
			if (k.equals(resultDatas.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * custom client factory
	 * developer can set a custom ABCoreKitClient by this Factory
	 */
	public static class CustomClientFactory<T> extends ViewModelProvider.NewInstanceFactory {

		private CoreKitBeanMapper mCoreKitBeanMapper;
		private ABCoreKitClient mABCoreKitClient;


		public CustomClientFactory(CoreKitBeanMapper coreKitBeanMapper, ABCoreKitClient aBCoreKitClient) {
			this.mABCoreKitClient = aBCoreKitClient;
			this.mCoreKitBeanMapper = coreKitBeanMapper;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitPagedViewModel(mCoreKitBeanMapper, mABCoreKitClient);
		}
	}

	public static class DefaultFactory<T> extends ViewModelProvider.NewInstanceFactory {

		private CoreKitBeanMapper mCoreKitBeanMapper;
		private Context mContext;

		public DefaultFactory(CoreKitBeanMapper coreKitBeanMapper, Context context) {
			this.mCoreKitBeanMapper = coreKitBeanMapper;
			this.mContext = context;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitPagedViewModel(mCoreKitBeanMapper, mContext);
		}
	}


}
