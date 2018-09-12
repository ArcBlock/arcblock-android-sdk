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
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;
import com.arcblock.corekit.viewmodel.i.CoreKitInterface;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoreKitViewModel<T, D> extends ViewModel implements CoreKitInterface {


    public static CoreKitViewModel getInstance(FragmentActivity activity, CoreKitViewModel.CustomClientFactory factory) {
        return ViewModelProviders.of(activity, factory).get(factory.getQuery().operationId() + "$" + factory.getQuery().variables().valueMap().hashCode(), CoreKitViewModel.class);
    }

    public static CoreKitViewModel getInstance(FragmentActivity activity, CoreKitViewModel.DefaultFactory factory) {
        return ViewModelProviders.of(activity, factory).get(factory.getQuery().operationId() + "$" + factory.getQuery().variables().valueMap().hashCode(), CoreKitViewModel.class);
    }

    public static CoreKitViewModel getInstance(Fragment fragment, CoreKitViewModel.CustomClientFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(factory.getQuery().operationId() + "$" + factory.getQuery().variables().valueMap().hashCode(), CoreKitViewModel.class);
    }

    public static CoreKitViewModel getInstance(Fragment fragment, CoreKitViewModel.DefaultFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(factory.getQuery().operationId() + "$" + factory.getQuery().variables().valueMap().hashCode(), CoreKitViewModel.class);
    }


    private ABCoreKitClient mABCoreKitClient;
    private MutableLiveData<CoreKitBean<D>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();
    private CoreKitBeanMapperInterface<T, D> mCoreKitBeanMapper;

    public CoreKitViewModel(CoreKitBeanMapperInterface<T, D> mapper, Context context, int apiType) {
        this.mCoreKitBeanMapper = mapper;
        this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context, apiType);
    }

    public CoreKitViewModel(CoreKitBeanMapperInterface<T, D> mapper, ABCoreKitClient aBCoreKitClient) {
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

    /**
     * set a new query then do query
     *
     * @param query
     */
    public void setNewQuery(Query query) {
        doFinalQuery(query);
    }

    @Override
    public void doFinalQuery(Query query) {
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
            return (T) new CoreKitViewModel(mCoreKitBeanMapper, mABCoreKitClient);
        }
    }

    public static class DefaultFactory extends ViewModelProvider.NewInstanceFactory {

        private CoreKitBeanMapperInterface mCoreKitBeanMapper;
        private Context mContext;
        private int apiType;
        private Query mQuery;

        public DefaultFactory(Query query, CoreKitBeanMapperInterface coreKitBeanMapper, Context context, int apiType) {
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
            return (T) new CoreKitViewModel(mCoreKitBeanMapper, mContext, apiType);
        }
    }


}
