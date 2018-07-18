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
package com.arcblock.corekit.data;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBeanMapper;
import com.arcblock.corekit.bean.PageData;
import com.arcblock.corekit.bean.PageQuery;

import static android.arch.lifecycle.Transformations.switchMap;

public class PageQueryRepositoryImpl<T, D extends PageData<K>, K> implements PageQueryRepository<T,D,K> {

	PageQueryDataFactory<T, D, K> mDataSourceFactory;
	private LiveData<PagedList<K>> datas;

	public PageQueryRepositoryImpl(ABCoreKitClient aBCoreKitClient, PageQuery query, CoreKitBeanMapper<Response<T>, D> coreKitBeanMapper) {
		mDataSourceFactory = new PageQueryDataFactory<>(aBCoreKitClient, query, coreKitBeanMapper);
	}

	@Override
	public LiveData<PagedList<K>> getDatas() {
		PagedList.Config config = new PagedList.Config.Builder()
				.setInitialLoadSizeHint(10)
				.setPageSize(10)
				.build();
		datas = new LivePagedListBuilder(mDataSourceFactory, config)
				.setInitialLoadKey("")
				.build();
		return datas;
	}

	@Override
	public LiveData<DataLoadState> getDataLoadStatus(){
		return switchMap(mDataSourceFactory.datasourceLiveData, new Function<PageQueryDataSource, LiveData<DataLoadState>>() {
			@Override
			public LiveData<DataLoadState> apply(PageQueryDataSource dataSource) {
				return dataSource.loadState;
			}
		});
	}
}
