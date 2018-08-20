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

import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.bean.CoreKitPagedBean;
import com.arcblock.corekit.utils.CoreKitBeanMapper;
import com.arcblock.corekit.utils.CoreKitPagedHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoreKitPagedViewModel<T, K> extends ViewModel implements CoreKitInterface {

	private ABCoreKitClient mABCoreKitClient;
	private MutableLiveData<CoreKitPagedBean<List<K>>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();
	private MutableLiveData<CoreKitPagedBean<List<K>>> mCleanDatasMutableLiveData = new MutableLiveData<>();
	private CoreKitBeanMapper<Response<T>, List<K>> mCoreKitBeanMapper;
	private boolean isLoading;
	private List<K> resultDatas = new ArrayList<>();
	private CoreKitPagedHelper mCoreKitPagedHelper;

	public CoreKitPagedViewModel(CoreKitBeanMapper<Response<T>, List<K>> mapper, CoreKitPagedHelper coreKitPagedHelper, Context context, int apiType) {
		this.mCoreKitBeanMapper = mapper;
		this.mCoreKitPagedHelper = coreKitPagedHelper;
		this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context, apiType);

	}

	public CoreKitPagedViewModel(CoreKitBeanMapper<Response<T>, List<K>> mapper, CoreKitPagedHelper coreKitPagedHelper, ABCoreKitClient aBCoreKitClient) {
		this.mCoreKitBeanMapper = mapper;
		this.mCoreKitPagedHelper = coreKitPagedHelper;
		this.mABCoreKitClient = aBCoreKitClient;
	}

	/**
	 * @return a livedata object with CoreKitPagedBean
	 */
	public MutableLiveData<CoreKitPagedBean<List<K>>> getQueryData() {
		if (mCoreKitPagedHelper == null) {
			throw new RuntimeException("CoreKitPagedHelper must be init.");
		}
		doFinalQuery(mCoreKitPagedHelper.getInitialQuery());
		return mCoreKitBeanMutableLiveData;
	}

	/**
	 * @return a livedata object with CoreKitPagedBean and have no repeat data
	 */
	public MutableLiveData<CoreKitPagedBean<List<K>>> getCleanQueryData() {
		if (mCoreKitPagedHelper == null) {
			throw new RuntimeException("CoreKitPagedHelper must be init.");
		}
		doFinalQuery(mCoreKitPagedHelper.getInitialQuery());
		return mCleanDatasMutableLiveData;
	}

	/**
	 * fetch data by this method
	 */
	@Override
	public void doFinalQuery(Query query) {
		if (mCoreKitPagedHelper == null && query == null) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The query is empty."));
		}
		Rx2Apollo.from(mABCoreKitClient.query(query).watcher())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Response<T>>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Response<T> t) {
						handleData(t);
						isLoading = false;
					}

					@Override
					public void onError(Throwable e) {
						isLoading = false;
						mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, e.toString()));
						mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, e.toString()));
					}

					@Override
					public void onComplete() {

					}
				});
	}

	/**
	 * handle response t to the data which are we want.
	 *
	 * @param t
	 */
	private synchronized void handleData(Response<T> t) {
		if (t != null) {
			List<K> temp = mCoreKitBeanMapper.map(t);
			if (temp == null) {
				mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty."));
				mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty."));
				return;
			}
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(temp, CoreKitBean.SUCCESS_CODE, ""));
			// handle list for repeated data
			for (int i = 0; i < temp.size(); i++) {
				if (isNotInBlocks(temp.get(i))) {
					resultDatas.add(temp.get(i));
				}
			}
			mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(resultDatas, CoreKitBean.SUCCESS_CODE, ""));
		} else {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty."));
			mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty."));
		}
	}

	/**
	 * load next page data
	 */
	public void loadMore() {
		if (isLoading) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do loadMore when loading."));
			mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do loadMore when loading."));
			return;
		}
		if (mCoreKitPagedHelper == null) {
			throw new RuntimeException("CoreKitPagedHelper must be init.");
		}
		isLoading = true;
		doFinalQuery(mCoreKitPagedHelper.getLoadMoreQuery());
	}

	/**
	 * do refresh
	 * reset query pageInput
	 * reset page
	 */
	public void refresh() {
		if (isLoading) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do refresh when loading."));
			mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do refresh when loading."));
			return;
		}
		if (mCoreKitPagedHelper == null) {
			throw new RuntimeException("CoreKitPagedHelper must be init.");
		}
		resultDatas.clear();
		isLoading = true;
		mCoreKitPagedHelper.setHasMoreForRefresh();
		doFinalQuery(mCoreKitPagedHelper.getRefreshQuery());
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
	public static class CustomClientFactory extends ViewModelProvider.NewInstanceFactory {

		private CoreKitBeanMapper mCoreKitBeanMapper;
		private CoreKitPagedHelper mCoreKitPagedHelper;
		private ABCoreKitClient mABCoreKitClient;

		public CustomClientFactory(CoreKitBeanMapper coreKitBeanMapper, CoreKitPagedHelper coreKitPagedHelper, ABCoreKitClient aBCoreKitClient) {
			this.mABCoreKitClient = aBCoreKitClient;
			this.mCoreKitPagedHelper = coreKitPagedHelper;
			this.mCoreKitBeanMapper = coreKitBeanMapper;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitPagedViewModel(mCoreKitBeanMapper, mCoreKitPagedHelper, mABCoreKitClient);
		}
	}

	public static class DefaultFactory extends ViewModelProvider.NewInstanceFactory {

		private CoreKitBeanMapper mCoreKitBeanMapper;
		private CoreKitPagedHelper mCoreKitPagedHelper;
		private Context mContext;
		private int apiType;

		public DefaultFactory(CoreKitBeanMapper coreKitBeanMapper, CoreKitPagedHelper coreKitPagedHelper, Context context, int apiType) {
			this.mCoreKitBeanMapper = coreKitBeanMapper;
			this.mCoreKitPagedHelper = coreKitPagedHelper;
			this.mContext = context;
			this.apiType = apiType;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitPagedViewModel(mCoreKitBeanMapper, mCoreKitPagedHelper, mContext, apiType);
		}
	}


}
