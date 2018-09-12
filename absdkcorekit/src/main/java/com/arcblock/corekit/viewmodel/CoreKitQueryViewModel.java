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
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoreKitQueryViewModel<T, D> extends ViewModel {


    public static CoreKitQueryViewModel getInstance(FragmentActivity activity, CoreKitQueryViewModel.CustomClientFactory factory) {
        return ViewModelProviders.of(activity, factory).get(factory.getQuery().operationId() + "$" + factory.getQuery().variables().valueMap().hashCode(), CoreKitQueryViewModel.class);
    }

    public static CoreKitQueryViewModel getInstance(FragmentActivity activity, CoreKitQueryViewModel.DefaultFactory factory) {
        return ViewModelProviders.of(activity, factory).get(factory.getQuery().operationId() + "$" + factory.getQuery().variables().valueMap().hashCode(), CoreKitQueryViewModel.class);
    }

    public static CoreKitQueryViewModel getInstance(Fragment fragment, CoreKitQueryViewModel.CustomClientFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(factory.getQuery().operationId() + "$" + factory.getQuery().variables().valueMap().hashCode(), CoreKitQueryViewModel.class);
    }

    public static CoreKitQueryViewModel getInstance(Fragment fragment, CoreKitQueryViewModel.DefaultFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(factory.getQuery().operationId() + "$" + factory.getQuery().variables().valueMap().hashCode(), CoreKitQueryViewModel.class);
    }


    private ABCoreKitClient mABCoreKitClient;
    private MutableLiveData<CoreKitBean<D>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();
    private CoreKitBeanMapperInterface<T, D> mCoreKitBeanMapper;

    public CoreKitQueryViewModel(CoreKitBeanMapperInterface<T, D> mapper, Context context, CoreKitConfig.ApiType apiType) {
        this.mCoreKitBeanMapper = mapper;
        this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context, apiType);
    }

    public CoreKitQueryViewModel(CoreKitBeanMapperInterface<T, D> mapper, ABCoreKitClient aBCoreKitClient) {
        this.mCoreKitBeanMapper = mapper;
        this.mABCoreKitClient = aBCoreKitClient;
    }

    /**
     * @param query
     * @return a livedata object with CoreKitBean
     */
    public MutableLiveData<CoreKitBean<D>> getQueryData(Query query) {
        doFinalQuery(query);
        return mCoreKitBeanMutableLiveData;
    }

    private void doFinalQuery(Query query) {
        if (query == null) {
            mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(null, CoreKitBean.FAIL_CODE, "The query is empty."));
            return;
        }
        Rx2Apollo.from(mABCoreKitClient.query(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T t) {
                        if (t != null) {
                            mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(mCoreKitBeanMapper.map(t), CoreKitBean.SUCCESS_CODE, ""));
                        } else {
                            mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(null, CoreKitBean.FAIL_CODE, "The result is empty."));
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(null, CoreKitBean.FAIL_CODE, e.toString()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        ;
    }

    public static class CustomClientFactory extends ViewModelProvider.NewInstanceFactory {

        private CoreKitBeanMapperInterface mCoreKitBeanMapper;
        private ABCoreKitClient mABCoreKitClient;
        private Query mQuery;


        public CustomClientFactory(Query query, CoreKitBeanMapperInterface coreKitBeanMapper, ABCoreKitClient aBCoreKitClient) {
            this.mABCoreKitClient = aBCoreKitClient;
            this.mCoreKitBeanMapper = coreKitBeanMapper;
            this.mQuery = query;
        }

        public Query getQuery() {
            return mQuery;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CoreKitQueryViewModel(mCoreKitBeanMapper, mABCoreKitClient);
        }
    }

    public static class DefaultFactory extends ViewModelProvider.NewInstanceFactory {

        private CoreKitBeanMapperInterface mCoreKitBeanMapper;
        private Context mContext;
        private CoreKitConfig.ApiType apiType;
        private Query mQuery;

        public DefaultFactory(Query query, CoreKitBeanMapperInterface coreKitBeanMapper, Context context, CoreKitConfig.ApiType apiType) {
            this.mCoreKitBeanMapper = coreKitBeanMapper;
            this.mContext = context;
            this.apiType = apiType;
            this.mQuery = query;
        }

        public Query getQuery() {
            return mQuery;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CoreKitQueryViewModel(mCoreKitBeanMapper, mContext, apiType);
        }
    }


}
