package com.arcblock.corekit.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBeanMapper;
import com.arcblock.corekit.bean.PageData;
import com.arcblock.corekit.bean.PageQuery;

public class PageQueryDataFactory<T, D extends PageData<K>, K> extends DataSource.Factory<String, K> {

	private ABCoreKitClient mABCoreKitClient;
	private PageQuery mQuery;
	private CoreKitBeanMapper<Response<T>, D> mCoreKitBeanMapper;
	public MutableLiveData<PageQueryDataSource> datasourceLiveData = new MutableLiveData<>();

	public PageQueryDataFactory(ABCoreKitClient aBCoreKitClient, PageQuery query, CoreKitBeanMapper coreKitBeanMapper) {
		mABCoreKitClient = aBCoreKitClient;
		mQuery = query;
		mCoreKitBeanMapper = coreKitBeanMapper;
	}

	@Override
	public DataSource<String, K> create() {
		PageQueryDataSource dataSource = new PageQueryDataSource(mABCoreKitClient, mQuery, mCoreKitBeanMapper);
		datasourceLiveData.postValue(dataSource);
		return dataSource;
	}
}
