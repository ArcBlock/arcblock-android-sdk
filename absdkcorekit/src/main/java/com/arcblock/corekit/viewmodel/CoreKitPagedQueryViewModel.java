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
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.bean.CoreKitPagedBean;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;
import com.arcblock.corekit.viewmodel.i.CoreKitPagedHelperInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoreKitPagedQueryViewModel<T, K> extends ViewModel {

    public static CoreKitPagedQueryViewModel getInstance(FragmentActivity activity, CoreKitPagedQueryViewModel.CustomClientFactory factory) {
        return ViewModelProviders.of(activity, factory).get(factory.getOperationId(), CoreKitPagedQueryViewModel.class);
    }

    public static CoreKitPagedQueryViewModel getInstance(FragmentActivity activity, CoreKitPagedQueryViewModel.DefaultFactory factory) {
        return ViewModelProviders.of(activity, factory).get(factory.getOperationId(), CoreKitPagedQueryViewModel.class);
    }

    public static CoreKitPagedQueryViewModel getInstance(Fragment fragment, CoreKitPagedQueryViewModel.CustomClientFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(factory.getOperationId(), CoreKitPagedQueryViewModel.class);
    }

    public static CoreKitPagedQueryViewModel getInstance(Fragment fragment, CoreKitPagedQueryViewModel.DefaultFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(factory.getOperationId(), CoreKitPagedQueryViewModel.class);
    }

    private ABCoreKitClient mABCoreKitClient;
    private MutableLiveData<CoreKitPagedBean<List<K>>> mCleanDatasMutableLiveData = new MutableLiveData<>();
    private CoreKitBeanMapperInterface<Response<T>, List<K>> mCoreKitBeanMapper;
    private boolean isLoading;
    private List<K> resultDatas = new ArrayList<>();
    private CoreKitPagedHelperInterface mCoreKitPagedHelper;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public CoreKitPagedQueryViewModel(CoreKitBeanMapperInterface<Response<T>, List<K>> mapper, CoreKitPagedHelperInterface coreKitPagedHelper, Context context, CoreKitConfig.ApiType apiType) {
        this.mCoreKitBeanMapper = mapper;
        this.mCoreKitPagedHelper = coreKitPagedHelper;
        this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context, apiType);

    }

    public CoreKitPagedQueryViewModel(CoreKitBeanMapperInterface<Response<T>, List<K>> mapper, CoreKitPagedHelperInterface coreKitPagedHelper, ABCoreKitClient aBCoreKitClient) {
        this.mCoreKitBeanMapper = mapper;
        this.mCoreKitPagedHelper = coreKitPagedHelper;
        this.mABCoreKitClient = aBCoreKitClient;
    }

    /**
     * @return a livedata object with CoreKitPagedBean and have no repeat data
     */
    public MutableLiveData<CoreKitPagedBean<List<K>>> getCleanQueryData() {
        if (mCoreKitPagedHelper == null) {
            throw new RuntimeException("CoreKitPagedHelperInterface must be init.");
        }
        doFinalQuery(mCoreKitPagedHelper.getInitialQuery());
        return mCleanDatasMutableLiveData;
    }

    /**
     * fetch data by this method
     */
    private void doFinalQuery(Query query) {
        if (mCoreKitPagedHelper == null && query == null) {
            mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The query is empty."));
        }
        Rx2Apollo.from(mABCoreKitClient.query(query).watcher())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<T>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<T> t) {
                        handleData(t);
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
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
                mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty."));
                return;
            }
            // handle list for repeated data
            for (int i = 0; i < temp.size(); i++) {
                if (isNotInBlocks(temp.get(i))) {
                    resultDatas.add(temp.get(i));
                }
            }
            mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(resultDatas, CoreKitBean.SUCCESS_CODE, ""));
        } else {
            mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "The result is empty."));
        }
    }

    /**
     * load next page data
     */
    public void loadMore() {
        if (isLoading) {
            mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do loadMore when loading."));
            return;
        }
        if (mCoreKitPagedHelper == null) {
            throw new RuntimeException("CoreKitPagedHelperInterface must be init.");
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
            mCleanDatasMutableLiveData.postValue(new CoreKitPagedBean(null, CoreKitBean.FAIL_CODE, "Cannot do refresh when loading."));
            return;
        }
        if (mCoreKitPagedHelper == null) {
            throw new RuntimeException("CoreKitPagedHelperInterface must be init.");
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

    @Override
    protected void onCleared() {
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
        super.onCleared();
    }

    /**
     * custom client factory
     * developer can set a custom ABCoreKitClient by this Factory
     */
    public static class CustomClientFactory extends ViewModelProvider.NewInstanceFactory {

        private CoreKitBeanMapperInterface mCoreKitBeanMapper;
        private CoreKitPagedHelperInterface mCoreKitPagedHelper;
        private ABCoreKitClient mABCoreKitClient;
        private String operationId;

        public CustomClientFactory(CoreKitBeanMapperInterface coreKitBeanMapper, CoreKitPagedHelperInterface coreKitPagedHelper, ABCoreKitClient aBCoreKitClient) {
            this.mABCoreKitClient = aBCoreKitClient;
            this.mCoreKitPagedHelper = coreKitPagedHelper;
            this.mCoreKitBeanMapper = coreKitBeanMapper;
            this.operationId = mCoreKitPagedHelper != null && mCoreKitPagedHelper.getInitialQuery() != null ? mCoreKitPagedHelper.getInitialQuery().operationId() : UUID.randomUUID().toString();
        }

        public String getOperationId() {
            return operationId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CoreKitPagedQueryViewModel(mCoreKitBeanMapper, mCoreKitPagedHelper, mABCoreKitClient);
        }
    }

    public static class DefaultFactory extends ViewModelProvider.NewInstanceFactory {

        private CoreKitBeanMapperInterface mCoreKitBeanMapper;
        private CoreKitPagedHelperInterface mCoreKitPagedHelper;
        private Context mContext;
        private CoreKitConfig.ApiType apiType;
        private String operationId;

        public DefaultFactory(CoreKitBeanMapperInterface coreKitBeanMapper, CoreKitPagedHelperInterface coreKitPagedHelper, Context context, CoreKitConfig.ApiType apiType) {
            this.mCoreKitBeanMapper = coreKitBeanMapper;
            this.mCoreKitPagedHelper = coreKitPagedHelper;
            this.mContext = context;
            this.apiType = apiType;
            this.operationId = mCoreKitPagedHelper != null && mCoreKitPagedHelper.getInitialQuery() != null ? mCoreKitPagedHelper.getInitialQuery().operationId() : UUID.randomUUID().toString();
        }

        public String getOperationId() {
            return operationId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CoreKitPagedQueryViewModel(mCoreKitBeanMapper, mCoreKitPagedHelper, mContext, apiType);
        }
    }


}
