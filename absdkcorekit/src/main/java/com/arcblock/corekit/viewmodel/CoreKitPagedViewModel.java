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

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.bean.CoreKitBeanMapper;
import com.arcblock.corekit.bean.CoreKitPagedBean;
import com.arcblock.corekit.bean.PageData;
import com.arcblock.corekit.bean.PageInput;
import com.arcblock.corekit.bean.PageQuery;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoreKitPagedViewModel<T, D extends PageData<K>, K> extends ViewModel {

	private ABCoreKitClient mABCoreKitClient;
	private MutableLiveData<CoreKitPagedBean<List<K>>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();
	private CoreKitBeanMapper<Response<T>, D> mCoreKitBeanMapper;
	private PageData.Page mPage;
	private PageInput mPageInput;
	private PageQuery mQuery;
	private boolean isRefresh;
	private boolean isLoadMore;
	private boolean isLoading;
	private boolean haveMore = true;
	private LinkedHashMap<String, List<K>> mHashMap = new LinkedHashMap<>();

	public CoreKitPagedViewModel(CoreKitBeanMapper<Response<T>, D> mapper, Context context) {
		this.mCoreKitBeanMapper = mapper;
		this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context);
	}

	public CoreKitPagedViewModel(CoreKitBeanMapper<Response<T>, D> mapper, ABCoreKitClient aBCoreKitClient) {
		this.mCoreKitBeanMapper = mapper;
		this.mABCoreKitClient = aBCoreKitClient;
	}

	/**
	 * @param query
	 * @return a livedata object with D
	 */
	public MutableLiveData<CoreKitPagedBean<List<K>>> getQueryData(PageQuery query) {
		this.mQuery = query;
		doQuery();
		return mCoreKitBeanMutableLiveData;
	}

	/**
	 * fetch data by this method
	 */
	public void doQuery() {
		if(isLoading){
			return;
		}
		isLoading = true;
		Rx2Apollo.from(mABCoreKitClient.query(mQuery).watcher())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Response<T>>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Response<T> t) {
						Log.e("onNext=>", "onNext=>" + t.fromCache());
						if(t.fromCache()){
							makeData(t);
						}
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
			D temp = mCoreKitBeanMapper.map(t);

			if (!t.fromCache()) {
				isLoadMore = false;
				isRefresh = false;
				isLoading = false;
			}

			String currentCursor = "empty_key";
			if (mPage != null) {
				currentCursor = mPage.getCursor();
			}

			if (temp.getData() != null && !temp.getData().isEmpty() && temp.getPage() != null && !t.fromCache()) {
				if (temp.getPage().isNext()) {
					mPage = temp.getPage();
				}
				haveMore = temp.getPage().isNext();
			}

			mHashMap.put(currentCursor, temp.data);
			List<T> tempArray = new ArrayList<>();

			List<String> keys = new ArrayList<>();
			for (String key : mHashMap.keySet()) {
				keys.add(key);
			}
			//Collections.reverse(keys);
			for (String key : keys) {
				Log.e("key=>", "key=>" + key);
				tempArray.addAll((List<T>) mHashMap.get(key));
			}
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(tempArray, CoreKitBean.SUCCESS_CODE, "", isLoadMore ? CoreKitPagedBean.DATA_TYPE_LOAD_MORE : CoreKitPagedBean.DATA_TYPE_REFRESH));
		} else {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty.", CoreKitPagedBean.DATA_TYPE_NONE));
		}
	}

	/**
	 * load next page data
	 */
	public void loadMore() {
		if (isLoading) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do loadMore when loading.", CoreKitPagedBean.DATA_TYPE_NONE));
			return;
		}
		if (isRefresh) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do loadMore when refreshing.", CoreKitPagedBean.DATA_TYPE_NONE));
			return;
		}
		if (!haveMore) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "do not have more data.", CoreKitPagedBean.DATA_TYPE_NONE));
			return;
		}
		if (mPage != null) {
			mPageInput = PageInput.builder().cursor(mPage.getCursor()).build();
			Log.e("Cursor=>", "Cursor=>" + mPage.getCursor());
		}
		if (mQuery != null) {
			isLoadMore = true;
			mQuery.setPageInput(Input.optional(mPageInput));
			doQuery();
		} else {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The query is null.", CoreKitPagedBean.DATA_TYPE_NONE));
		}
	}

	/**
	 * @return is have next page
	 */
	public boolean isNext() {
		return haveMore;
	}

	/**
	 * @return is loading data
	 */
	public boolean isLoading(){
		return isLoading;
	}

	/**
	 * do refresh
	 * reset query pageInput
	 * reset page
	 */
	public void refresh() {
		if (isLoadMore) {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do refresh when loadMore.", CoreKitPagedBean.DATA_TYPE_NONE));
			return;
		}
		if (mQuery != null) {
			isRefresh = true;
			mQuery.setPageInput(Input.optional(null));
			mPage = null;
			mHashMap.clear();
			doQuery();
		} else {
			mCoreKitBeanMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The query is null.", CoreKitPagedBean.DATA_TYPE_NONE));
		}
	}

	/**
	 * custom client factory
	 * developer can set a custom ABCoreKitClient by this Factory
	 */
	public static class CustomClientFactory extends ViewModelProvider.NewInstanceFactory {

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

	public static class DefaultFactory extends ViewModelProvider.NewInstanceFactory {

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
