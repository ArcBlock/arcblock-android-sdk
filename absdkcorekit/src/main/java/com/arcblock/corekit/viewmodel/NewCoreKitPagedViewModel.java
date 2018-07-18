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

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBeanMapper;
import com.arcblock.corekit.bean.PageData;
import com.arcblock.corekit.bean.PageQuery;
import com.arcblock.corekit.data.DataLoadState;
import com.arcblock.corekit.data.PageQueryRepository;
import com.arcblock.corekit.data.PageQueryRepositoryImpl;

public class NewCoreKitPagedViewModel<T, D extends PageData<K>, K> extends AndroidViewModel {

	private PageQueryRepository<T,D,K> repository;

	public NewCoreKitPagedViewModel(@NonNull Application application, ABCoreKitClient aBCoreKitClient, PageQuery query, CoreKitBeanMapper<Response<T>, D> coreKitBeanMapper) {
		super(application);
		repository = new PageQueryRepositoryImpl<>(aBCoreKitClient, query, coreKitBeanMapper);
	}

	public NewCoreKitPagedViewModel(@NonNull Application application, PageQuery query, CoreKitBeanMapper<Response<T>, D> coreKitBeanMapper) {
		super(application);
		repository = new PageQueryRepositoryImpl(ABCoreKitClient.defaultInstance(application), query, coreKitBeanMapper);
	}

	public LiveData<PagedList<K>> getProducts() {
		return repository.getDatas();
	}

	public LiveData<DataLoadState> dataLoadStatus() {
		return repository.getDataLoadStatus();
	}

	/**
	 * custom client factory
	 * developer can set a custom ABCoreKitClient by this Factory
	 */
	public static class CustomClientFactory extends ViewModelProvider.NewInstanceFactory {

		private Application mApplication;
		private ABCoreKitClient mABCoreKitClient;
		private PageQuery mPageQuery;
		private CoreKitBeanMapper mCoreKitBeanMapper;


		public CustomClientFactory(Application application, ABCoreKitClient aBCoreKitClient, PageQuery query, CoreKitBeanMapper coreKitBeanMapper) {
			this.mApplication = application;
			this.mABCoreKitClient = aBCoreKitClient;
			this.mPageQuery = query;
			this.mCoreKitBeanMapper = coreKitBeanMapper;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new NewCoreKitPagedViewModel(mApplication, mABCoreKitClient, mPageQuery, mCoreKitBeanMapper);
		}
	}

	public static class DefaultFactory extends ViewModelProvider.NewInstanceFactory {

		private Application mApplication;
		private PageQuery mPageQuery;
		private CoreKitBeanMapper mCoreKitBeanMapper;

		public DefaultFactory(Application application, PageQuery pageQuery, CoreKitBeanMapper coreKitBeanMapper) {
			this.mApplication = application;
			this.mPageQuery = pageQuery;
			this.mCoreKitBeanMapper = coreKitBeanMapper;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new NewCoreKitPagedViewModel(mApplication, mPageQuery, mCoreKitBeanMapper);
		}
	}


}
